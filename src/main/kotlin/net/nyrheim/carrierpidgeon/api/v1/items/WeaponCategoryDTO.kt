package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.item.weapon.WeaponCategory

data class WeaponCategoryDTO(
    val isSimple: Boolean,
    val isMartial: Boolean,
    val isMelee: Boolean,
    val isRanged: Boolean
) {
    constructor(weaponCategory: WeaponCategory) : this(
        weaponCategory.isSimple,
        weaponCategory.isMartial,
        weaponCategory.isMelee,
        weaponCategory.isRanged
    )
}