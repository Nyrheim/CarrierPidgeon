package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.distance.Distance
import net.nyrheim.penandpaper.item.weapon.property.WeaponProperty

sealed class WeaponPropertyDTO(
    val name: String
) {
    companion object {
        operator fun invoke(property: WeaponProperty): WeaponPropertyDTO? {
            return when (property) {
                is WeaponProperty.Versatile -> Versatile(property)
                is WeaponProperty.TwoHanded -> TwoHanded()
                is WeaponProperty.Thrown -> Thrown()
                is WeaponProperty.Special -> Special(property)
                is WeaponProperty.Reach -> Reach()
                is WeaponProperty.Range -> Range(property)
                is WeaponProperty.Loading -> Loading()
                is WeaponProperty.Light -> Light()
                is WeaponProperty.Heavy -> Heavy()
                is WeaponProperty.Finesse -> Finesse()
                is WeaponProperty.Ammunition -> Ammunition()
                else -> null
            }
        }
    }
}

data class Versatile(
    val twoHandedRoll: String
) : WeaponPropertyDTO("Versatile") {
    constructor(property: WeaponProperty.Versatile) : this(property.twoHandedRoll.toString())
}

class TwoHanded : WeaponPropertyDTO("Two-handed")

class Thrown : WeaponPropertyDTO("Thrown")

data class Special(
    val description: String
) : WeaponPropertyDTO("Special") {
    constructor(property: WeaponProperty.Special) : this(property.description)
}

class Reach : WeaponPropertyDTO("Reach")

data class Range(
    val normalRange: Distance,
    val longRange: Distance
) : WeaponPropertyDTO("Range") {
    constructor(property: WeaponProperty.Range) : this(
        property.normalRange,
        property.longRange
    )
}

class Loading : WeaponPropertyDTO("Loading")

class Light : WeaponPropertyDTO("Light")

class Heavy : WeaponPropertyDTO("Heavy")

class Finesse : WeaponPropertyDTO("Finesse")

class Ammunition : WeaponPropertyDTO("Ammunition")