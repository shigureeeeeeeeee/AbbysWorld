#!/usr/bin/env python3
"""abyssworld のアイテム・ブロック・エンティティ用アセットを生成する。"""
import json
import math
import os
import random
import struct
import zlib

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources")
ASSETS = os.path.join(ROOT, "assets", "abyssworld")


def write_png(path, pixels):
    """pixels: list of rows containing (r,g,b,a) tuples."""
    height = len(pixels)
    width = len(pixels[0])
    raw = b""
    for y in range(height):
        raw += b"\x00"
        for x in range(width):
            raw += struct.pack("4B", *pixels[y][x])

    def chunk(tag, data):
        c = struct.pack(">I", len(data)) + tag + data
        return c + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw))
    png += chunk(b"IEND", b"")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)


def clamp(v):
    return max(0, min(255, int(v)))


def shade(color, factor):
    return tuple(clamp(c * factor) for c in color[:3]) + (255,)


def gem_texture(base, seed):
    """結晶・核系: 中央にひし形の輝き"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    cx, cy = 7.5, 7.5
    for y in range(16):
        for x in range(16):
            d = abs(x - cx) + abs(y - cy)
            if d <= 6.5:
                f = 1.25 - d * 0.09 + rng.uniform(-0.05, 0.05)
                px[y][x] = shade(base, f)
    for x, y in [(7, 5), (8, 6), (6, 7)]:
        px[y][x] = shade((255, 255, 255), 0.95)
    return px


def ingot_texture(base, seed):
    """インゴット系: 横長の板"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for y in range(5, 12):
        for x in range(2, 14):
            f = 1.1 - (y - 5) * 0.06 + rng.uniform(-0.04, 0.04)
            px[y][x] = shade(base, f)
    for x in range(2, 14):
        px[5][x] = shade(base, 1.35)
        px[11][x] = shade(base, 0.6)
    return px


def blob_texture(base, seed):
    """有機物・液体系: 不定形の塊"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    cx, cy = 7.5, 8.0
    for y in range(16):
        for x in range(16):
            d = ((x - cx) ** 2 + (y - cy) ** 2) ** 0.5
            if d <= 5.5 + rng.uniform(-1.0, 1.0):
                f = 1.15 - d * 0.08 + rng.uniform(-0.08, 0.08)
                px[y][x] = shade(base, f)
    return px


def relic_texture(base, seed):
    """原初神器: 縦の刃と柄"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    for i in range(11):
        x = 12 - i
        y = 3 + i
        for dx in range(-1, 2):
            if 0 <= x + dx < 16:
                f = 1.3 - abs(dx) * 0.35 + rng.uniform(-0.05, 0.05)
                px[y][x + dx] = shade(base, f)
    for i in range(4):
        x, y = 3 - min(i, 1), 12 + i
        if 0 <= x < 16 and 0 <= y < 16:
            px[y][x] = (72, 48, 24, 255)
    px[3][12] = (255, 255, 255, 255)
    px[4][11] = shade((255, 230, 160), 1.0)
    return px


def key_texture(base, seed):
    """鍵: 上部のリングと下部の刃"""
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    # リング(上部)
    for y in range(2, 7):
        for x in range(5, 11):
            d = ((x - 7.5) ** 2 + (y - 4) ** 2) ** 0.5
            if 1.5 <= d <= 2.8:
                px[y][x] = shade(base, 1.1)
    # 軸
    for y in range(7, 14):
        px[y][7] = shade(base, 1.0)
        px[y][8] = shade(base, 0.8)
    # 刃(下部の歯)
    for x in (9, 10):
        px[12][x] = shade(base, 1.05)
    for x in (9,):
        px[10][x] = shade(base, 1.05)
    px[3][7] = (255, 255, 255, 255)
    return px


def spellbook_texture(base, seed):
    """呪文書: 金属縁と発光する深淵ルーンを持つ厚い本。"""
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    dark = shade(base, 0.48)
    edge = shade(base, 1.25)
    for y in range(2, 14):
        for x in range(2, 14):
            px[y][x] = base + (255,)
    for x in range(2, 14):
        px[2][x] = edge
        px[13][x] = dark
    for y in range(2, 14):
        px[y][2] = edge
        px[y][13] = dark
    for y in range(3, 13):
        px[y][4] = (72, 56, 82, 255)
    rune = (92, 235, 230, 255)
    for x, y in ((8, 4), (7, 5), (9, 5), (6, 6), (10, 6),
                 (7, 7), (9, 7), (8, 8), (8, 9), (7, 10), (9, 10), (8, 11)):
        px[y][x] = rune
    px[5][8] = (235, 255, 255, 255)
    return px


def glyph_texture(base, seed):
    """差し替え可能な呪文グリフ: カテゴリごとに異なるルーン構造。"""
    rng = random.Random(seed)
    px = [[(0, 0, 0, 0)] * 16 for _ in range(16)]
    frame = (46, 37, 58, 255)
    rim = (118, 96, 142, 255)
    accent = base + (255,)
    bright = shade(base, 1.45)
    for y in range(2, 14):
        for x in range(2, 14):
            if 4 <= x + y <= 26 and -10 <= x - y <= 10:
                px[y][x] = frame
    for x, y in ((5, 2), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
                 (5, 13), (6, 13), (7, 13), (8, 13), (9, 13), (10, 13),
                 (2, 5), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
                 (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10)):
        px[y][x] = rim
    if "form_" in seed:
        for x, y in ((7, 4), (6, 5), (8, 5), (5, 6), (9, 6),
                     (7, 7), (7, 8), (7, 9), (6, 10), (8, 10), (5, 11), (9, 11)):
            px[y][x] = accent
    elif "effect_" in seed:
        for x, y in ((7, 4), (6, 5), (8, 5), (5, 6), (9, 6),
                     (4, 7), (7, 7), (10, 7), (5, 8), (9, 8),
                     (6, 9), (8, 9), (7, 10)):
            px[y][x] = accent
    else:
        for x, y in ((5, 4), (9, 4), (4, 5), (10, 5), (7, 5),
                     (5, 7), (7, 7), (9, 7), (4, 9), (10, 9),
                     (5, 10), (9, 10)):
            px[y][x] = accent
    for _ in range(3):
        x, y = rng.randint(5, 9), rng.randint(5, 9)
        if px[y][x][3]:
            px[y][x] = bright
    return px


def block_texture(base, seed, noisy=True, spots=None):
    """フルブロック用 16x16"""
    rng = random.Random(seed)
    px = [[None] * 16 for _ in range(16)]
    for y in range(16):
        for x in range(16):
            f = 1.0 + (rng.uniform(-0.12, 0.12) if noisy else 0)
            px[y][x] = shade(base, f)
    if spots:
        for _ in range(14):
            x, y = rng.randint(1, 14), rng.randint(1, 14)
            px[y][x] = shade(spots, 1.1)
            px[y][min(x + 1, 15)] = shade(spots, 0.85)
    return px


