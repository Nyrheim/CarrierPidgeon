package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.damage.DamageType
import net.nyrheim.penandpaper.item.weapon.WeaponDamage

data class WeaponDamageDTO(
    val roll: String,
    val damageType: DamageType
) {
    constructor(weaponDamage: WeaponDamage) : this(weaponDamage.roll.toString(), weaponDamage.damageType)
}