#!/usr/bin/env python3
"""Build inventory and worn armor textures from the generated source atlases."""

from __future__ import annotations

import argparse
from collections import deque
from pathlib import Path

from PIL import Image, ImageDraw


ARMOR_NAMES = [
    "crystalline_abyss_helmet",
    "crystalline_abyss_chestplate",
    "crystalline_abyss_leggings",
    "crystalline_abyss_boots",
    "singularity_abyss_helmet",
    "singularity_abyss_chestplate",
    "singularity_abyss_leggings",
    "singularity_abyss_boots",
]

MATERIAL_NAMES = [
    "crystallization_residue",
    "crystalline_armor_plate",
    "singularity_residue",
    "singularity_armor_plate",
    "abyss_armor_module_frame",
    "verdant_armor_module",
    "cinder_armor_module",
    "frost_armor_module",
    "flesh_armor_module",
    "void_armor_module",
]


def is_background(pixel: tuple[int, int, int, int]) -> bool:
    red, green, blue, _ = pixel
    return min(red, green, blue) > 218 and max(red, green, blue) - min(red, green, blue) < 20


def remove_checkerboard(image: Image.Image) -> Image.Image:
    image = image.convert("RGBA")
    width, height = image.size
    candidates = {index for index, pixel in enumerate(image.getdata()) if is_background(pixel)}
    visited: set[int] = set()

    for start in tuple(candidates):
        if start in visited:
            continue
        queue = deque([start])
        component: list[int] = []
        touches_edge = False
        visited.add(start)
        while queue:
            index = queue.popleft()
            component.append(index)
            x, y = index % width, index // width
            touches_edge |= x == 0 or y == 0 or x == width - 1 or y == height - 1
            for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
                next_index = ny * width + nx
                if 0 <= nx < width and 0 <= ny < height and next_index in candidates and next_index not in visited:
                    visited.add(next_index)
                    queue.append(next_index)
        if touches_edge or len(component) >= 900:
            for index in component:
                x, y = index % width, index // width
                red, green, blue, _ = image.getpixel((x, y))
                image.putpixel((x, y), (red, green, blue, 0))
    return image


def split_atlas(source: Path, columns: int, rows: int, names: list[str], output: Path) -> None:
    atlas = Image.open(source).convert("RGB")
    output.mkdir(parents=True, exist_ok=True)
    for index, name in enumerate(names):
        column, row = index % columns, index // columns
        x0 = round(column * atlas.width / columns)
        x1 = round((column + 1) * atlas.width / columns)
        y0 = round(row * atlas.height / rows)
        y1 = round((row + 1) * atlas.height / rows)
        cell = remove_checkerboard(atlas.crop((x0, y0, x1, y1)))
        bounds = cell.getbbox()
        if bounds is None:
            raise RuntimeError(f"No visible pixels found for {name}")
        sprite = cell.crop(bounds)
        sprite.thumbnail((30, 30), Image.Resampling.NEAREST)
        icon = Image.new("RGBA", (32, 32))
        icon.alpha_composite(sprite, ((32 - sprite.width) // 2, (32 - sprite.height) // 2))
        icon.save(output / f"{name}.png")


def worn_texture(path: Path, base: tuple[int, int, int], accent: tuple[int, int, int], void: bool,
                 layer: int) -> None:
    image = Image.new("RGBA", (128, 64), (*base, 255))
    draw = ImageDraw.Draw(image)
    shadow = tuple(max(0, channel - 13) for channel in base)
    light = tuple(min(255, channel + 25) for channel in base)

    # Dense forged panels form the base of every cube face used by the custom model.
    for y in range(64):
        for x in range(128):
            checker = ((x // 3) + (y // 3)) % 2
            color = light if checker else base
            if x % 8 == 0 or y % 8 == 0:
                color = shadow
            image.putpixel((x, y), (*color, 255))

    panel_regions = ((0, 0, 32, 16), (0, 16, 16, 32), (16, 16, 40, 32),
                     (40, 16, 56, 32), (16, 48, 32, 64), (32, 48, 48, 64),
                     (64, 0, 128, 16), (64, 16, 128, 44), (64, 44, 128, 64))
    for left, top, right, bottom in panel_regions:
        draw.rectangle((left, top, right - 1, bottom - 1), outline=(*shadow, 255))
        draw.line((left + 1, top + 1, right - 2, top + 1), fill=(*light, 255))

    secondary = (126, 84, 238) if not void else (218, 222, 232)
    for offset in (2, 18, 34, 66, 82, 98, 114):
        draw.line((offset, 2, offset + 7, 9), fill=(*secondary, 255), width=1)
        draw.line((offset + 7, 9, offset + 2, 14), fill=(*accent, 255), width=2)
    for offset in (2, 18, 34, 66, 86, 106):
        draw.line((offset, 20, offset + 7, 27), fill=(*accent, 255), width=2)
        draw.point((offset + 8, 28), fill=(224, 250, 255, 255))
    for offset in (66, 82, 98, 114):
        draw.line((offset, 47, offset + 6, 54), fill=(*secondary, 255), width=2)
        draw.line((offset + 6, 54, offset + 2, 62), fill=(*accent, 255), width=1)

    core_positions = ((12, 7), (28, 24), (72, 7), (106, 23), (112, 48))
    for x, y in core_positions:
        if void:
            draw.rectangle((x - 4, y - 4, x + 4, y + 4), fill=(218, 222, 232, 255))
            draw.rectangle((x - 3, y - 3, x + 3, y + 3), fill=(76, 24, 86, 255))
            draw.rectangle((x - 1, y - 2, x + 1, y + 2), fill=(*accent, 255))
            draw.point((x, y), fill=(255, 194, 244, 255))
        else:
            draw.polygon(((x, y - 5), (x + 4, y), (x, y + 5), (x - 4, y)),
                         fill=(9, 92, 122, 255))
            draw.polygon(((x, y - 4), (x + 2, y), (x, y + 4), (x - 2, y)), fill=(*accent, 255))
            draw.line((x - 1, y - 3, x - 1, y + 1), fill=(215, 255, 255, 255))

    # Layer two is sampled by leggings, so its lower armor panels receive a brighter edge treatment.
    if layer == 2:
        for x in (2, 18, 66, 90, 110):
            draw.line((x, 34, x + 8, 42), fill=(*accent, 255), width=2)
            draw.rectangle((x + 2, 56, x + 8, 62), outline=(*secondary, 255))
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("armor_atlas", type=Path)
    parser.add_argument("material_atlas", type=Path)
    parser.add_argument("resource_root", type=Path)
    args = parser.parse_args()

    item_output = args.resource_root / "textures/item"
    split_atlas(args.armor_atlas, 4, 2, ARMOR_NAMES, item_output)
    split_atlas(args.material_atlas, 5, 2, MATERIAL_NAMES, item_output)

    armor_output = args.resource_root / "textures/models/armor"
    for layer in (1, 2):
        worn_texture(armor_output / f"crystalline_abyss_layer_{layer}.png",
                     (29, 32, 48), (35, 224, 246), False, layer)
        worn_texture(armor_output / f"singularity_abyss_layer_{layer}.png",
                     (20, 18, 31), (232, 35, 184), True, layer)


if __name__ == "__main__":
    main()
