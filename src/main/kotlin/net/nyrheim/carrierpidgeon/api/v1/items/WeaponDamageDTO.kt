package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.damage.DamageType
import net.nyrheim.penandpaper.dice.Roll
import net.nyrheim.penandpaper.item.weapon.WeaponDamage

data class WeaponDamageDTO(
    val roll: Roll,
    val damageType: DamageType
) {
    constructor(weaponDamage: WeaponDamage) : this(weaponDamage.roll, weaponDamage.damageType)
}