def grove_seal_texture(base, seed):
    """忘却樹海の封印: 石枠、金の根、青緑に発光する中心紋。"""
    rng = random.Random(seed)
    px = [[shade(base, 0.72 + rng.uniform(-0.08, 0.08)) for _ in range(16)] for _ in range(16)]
    stone = (39, 55, 45, 255)
    gold = (184, 145, 55, 255)
    glow = (76, 232, 168, 255)
    for i in range(16):
        px[0][i] = stone
        px[15][i] = stone
        px[i][0] = stone
        px[i][15] = stone
    for x, y in ((3, 2), (4, 3), (5, 4), (10, 4), (11, 3), (12, 2),
                 (2, 7), (3, 8), (4, 9), (11, 9), (12, 8), (13, 7),
                 (4, 13), (5, 12), (10, 12), (11, 13)):
        px[y][x] = gold
    for x, y in ((7, 3), (8, 3), (6, 4), (9, 4), (5, 6), (10, 6),
                 (6, 7), (7, 7), (8, 7), (9, 7), (7, 8), (8, 8),
                 (6, 9), (9, 9), (5, 11), (10, 11), (7, 12), (8, 12)):
        px[y][x] = glow
    px[7][7] = (226, 255, 220, 255)
    px[7][8] = (226, 255, 220, 255)
    return px


