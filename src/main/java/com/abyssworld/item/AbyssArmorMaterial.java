package com.abyssworld.item;

import com.abyssworld.AbyssWorld;
import com.abyssworld.registry.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public enum AbyssArmorMaterial implements ArmorMaterial {
    CRYSTALLINE("crystalline_abyss", 45, 18, 4.0F, 0.15F,
            Map.of(ArmorItem.Type.HELMET, 4, ArmorItem.Type.CHESTPLATE, 9,
                    ArmorItem.Type.LEGGINGS, 7, ArmorItem.Type.BOOTS, 4),
            () -> Ingredient.of(ModItems.CRYSTALLINE_ARMOR_PLATE.get())),
    SINGULARITY("singularity_abyss", 55, 25, 5.0F, 0.20F,
            Map.of(ArmorItem.Type.HELMET, 5, ArmorItem.Type.CHESTPLATE, 11,
                    ArmorItem.Type.LEGGINGS, 8, ArmorItem.Type.BOOTS, 5),
            () -> Ingredient.of(ModItems.SINGULARITY_ARMOR_PLATE.get()));

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = new EnumMap<>(ArmorItem.Type.class);

    static {
        BASE_DURABILITY.put(ArmorItem.Type.HELMET, 13);
        BASE_DURABILITY.put(ArmorItem.Type.CHESTPLATE, 15);
        BASE_DURABILITY.put(ArmorItem.Type.LEGGINGS, 16);
        BASE_DURABILITY.put(ArmorItem.Type.BOOTS, 11);
    }

    private final String name;
    private final int durabilityMultiplier;
    private final int enchantmentValue;
    private final float toughness;
    private final float knockbackResistance;
    private final Map<ArmorItem.Type, Integer> defense;
    private final Supplier<Ingredient> repairIngredient;

    AbyssArmorMaterial(String name, int durabilityMultiplier, int enchantmentValue,
                       float toughness, float knockbackResistance,
                       Map<ArmorItem.Type, Integer> defense,
                       Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.enchantmentValue = enchantmentValue;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.defense = defense;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY.get(type) * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return defense.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return AbyssWorld.MODID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
