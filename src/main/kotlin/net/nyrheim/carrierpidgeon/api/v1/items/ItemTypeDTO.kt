package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.ability.StrengthRequirement
import net.nyrheim.penandpaper.item.ItemType
import net.nyrheim.penandpaper.item.adventuringgear.AdventuringGearType
import net.nyrheim.penandpaper.item.armor.ArmorType
import net.nyrheim.penandpaper.item.weapon.WeaponType
import net.nyrheim.penandpaper.money.Money
import net.nyrheim.penandpaper.weight.Weight

sealed class ItemTypeDTO {
    abstract val name: String
    abstract val displayName: String
    abstract val cost: Money
    abstract val weight: Weight
    companion object {
        operator fun invoke(itemType: ItemType): ItemTypeDTO {
            return when (itemType) {
                is AdventuringGearType -> AdventuringGearTypeDTO(itemType)
                is ArmorType -> ArmorTypeDTO(itemType)
                is WeaponType -> WeaponTypeDTO(itemType)
                else -> GenericItemTypeDTO(itemType)
            }
        }
    }
}

data class AdventuringGearTypeDTO(
    override val name: String,
    override val displayName: String,
    override val cost: Money,
    override val weight: Weight
) : ItemTypeDTO() {
    constructor(adventuringGearType: AdventuringGearType) : this(
        adventuringGearType.toString(),
        adventuringGearType.getName(),
        adventuringGearType.cost,
        adventuringGearType.weight
    )
}

class ArmorTypeDTO(
    override val name: String,
    override val displayName: String,
    val category: ArmorCategoryDTO,
    override val cost: Money,
    val armorClass: String?,
    val strengthRequirement: StrengthRequirement?,
    val disadvantageToStealthChecks: Boolean,
    override val weight: Weight,
    val isMetal: Boolean
) : ItemTypeDTO() {
    constructor(armorType: ArmorType) : this(
        armorType.toString(),
        armorType.getName(),
        ArmorCategoryDTO(armorType.category),
        armorType.cost,
        armorType.armorClass?.toString(),
        armorType.strengthRequirement,
        armorType.isDisadvantageToStealthChecks,
        armorType.weight,
        armorType.isMetal
    )
}

data class WeaponTypeDTO(
    override val name: String,
    override val displayName: String,
    val category: WeaponCategoryDTO,
    override val cost: Money,
    val damage: WeaponDamageDTO?,
    override val weight: Weight,
    val properties: List<WeaponPropertyDTO>
) : ItemTypeDTO() {
    constructor(weaponType: WeaponType) : this(
        weaponType.toString(),
        weaponType.getName(),
        WeaponCategoryDTO(weaponType.category),
        weaponType.cost,
        weaponType.damage?.let(::WeaponDamageDTO),
        weaponType.weight,
        weaponType.properties.mapNotNull { weaponProperty -> WeaponPropertyDTO(weaponProperty) }
    )
}

data class GenericItemTypeDTO(
    override val name: String,
    override val displayName: String,
    override val cost: Money,
    override val weight: Weight
) : ItemTypeDTO() {
    constructor(itemType: ItemType) : this(
        itemType.toString(),
        itemType.name,
        itemType.cost,
        itemType.weight
    )
}