def entity_texture(width, height, base, accent, seed):
    """敵モブ用: モデルUV全体を埋める専用パレットのテクスチャ。"""
    rng = random.Random(seed)
    px = [[None] * width for _ in range(height)]
    for y in range(height):
        for x in range(width):
            band = 0.08 if (x // 8 + y // 8) % 2 == 0 else -0.03
            edge = 0.0
            if x % 16 in (0, 15) or y % 16 in (0, 15):
                edge = -0.18
            f = 0.92 + band + edge + rng.uniform(-0.10, 0.10)
            px[y][x] = shade(base, f)

    for _ in range(max(16, width * height // 128)):
        cx = rng.randrange(width)
        cy = rng.randrange(height)
        radius = rng.randint(1, 3)
        for yy in range(max(0, cy - radius), min(height, cy + radius + 1)):
            for xx in range(max(0, cx - radius), min(width, cx + radius + 1)):
                d = abs(xx - cx) + abs(yy - cy)
                if d <= radius:
                    px[yy][xx] = shade(accent, 1.2 - d * 0.18)

    for y in range(2, height, 13):
        for x in range(0, width, 2):
            if rng.random() < 0.55:
                px[y][x] = shade(accent, 1.35)
    return px


def abyss_hound_texture(glow=False):
    """深淵猟犬専用256x128 UV。黒い甲殻、血肉、骨、発光亀裂を別レイヤー化する。"""
    width, height = 256, 128
    transparent = (0, 0, 0, 0)
    px = [[transparent for _ in range(width)] for _ in range(height)]
    rng = random.Random("abyss_hound_production")

    if not glow:
        chitin = (28, 22, 30)
        tissue = (92, 24, 40)
        for y in range(height):
            for x in range(128):
                plate = ((x // 7) + (y // 6)) % 3
                edge = -0.20 if x % 16 in (0, 15) or y % 16 in (0, 15) else 0.0
                grain = rng.uniform(-0.11, 0.11)
                px[y][x] = shade(chitin, 0.86 + plate * 0.075 + edge + grain)

        # Exposed sinew seams under the armour plates.
        for y in range(30, 84):
            for x in range(0, 112):
                if (x + y * 2) % 19 in (0, 1) or (x * 2 - y) % 31 == 0:
                    px[y][x] = shade(tissue, 0.8 + rng.random() * 0.35)

        # Bone regions: horns, jaw teeth, claws, dorsal spines and tail blade.
        bone_regions = (
            (96, 0, 124, 20), (68, 8, 94, 15),
            (34, 86, 59, 99), (98, 86, 124, 100),
            (66, 103, 116, 118),
        )
        for x0, y0, x1, y1 in bone_regions:
            for y in range(y0, y1):
                for x in range(x0, x1):
                    fracture = 0.58 if (x * 3 + y * 5) % 23 == 0 else 0.0
                    px[y][x] = shade((178, 157, 136), 0.82 + rng.random() * 0.24 - fracture)

        # Chipped highlights give the broad armour plates readable edges at game scale.
        for x0, y0, x1, y1 in ((0, 32, 50, 57), (52, 32, 96, 65), (0, 64, 44, 82)):
            for x in range(x0 + 2, x1 - 2):
                if (x + y0) % 3:
                    px[y0 + 1][x] = shade((62, 48, 65), 1.0)

    # These atlas islands belong only to the raised eye/fissure geometry.
    glow_regions = ((224, 0, 229, 4), (224, 8, 237, 16), (224, 20, 236, 27))
    for x0, y0, x1, y1 in glow_regions:
        for y in range(y0, y1):
            for x in range(x0, x1):
                rim = x in (x0, x1 - 1) or y in (y0, y1 - 1)
                if glow:
                    px[y][x] = (255, 36, 54, 210 if rim else 255)
                else:
                    px[y][x] = (166, 18, 38, 255) if rim else (255, 58, 72, 255)
        if not glow:
            px[(y0 + y1) // 2][(x0 + x1) // 2] = (255, 205, 196, 255)

    return px


def shadow_walker_texture(glow=False):
    """縦に崩れる虚無布。規則的な装甲線を使わない。"""
    px = [[(0, 0, 0, 0) for _ in range(256)] for _ in range(128)]
    rng = random.Random("shadow_walker_void_cloth")
    if not glow:
        for y in range(128):
            for x in range(160):
                band = (x // 5) % 7
                fade = 0.72 + band * 0.035 + rng.uniform(-0.08, 0.08)
                if (x * 5 + y * 3) % 47 < 3:
                    fade -= 0.28
                px[y][x] = shade((21, 18, 35), fade)

        # 影が下へほどける縦裂けと、輪郭を失う市松状の欠落。
        for start_x in (9, 27, 54, 83, 118, 145):
            x = start_x
            for y in range(4, 124):
                if y % 9 == 0:
                    x += rng.choice((-1, 0, 1))
                if 1 <= x < 158 and rng.random() > 0.13:
                    px[y][x] = shade((96, 48, 190), 0.55 + rng.random() * 0.45)
                    if y % 5 == 0 and x + 1 < 160:
                        px[y][x + 1] = shade((48, 30, 82), 0.9)
        for y in range(72, 128):
            for x in range(160):
                cell = (x // 4 + y // 5) % 2
                threshold = 0.06 + (y - 72) / 150.0
                if cell and rng.random() < threshold:
                    px[y][x] = shade((6, 5, 13), 0.75)

    # 細い顔裂けと肋骨状の断片。発光マップ側も縦方向のリズムだけを持つ。
    for y in range(0, 7):
        for x in range(224, 229):
            if abs(x - 226) <= (1 if y in (2, 3, 4) else 0):
                px[y][x] = (202, 118, 255, 255) if glow else (126, 52, 220, 255)
    for y in range(8, 20):
        span = 2 + (y % 3)
        for x in range(224, 224 + span):
            if (x + y) % 2 == 0:
                px[y][x] = (170, 74, 255, 235) if glow else (91, 38, 174, 255)
    return px


def mana_leech_texture(glow=False):
    """濡れた節殻、膜、脈、膨張する魔力嚢からなる生体表皮。"""
    px = [[(0, 0, 0, 0) for _ in range(256)] for _ in range(128)]
    rng = random.Random("mana_leech_living_membrane")
    if not glow:
        for y in range(128):
            for x in range(160):
                dx, dy = x - 76, (y - 62) * 1.35
                ring = int(math.sqrt(dx * dx + dy * dy))
                shell = 0.70 + (ring % 12) / 34.0
                pores = -0.25 if (x * 7 + y * 11) % 53 < 4 else 0.0
                px[y][x] = shade((66, 25, 60), shell + pores + rng.uniform(-0.05, 0.05))

        # 曲線状の腹節。直線グリッドではなく同心弧で体の伸縮を表現する。
        for radius in (17, 29, 43, 58, 74, 91):
            for y in range(2, 126):
                for x in range(2, 158):
                    d = math.sqrt((x - 76) ** 2 + ((y - 61) * 1.35) ** 2)
                    if abs(d - radius) < 0.65:
                        px[y][x] = shade((145, 48, 101), 0.9 + rng.random() * 0.25)
        # 半透明膜の穴と、嚢へ集まる枝分かれした血管。
        for cy, cx in ((22, 24), (38, 116), (82, 36), (101, 126)):
            for y in range(max(0, cy - 8), min(128, cy + 9)):
                for x in range(max(0, cx - 12), min(160, cx + 13)):
                    oval = ((x - cx) / 12) ** 2 + ((y - cy) / 8) ** 2
                    if oval < 1:
                        px[y][x] = shade((95, 34, 83), 0.82 + (1 - oval) * 0.35)
        for root in (18, 49, 105, 138):
            for y in range(10, 118):
                x = root + int(5 * math.sin((y + root) / 9.0))
                if 1 <= x < 159:
                    px[y][x] = shade((202, 56, 145), 0.75 + (y % 7) * 0.035)

    # 花弁状の口先と丸い魔力嚢。中心ほど白熱する。
    for y in range(0, 7):
        for x in range(224, 238):
            petal = ((x - 230) + (y - 3) * 2) % 6
            if petal in (0, 1, 2):
                px[y][x] = (255, 90, 220, 240) if glow else (196, 43, 144, 255)
    for y in range(8, 20):
        for x in range(224, 244):
            d = ((x - 233.5) / 9.5) ** 2 + ((y - 13.5) / 5.5) ** 2
            if d <= 1:
                if glow:
                    px[y][x] = (255, clamp(118 + (1 - d) * 100), 230, 255)
                else:
                    px[y][x] = shade((236, 61, 181), 0.72 + (1 - d) * 0.45)
    return px


def crystal_parasite_texture(glow=False):
    """斜めに割れた頁岩と、内側から覗くシアンの晶洞。"""
    px = [[(0, 0, 0, 0) for _ in range(256)] for _ in range(128)]
    rng = random.Random("crystal_parasite_slate_geode")
    if not glow:
        for y in range(128):
            for x in range(160):
                stratum = ((x + y * 2) // 9) % 5
                facet = ((x - y) // 13) % 3
                px[y][x] = shade((37, 55, 61), 0.68 + stratum * 0.07 + facet * 0.045
                                                 + rng.uniform(-0.07, 0.07))

        # 斜めの層理面と三角形の欠け。生物系の曲線は使わない。
        for offset in (-92, -58, -21, 17, 54, 91, 128):
            for y in range(128):
                x = offset + y
                for dx in (0, 1):
                    if 0 <= x + dx < 160:
                        px[y][x + dx] = shade((79, 112, 118), 0.8 + rng.random() * 0.2)
        for apex_x, apex_y, size in ((25, 19, 13), (92, 33, 18), (46, 91, 21), (132, 78, 15)):
            for yy in range(size):
                width = max(1, size - yy)
                for xx in range(-width, width + 1):
                    x, y = apex_x + xx, apex_y + yy
                    if 0 <= x < 160 and 0 <= y < 128 and (xx + yy) % 4 != 0:
                        px[y][x] = shade((56, 89, 96), 0.82 + yy / max(1, size) * 0.22)
        # 晶洞へ向かう枝分かれ亀裂。
        for start_x in (16, 71, 113, 148):
            x = start_x
            for y in range(5, 123):
                if y % 6 == 0:
                    x += rng.choice((-2, -1, 1, 2))
                if 1 <= x < 159:
                    px[y][x] = shade((62, 197, 211), 0.55 + (y % 5) * 0.08)
                    if y % 17 == 0 and x + 1 < 160:
                        px[y][x + 1] = shade((125, 232, 238), 0.75)

    # 中央開口だけを硬質な菱形ファセットとして発光させる。
    for y in range(0, 9):
        for x in range(224, 241):
            d = abs(x - 232) / 8 + abs(y - 4) / 4
            if d <= 1:
                if glow:
                    edge = d > 0.68
                    px[y][x] = (97 if edge else 220, 236 if edge else 255, 255, 255)
                else:
                    px[y][x] = shade((75, 216, 231), 0.75 + (1 - d) * 0.55)
    return px


def fallen_researcher_texture(glow=False):
    """縫い直された煤布、革、羊皮紙、真鍮、薬液染みの混成装束。"""
    px = [[(0, 0, 0, 0) for _ in range(256)] for _ in range(128)]
    rng = random.Random("fallen_researcher_stitched_archive")
    if not glow:
        for y in range(128):
            for x in range(160):
                weave = 0.07 if (x + y) % 4 == 0 else -0.03 if (x - y) % 5 == 0 else 0
                px[y][x] = shade((39, 32, 37), 0.79 + weave + rng.uniform(-0.055, 0.055))

        # 不揃いな革・羊皮紙の継ぎ当て。各パッチは縫い方向も材質も異なる。
        patches = (
            (7, 8, 47, 34, (91, 64, 42)), (57, 4, 112, 28, (126, 101, 64)),
            (19, 50, 69, 82, (68, 49, 39)), (88, 43, 151, 76, (111, 87, 58)),
            (4, 91, 54, 122, (82, 59, 45)), (69, 88, 132, 119, (128, 104, 70)),
        )
        for index, (x0, y0, x1, y1, color) in enumerate(patches):
            for y in range(y0, y1):
                for x in range(x0, x1):
                    grain = 0.16 if (x * (index + 2) + y) % 17 == 0 else 0
                    px[y][x] = shade(color, 0.74 + grain + rng.uniform(-0.08, 0.08))
            # 大きな連続線でなく、一針ごとに切れた縫い目。
            for x in range(x0 + 2, x1 - 1, 4):
                px[y0][x] = shade((164, 119, 66), 0.95)
                px[y1 - 1][x] = shade((164, 119, 66), 0.78)
            for y in range(y0 + 2, y1 - 1, 4):
                px[y][x0] = shade((164, 119, 66), 0.9)
                px[y][x1 - 1] = shade((164, 119, 66), 0.78)
        # 真鍮鋲と、失敗した薬液の不定形な染み。
        for x, y in ((11, 11), (43, 31), (61, 7), (108, 25), (91, 47), (145, 72), (73, 91), (127, 116)):
            px[y][x] = (190, 139, 58, 255)
            if x + 1 < 160:
                px[y][x + 1] = (92, 65, 31, 255)
        for cx, cy, radius in ((38, 65, 9), (105, 99, 12), (139, 38, 6)):
            for y in range(max(0, cy - radius), min(128, cy + radius + 1)):
                for x in range(max(0, cx - radius), min(160, cx + radius + 1)):
                    d = math.sqrt((x - cx) ** 2 + (y - cy) ** 2)
                    wobble = ((x * 7 + y * 3) % 9) / 5
                    if d < radius - wobble:
                        px[y][x] = shade((91, 43, 104), 0.67 + rng.random() * 0.24)

    # 三眼レンズ、魔導書の印、結晶化した指先を別々の図形として描く。
    for center_x in (226, 232, 238):
        for y in range(0, 7):
            for x in range(center_x - 2, center_x + 3):
                d = abs(x - center_x) + abs(y - 3)
                if d <= 2:
                    px[y][x] = (222, 130, 255, 255) if glow else (127, 58, 164, 255)
    for y in range(10, 18):
        for x in range(224, 238):
            if x in (230, 231) or y in (13, 14) or (x + y) % 9 == 0:
                px[y][x] = (188, 86, 255, 245) if glow else (108, 46, 154, 255)
    for y in range(20, 32):
        for x in range(224, 244):
            if (x - 224) % 6 <= 2 and y >= 20 + abs((x - 225) % 6 - 1):
                px[y][x] = (213, 105, 255, 235) if glow else (119, 53, 166, 255)
    return px


def boundary_watcher_texture(glow=False):
    """巨石板、古金の象嵌、宇宙幾何、境界亀裂からなる儀礼装甲。"""
    px = [[(0, 0, 0, 0) for _ in range(256)] for _ in range(128)]
    rng = random.Random("boundary_watcher_cosmic_monument")
    if not glow:
        for y in range(128):
            for x in range(160):
                slab = ((x // 32) + (y // 40) * 2) % 4
                mineral = rng.uniform(-0.07, 0.07)
                px[y][x] = shade((39, 38, 43), 0.68 + slab * 0.055 + mineral)

        gold = (157, 116, 48)
        dark_gold = (91, 65, 30)
        # 中央軸と左右対称の象嵌。小さな反復格子でなく、巨像全体を貫く構図。
        for y in range(4, 124):
            for x in (78, 79, 80, 81):
                px[y][x] = shade(gold, 1.08 if x in (79, 80) else 0.72)
            arm = 8 + (y // 16) * 3
            if y % 16 in (0, 1):
                for x in range(max(2, 80 - arm), min(158, 81 + arm)):
                    px[y][x] = shade(gold, 0.86 if x % 5 else 1.15)
        for cx, cy, radius in ((80, 31, 20), (80, 91, 27)):
            for y in range(max(0, cy - radius - 1), min(128, cy + radius + 2)):
                for x in range(max(0, cx - radius - 1), min(160, cx + radius + 2)):
                    d = math.sqrt((x - cx) ** 2 + ((y - cy) * 1.35) ** 2)
                    if abs(d - radius) < 0.8:
                        px[y][x] = shade(gold, 0.9 + (x % 3) * 0.08)
                    elif abs(d - radius * 0.55) < 0.65:
                        px[y][x] = shade(dark_gold, 1.0)
        # 石板の割れ目は象嵌を横切る紫の境界破断として限定する。
        for start_x, direction in ((18, 1), (142, -1), (55, 1), (108, -1)):
            x = start_x
            for y in range(10, 124):
                if y % 11 == 0:
                    x += direction * rng.choice((1, 2, 3))
                if 0 <= x < 160 and y % 3 != 0:
                    px[y][x] = shade((110, 47, 177), 0.72 + (y % 7) * 0.04)

    # 顔の縦裂け、胸の境界核、浮遊碑の小紋章。
    for y in range(0, 9):
        for x in range(224, 231):
            width = 0 if y in (0, 8) else 1 if y in (1, 2, 6, 7) else 2
            if abs(x - 227) <= width:
                px[y][x] = (210, 119, 255, 255) if glow else (124, 55, 183, 255)
    for y in range(8, 20):
        for x in range(224, 244):
            d = abs(x - 233.5) / 9.5 + abs(y - 13.5) / 5.5
            ring = 0.48 <= d <= 0.78
            axis = x in (233, 234) or y in (13, 14)
            if d <= 1 and (ring or axis):
                px[y][x] = (231, 158, 255, 255) if glow else (137, 65, 190, 255)
    for y in range(20, 32):
        for x in range(224, 250):
            rune = (x - 224) % 7 in (0, 1) and y % 4 != 0
            cap = y in (21, 29) and (x - 224) % 7 < 5
            if rune or cap:
                px[y][x] = (197, 115, 255, 225) if glow else (119, 59, 174, 255)
    return px


OVERWORLD_MOB_TEXTURES = {
    "shadow_walker": shadow_walker_texture,
    "mana_leech": mana_leech_texture,
    "crystal_parasite": crystal_parasite_texture,
    "fallen_researcher": fallen_researcher_texture,
    "boundary_watcher": boundary_watcher_texture,
}


ITEM_TEXTURES = {
    # 基礎素材
    "raw_abyss_iron": (blob_texture, (86, 70, 110)),
    "abyss_iron_ingot": (ingot_texture, (128, 100, 180)),
    "compressed_abyss_iron": (ingot_texture, (96, 72, 150)),
    "high_density_abyss_alloy": (ingot_texture, (60, 40, 110)),
    "abyss_crystal": (gem_texture, (150, 90, 220)),
    "compressed_abyss_crystal": (gem_texture, (100, 50, 180)),
    "verdant_fang": (gem_texture, (110, 210, 90)),
    "cinder_heart": (gem_texture, (255, 90, 20)),
    "glacial_plate": (ingot_texture, (120, 200, 240)),
    "living_sinew": (blob_texture, (210, 70, 95)),
    "void_eye": (gem_texture, (140, 95, 220)),
    "verdant_blade": (relic_texture, (110, 230, 120)),
    "cinder_cleaver": (relic_texture, (255, 105, 35)),
    "frost_lance": (relic_texture, (130, 215, 255)),
    "flesh_scythe": (relic_texture, (220, 85, 105)),
    "void_edge": (relic_texture, (170, 120, 255)),
    # 忘却の森
    "primordial_sap": (blob_texture, (110, 190, 70)),
    "awakened_vine": (blob_texture, (60, 140, 50)),
    "grove_heart_key": (key_texture, (190, 158, 62)),
    "perfect_life_core": (gem_texture, (120, 230, 120)),
    "rotten_forest_core": (gem_texture, (70, 110, 40)),
    # 灰の荒野
    "eternal_flame": (blob_texture, (255, 140, 30)),
    "superheated_core": (gem_texture, (255, 90, 30)),
    "ash_king_metal": (ingot_texture, (150, 140, 140)),
    "eternal_furnace_core": (gem_texture, (255, 60, 0)),
    # 蒼氷洞窟
    "unmelting_ice_crystal": (gem_texture, (140, 210, 255)),
    "frozen_time_shard": (gem_texture, (90, 160, 230)),
    "permafrost_core": (gem_texture, (60, 110, 200)),
    # 肉体鉱山
    "primordial_nerve": (blob_texture, (230, 120, 140)),
    "undying_cell": (blob_texture, (200, 60, 90)),
    "world_pulse_fluid": (blob_texture, (170, 30, 60)),
    "primordial_nerve_bundle": (gem_texture, (240, 100, 120)),
    # 虚無の都
    "spatial_anchor_crystal": (gem_texture, (180, 160, 255)),
    "void_stabilizer": (gem_texture, (110, 90, 160)),
    "world_law_fragment": (gem_texture, (230, 220, 255)),
    # 最終素材・究極アイテム
    "abyss_god_core": (gem_texture, (40, 0, 80)),
    "five_layer_unified_core": (gem_texture, (255, 215, 90)),
    "primordial_relic": (relic_texture, (200, 170, 255)),
    "abyss_key": (key_texture, (170, 120, 230)),
    "abyss_god_catalyst": (gem_texture, (80, 0, 120)),
    # 深淵呪文体系
    "abyss_spellbook": (spellbook_texture, (82, 42, 112)),
    "glyph_form_self": (glyph_texture, (74, 225, 218)),
    "glyph_form_bolt": (glyph_texture, (68, 176, 238)),
    "glyph_form_area": (glyph_texture, (108, 212, 176)),
    "glyph_effect_fire": (glyph_texture, (255, 94, 36)),
    "glyph_effect_frost": (glyph_texture, (126, 216, 255)),
    "glyph_effect_heal": (glyph_texture, (98, 230, 128)),
    "glyph_effect_break": (glyph_texture, (220, 176, 92)),
    "glyph_effect_pull": (glyph_texture, (194, 104, 232)),
    "glyph_effect_blink": (glyph_texture, (132, 92, 244)),
    "glyph_augment_power": (glyph_texture, (255, 192, 66)),
    "glyph_augment_range": (glyph_texture, (240, 126, 210)),
    "glyph_augment_duration": (glyph_texture, (112, 196, 236)),
    "glyph_augment_efficiency": (glyph_texture, (102, 226, 158)),
    "glyph_augment_chain": (glyph_texture, (224, 130, 92)),
    "glyph_augment_area": (glyph_texture, (188, 142, 238)),
    # 工業制御・中間資源
    "resonance_configurator": (relic_texture, (80, 220, 230)),
    "item_filter": (gem_texture, (210, 180, 70)),
    "speed_upgrade": (gem_texture, (70, 210, 255)),
    "efficiency_upgrade": (gem_texture, (90, 230, 120)),
    "capacity_upgrade": (gem_texture, (210, 150, 255)),
    "auto_export_upgrade": (gem_texture, (255, 180, 70)),
    "range_upgrade": (gem_texture, (240, 100, 210)),
    "basic_factory_core": (gem_texture, (120, 170, 190)),
    "advanced_factory_core": (gem_texture, (90, 210, 220)),
    "ultimate_factory_core": (gem_texture, (220, 120, 255)),
    "resonance_matrix": (gem_texture, (110, 225, 235)),
    "thermal_matrix": (gem_texture, (255, 100, 35)),
    "abyssal_essence_bucket": (blob_texture, (175, 90, 235)),
}

INDUSTRIAL_BLOCKS = {
    "abyss_essence_extractor": ((35, 48, 62), (175, 90, 235)),
    "verdant_mana_bloom": ((34, 70, 48), (105, 235, 115)),
    "inferno_mana_crucible": ((72, 36, 28), (255, 105, 35)),
    "cryo_mana_siphon": ((45, 74, 92), (150, 225, 255)),
    "void_mana_tap": ((25, 18, 42), (175, 100, 245)),
    "abyss_mana_reservoir": ((42, 31, 62), (165, 95, 225)),
    "wireless_mana_relay": ((25, 55, 67), (80, 235, 240)),
    "mana_vortex_reactor": ((27, 18, 42), (235, 90, 255)),
    "abyss_essence_reservoir": ((43, 34, 59), (190, 100, 245)),
    "abyss_storage_terminal": ((32, 40, 48), (70, 210, 220)),
    "leyline_miner": ((28, 44, 50), (45, 225, 210)),
}

MAGIC_NEXUS_BLOCKS = {
    "verdant_nexus": ((30, 64, 44), (84, 190, 92), (116, 242, 142)),
    "gathering_nexus": ((28, 48, 62), (70, 154, 174), (86, 230, 232)),
    "warding_nexus": ((45, 28, 66), (128, 76, 170), (218, 126, 250)),
}

BLOCK_TEXTURES = {
    "abyss_iron_ore": ((45, 42, 60), (128, 100, 180)),
    "abyss_crystal_ore": ((45, 42, 60), (150, 90, 220)),
    "abyss_iron_block": ((110, 85, 160), None),
    "world_reconstruction_furnace": ((25, 15, 45), (255, 215, 90)),
    "abyss_stone": ((38, 32, 58), (86, 62, 128)),
    "boundary_soil": ((25, 23, 31), (78, 42, 104)),
    "boundary_stone": ((31, 30, 38), (98, 55, 132)),
    "boundary_bricks": ((38, 36, 45), (156, 116, 52)),
    "rift_core": ((54, 22, 76), (208, 104, 255)),
    "forgotten_soil": ((36, 64, 38), (92, 142, 58)),
    "forgotten_stone": ((38, 58, 44), (78, 114, 70)),
    "grove_seal": ((22, 48, 37), (76, 232, 168)),
    "ash_crust": ((54, 50, 50), (178, 72, 42)),
    "ash_stone": ((34, 31, 34), (112, 84, 74)),
    "frozen_surface": ((164, 204, 220), (230, 250, 255)),
    "frozen_stone": ((70, 104, 132), (148, 200, 230)),
    "flesh_mass": ((118, 38, 54), (210, 72, 92)),
    "flesh_stone": ((82, 32, 46), (150, 58, 70)),
    "void_surface": ((34, 22, 58), (170, 132, 230)),
    "void_stone": ((22, 18, 36), (92, 64, 150)),
    "primordial_bloom": ((40, 70, 35), (120, 220, 80)),
    "ash_vein": ((50, 45, 45), (255, 120, 40)),
    "frozen_cluster": ((180, 210, 240), (140, 200, 255)),
    "flesh_deposit": ((120, 30, 45), (220, 80, 100)),
    "void_crystal": ((30, 20, 50), (180, 160, 255)),
}

ENTITY_TEXTURES = {
    "abyss_hound": (256, 128, (30, 18, 28), (218, 36, 72)),
    "shadow_walker": (256, 128, (18, 16, 32), (112, 62, 238)),
    "mana_leech": (256, 128, (42, 20, 44), (240, 82, 200)),
    "crystal_parasite": (256, 128, (24, 38, 46), (80, 216, 232)),
    "fallen_researcher": (256, 128, (34, 28, 38), (168, 90, 232)),
    "boundary_watcher": (256, 128, (38, 32, 38), (176, 92, 238)),
    "abyss_sovereign": (64, 32, (36, 20, 60), (214, 162, 255)),
    "rotten_forest_guardian": (64, 64, (48, 86, 42), (132, 220, 84)),
    "grove_sentinel": (64, 64, (38, 62, 39), (198, 166, 74)),
    "ash_king": (64, 32, (88, 34, 20), (255, 118, 34)),
    "frostbound_warden": (64, 32, (82, 128, 160), (188, 236, 255)),
    "flesh_colossus": (128, 64, (104, 34, 48), (232, 92, 112)),
    "void_archon": (64, 32, (28, 18, 54), (176, 126, 255)),
    "forest_stalker": (64, 64, (38, 74, 36), (118, 196, 74)),
    "ash_revenant": (64, 32, (64, 48, 46), (236, 94, 40)),
    "frost_marauder": (64, 32, (72, 118, 150), (156, 224, 255)),
    "flesh_hunter": (128, 64, (92, 28, 42), (210, 76, 98)),
    "void_reaper": (64, 32, (24, 18, 42), (146, 96, 224)),
    "rootbound_thrall": (64, 64, (56, 72, 34), (120, 188, 82)),
    "cinder_imp": (64, 64, (78, 42, 28), (255, 112, 32)),
    "glacial_wraith": (64, 32, (92, 146, 180), (214, 248, 255)),
    "marrow_crawler": (64, 64, (104, 44, 58), (226, 112, 130)),
    "void_shade": (64, 32, (22, 16, 38), (120, 84, 210)),
}

SPAWN_EGG_MODELS = [
    "abyss_hound_spawn_egg",
    "shadow_walker_spawn_egg",
    "mana_leech_spawn_egg",
    "crystal_parasite_spawn_egg",
    "fallen_researcher_spawn_egg",
    "boundary_watcher_spawn_egg",
    "abyss_sovereign_spawn_egg",
    "rotten_forest_guardian_spawn_egg",
    "grove_sentinel_spawn_egg",
    "ash_king_spawn_egg",
    "frostbound_warden_spawn_egg",
    "flesh_colossus_spawn_egg",
    "void_archon_spawn_egg",
    "forest_stalker_spawn_egg",
    "ash_revenant_spawn_egg",
    "frost_marauder_spawn_egg",
    "flesh_hunter_spawn_egg",
    "void_reaper_spawn_egg",
    "rootbound_thrall_spawn_egg",
    "cinder_imp_spawn_egg",
    "glacial_wraith_spawn_egg",
    "marrow_crawler_spawn_egg",
    "void_shade_spawn_egg",
]


def main():
    # アイテムテクスチャ + モデル
    for name, (fn, color) in ITEM_TEXTURES.items():
        write_png(os.path.join(ASSETS, "textures", "item", name + ".png"), fn(color, name))
        model = {"parent": "minecraft:item/generated",
                 "textures": {"layer0": "abyssworld:item/" + name}}
        if name in ("primordial_relic", "verdant_blade", "cinder_cleaver",
                    "frost_lance", "flesh_scythe", "void_edge"):
            model["parent"] = "minecraft:item/handheld"
        path = os.path.join(ASSETS, "models", "item", name + ".json")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w") as f:
            json.dump(model, f, indent=2)

    # ブロックテクスチャ + モデル + blockstate + ブロックアイテムモデル
    for name, (base, spots) in BLOCK_TEXTURES.items():
        write_png(os.path.join(ASSETS, "textures", "block", name + ".png"),
                  block_texture(base, name, spots=spots))
        if name == "grove_seal":
            write_png(os.path.join(ASSETS, "textures", "block", name + ".png"),
                      grove_seal_texture(base, name))
        bmodel = {"parent": "minecraft:block/cube_all",
                  "textures": {"all": "abyssworld:block/" + name}}
        with open(os.path.join(ASSETS, "models", "block", name + ".json"), "w") as f:
            json.dump(bmodel, f, indent=2)
        state = {"variants": {"": {"model": "abyssworld:block/" + name}}}
        os.makedirs(os.path.join(ASSETS, "blockstates"), exist_ok=True)
        with open(os.path.join(ASSETS, "blockstates", name + ".json"), "w") as f:
            json.dump(state, f, indent=2)
        imodel = {"parent": "abyssworld:block/" + name}
        with open(os.path.join(ASSETS, "models", "item", name + ".json"), "w") as f:
            json.dump(imodel, f, indent=2)

    # 工業設備: 四隅フレームを持つ既存の立体機械形状に専用三面テクスチャを与える
    for name, (base, accent) in INDUSTRIAL_BLOCKS.items():
        for face, color, spots in (("side", base, accent), ("front", accent, (240, 245, 255)),
                                   ("top", tuple(min(255, c + 22) for c in base), accent)):
            write_png(os.path.join(ASSETS, "textures", "block", name + "_" + face + ".png"),
                      block_texture(color, name + face, spots=spots))
        bmodel = {"parent": "abyssworld:block/abyss_mana_condenser",
                  "textures": {"side": "abyssworld:block/" + name + "_side",
                               "front": "abyssworld:block/" + name + "_front",
                               "top": "abyssworld:block/" + name + "_top",
                               "particle": "abyssworld:block/" + name + "_side"}}
        with open(os.path.join(ASSETS, "models", "block", name + ".json"), "w") as f:
            json.dump(bmodel, f, indent=2)
        facing = name not in ("abyss_essence_reservoir", "abyss_storage_terminal")
        state = ({"variants": {
            "facing=north": {"model": "abyssworld:block/" + name},
            "facing=east": {"model": "abyssworld:block/" + name, "y": 90},
            "facing=south": {"model": "abyssworld:block/" + name, "y": 180},
            "facing=west": {"model": "abyssworld:block/" + name, "y": 270}}}
            if facing else {"variants": {"": {"model": "abyssworld:block/" + name}}})
        with open(os.path.join(ASSETS, "blockstates", name + ".json"), "w") as f:
            json.dump(state, f, indent=2)
        with open(os.path.join(ASSETS, "models", "item", name + ".json"), "w") as f:
            json.dump({"parent": "abyssworld:block/" + name}, f, indent=2)

    # 設置型魔術: 低い台座、刻印柱、上部結晶で構成する専用モデル
    for name, (base, core, rune) in MAGIC_NEXUS_BLOCKS.items():
        for part, color, spots in (("base", base, core), ("core", core, rune), ("rune", rune, (245, 255, 255))):
            write_png(os.path.join(ASSETS, "textures", "block", name + "_" + part + ".png"),
                      block_texture(color, name + part, spots=spots))
        def faces(texture):
            return {face: {"texture": texture} for face in ("down", "up", "north", "south", "west", "east")}
        model = {
            "textures": {
                "base": "abyssworld:block/" + name + "_base",
                "core": "abyssworld:block/" + name + "_core",
                "rune": "abyssworld:block/" + name + "_rune",
                "particle": "abyssworld:block/" + name + "_base"
            },
            "elements": [
                {"from": [1, 0, 1], "to": [15, 3, 15], "faces": faces("#base")},
                {"from": [4, 3, 4], "to": [12, 10, 12], "faces": faces("#core")},
                {"from": [3, 10, 3], "to": [13, 12, 13], "faces": faces("#base")},
                {"from": [5, 12, 5], "to": [11, 16, 11], "faces": faces("#rune")}
            ]
        }
        with open(os.path.join(ASSETS, "models", "block", name + ".json"), "w") as f:
            json.dump(model, f, indent=2)
        with open(os.path.join(ASSETS, "blockstates", name + ".json"), "w") as f:
            json.dump({"variants": {"": {"model": "abyssworld:block/" + name}}}, f, indent=2)
        with open(os.path.join(ASSETS, "models", "item", name + ".json"), "w") as f:
            json.dump({"parent": "abyssworld:block/" + name}, f, indent=2)

    # 霊液本体の静止・流動テクスチャ
    write_png(os.path.join(ASSETS, "textures", "block", "abyssal_essence_still.png"),
              block_texture((150, 70, 215), "essence_still", spots=(220, 160, 255)))
    write_png(os.path.join(ASSETS, "textures", "block", "abyssal_essence_flow.png"),
              block_texture((125, 55, 195), "essence_flow", spots=(195, 125, 245)))
    for name, shell, core in (
            ("item_conduit", (62, 72, 78), (75, 225, 230)),
            ("fluid_conduit", (58, 38, 72), (205, 100, 250))):
        write_png(os.path.join(ASSETS, "textures", "block", name + "_shell.png"),
                  block_texture(shell, name + "shell", spots=(130, 145, 150)))
        write_png(os.path.join(ASSETS, "textures", "block", name + "_core.png"),
                  block_texture(core, name + "core", spots=(245, 245, 255)))

    # エンティティテクスチャ
    for name, (width, height, base, accent) in ENTITY_TEXTURES.items():
        if name == "abyss_hound":
            pixels = abyss_hound_texture()
        elif name in OVERWORLD_MOB_TEXTURES:
            pixels = OVERWORLD_MOB_TEXTURES[name]()
        else:
            pixels = entity_texture(width, height, base, accent, name)
        write_png(os.path.join(ASSETS, "textures", "entity", name + ".png"), pixels)
    write_png(os.path.join(ASSETS, "textures", "entity", "abyss_hound_glow.png"),
              abyss_hound_texture(glow=True))
    for name, texture_factory in OVERWORLD_MOB_TEXTURES.items():
        write_png(os.path.join(ASSETS, "textures", "entity", name + "_glow.png"),
                  texture_factory(glow=True))

    for name in SPAWN_EGG_MODELS:
        model = {"parent": "minecraft:item/template_spawn_egg"}
        with open(os.path.join(ASSETS, "models", "item", name + ".json"), "w") as f:
            json.dump(model, f, indent=2)

    # 新規工業ブロックのloot（霊液そのものは対象外）
    industrial_loot = list(INDUSTRIAL_BLOCKS) + list(MAGIC_NEXUS_BLOCKS) + ["abyss_item_conduit", "abyss_fluid_conduit"]
    for name in industrial_loot:
        loot = {"type": "minecraft:block", "pools": [{"rolls": 1,
                "entries": [{"type": "minecraft:item", "name": "abyssworld:" + name}],
                "conditions": [{"condition": "minecraft:survives_explosion"}]}]}
        path = os.path.join(ROOT, "data", "abyssworld", "loot_tables", "blocks", name + ".json")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w") as f:
            json.dump(loot, f, indent=2)

    recipes = {
        "abyss_spellbook": (["LCL", "PBP", "LCL"], {"L":"minecraft:leather","C":"abyssworld:abyss_crystal","P":"minecraft:paper","B":"minecraft:book"}, 1),
        "glyph_form_self": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"minecraft:compass","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_form_bolt": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"minecraft:arrow","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_form_area": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:rotten_forest_core","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_fire": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:eternal_flame","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_frost": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:unmelting_ice_crystal","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_heal": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:verdant_fang","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_break": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"minecraft:iron_pickaxe","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_pull": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:living_sinew","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_effect_blink": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:spatial_anchor_crystal","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_power": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:cinder_heart","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_range": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:glacial_plate","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_duration": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:permafrost_core","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_efficiency": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:crystallization_residue","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_chain": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:primordial_nerve_bundle","C":"abyssworld:abyss_crystal"}, 1),
        "glyph_augment_area": ([" P ", "PXP", " C "], {"P":"minecraft:paper","X":"abyssworld:void_eye","C":"abyssworld:abyss_crystal"}, 1),
        "abyss_item_conduit": (["IGI", "RCR", "IGI"], {"I":"abyssworld:abyss_iron_ingot","G":"minecraft:glass","R":"minecraft:redstone","C":"abyssworld:abyss_crystal"}, 8),
        "abyss_fluid_conduit": (["IGI", "RCR", "IGI"], {"I":"abyssworld:abyss_iron_ingot","G":"minecraft:glass","R":"minecraft:copper_ingot","C":"abyssworld:compressed_abyss_crystal"}, 8),
        "resonance_configurator": (["  C", " IC", "I  "], {"I":"abyssworld:abyss_iron_ingot","C":"abyssworld:abyss_crystal"}, 1),
        "item_filter": ([" P ", "RCR", " P "], {"P":"minecraft:paper","R":"minecraft:redstone","C":"abyssworld:abyss_crystal"}, 1),
        "speed_upgrade": ([" S ", "RCR", " S "], {"S":"minecraft:sugar","R":"minecraft:redstone","C":"abyssworld:abyss_crystal"}, 1),
        "efficiency_upgrade": ([" L ", "RCR", " L "], {"L":"minecraft:lapis_lazuli","R":"minecraft:redstone","C":"abyssworld:abyss_crystal"}, 1),
        "capacity_upgrade": ([" G ", "RCR", " G "], {"G":"minecraft:glass","R":"minecraft:redstone","C":"abyssworld:compressed_abyss_crystal"}, 1),
        "auto_export_upgrade": ([" H ", "RCR", " H "], {"H":"minecraft:hopper","R":"minecraft:redstone","C":"abyssworld:abyss_crystal"}, 1),
        "range_upgrade": ([" E ", "RCR", " E "], {"E":"minecraft:ender_pearl","R":"minecraft:redstone","C":"abyssworld:compressed_abyss_crystal"}, 1),
        "basic_factory_core": (["SCS", "RMR", "SCS"], {"S":"abyssworld:speed_upgrade","C":"abyssworld:compressed_abyss_iron","R":"minecraft:redstone","M":"abyssworld:abyss_machine_casing"}, 1),
        "advanced_factory_core": (["CBC", "RMR", "CBC"], {"C":"abyssworld:compressed_abyss_crystal","B":"abyssworld:basic_factory_core","R":"abyssworld:resonance_matrix","M":"abyssworld:abyss_machine_casing"}, 1),
        "ultimate_factory_core": (["UAU", "RMR", "UAU"], {"U":"abyssworld:high_density_abyss_alloy","A":"abyssworld:advanced_factory_core","R":"abyssworld:singularity_residue","M":"abyssworld:abyss_machine_casing"}, 1),
        "thermal_matrix": (["EFE", "RCR", "EFE"], {"E":"abyssworld:eternal_flame","F":"minecraft:blaze_powder","R":"abyssworld:resonance_matrix","C":"abyssworld:compressed_abyss_crystal"}, 1),
        "abyss_essence_extractor": (["GCG", "AMA", "GCG"], {"G":"minecraft:glass","C":"abyssworld:compressed_abyss_crystal","A":"abyssworld:abyss_iron_ingot","M":"abyssworld:abyss_machine_casing"}, 1),
        "abyss_essence_reservoir": (["GAG", "AMA", "GAG"], {"G":"minecraft:glass","A":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_machine_casing"}, 1),
        "abyss_storage_terminal": (["ICI", "CMC", "IRI"], {"I":"abyssworld:compressed_abyss_iron","C":"minecraft:ender_chest","M":"abyssworld:abyss_machine_casing","R":"minecraft:redstone_block"}, 1),
        "verdant_mana_bloom": (["VAV", "CMC", "VAV"], {"V":"abyssworld:awakened_vine","A":"abyssworld:primordial_sap","C":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_mana_pool"}, 1),
        "inferno_mana_crucible": (["EAE", "CMC", "EAE"], {"E":"abyssworld:eternal_flame","A":"minecraft:blaze_rod","C":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_mana_condenser"}, 1),
        "cryo_mana_siphon": (["IAI", "CMC", "IAI"], {"I":"abyssworld:unmelting_ice_crystal","A":"minecraft:blue_ice","C":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_mana_condenser"}, 1),
        "void_mana_tap": (["EVE", "CMC", "EVE"], {"E":"minecraft:ender_eye","V":"abyssworld:void_stabilizer","C":"abyssworld:high_density_abyss_alloy","M":"abyssworld:abyss_mana_condenser"}, 1),
        "abyss_mana_reservoir": (["HCH", "CMC", "HCH"], {"H":"abyssworld:high_density_abyss_alloy","C":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_mana_condenser"}, 1),
        "wireless_mana_relay": (["ESE", "RMR", "ESE"], {"E":"minecraft:ender_eye","S":"abyssworld:spatial_anchor_crystal","R":"abyssworld:resonance_matrix","M":"abyssworld:abyss_mana_reservoir"}, 1),
        "mana_vortex_reactor": (["SUS", "RMR", "SUS"], {"S":"abyssworld:singularity_residue","U":"abyssworld:five_layer_unified_core","R":"abyssworld:thermal_matrix","M":"abyssworld:abyss_mana_reservoir"}, 1),
        "leyline_miner": (["ASA", "CMC", "ETE"], {"A":"abyssworld:high_density_abyss_alloy","S":"abyssworld:spatial_anchor_crystal","C":"abyssworld:compressed_abyss_crystal","M":"abyssworld:abyss_machine_casing","E":"minecraft:ender_eye","T":"abyssworld:thermal_matrix"}, 1),
        "verdant_nexus": (["VGV", "PMP", "VGV"], {"V":"abyssworld:awakened_vine","G":"abyssworld:glyph_effect_heal","P":"abyssworld:primordial_sap","M":"abyssworld:abyss_mana_pool"}, 1),
        "gathering_nexus": (["HGH", "PMP", "HGH"], {"H":"minecraft:hopper","G":"abyssworld:glyph_effect_pull","P":"abyssworld:living_sinew","M":"abyssworld:abyss_mana_pool"}, 1),
        "warding_nexus": (["FGF", "PMP", "FGF"], {"F":"abyssworld:glacial_plate","G":"abyssworld:glyph_augment_area","P":"abyssworld:verdant_fang","M":"abyssworld:abyss_mana_pool"}, 1),
    }
    for name, (pattern, keys, count) in recipes.items():
        recipe = {"type":"minecraft:crafting_shaped", "pattern":pattern,
                  "key":{key:{"item":value} for key,value in keys.items()},
                  "result":{"item":"abyssworld:"+name}}
        if count != 1:
            recipe["result"]["count"] = count
        path = os.path.join(ROOT, "data", "abyssworld", "recipes", name + ".json")
        with open(path, "w") as f:
            json.dump(recipe, f, indent=2)

    print("generated:", len(ITEM_TEXTURES), "items,", len(BLOCK_TEXTURES) + len(MAGIC_NEXUS_BLOCKS), "blocks,",
          len(ENTITY_TEXTURES), "entities,", len(SPAWN_EGG_MODELS), "spawn eggs")


if __name__ == "__main__":
    main()
