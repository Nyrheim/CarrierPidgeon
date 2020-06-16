package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.item.armor.ArmorCategory
import java.time.Duration

data class ArmorCategoryDTO(
    val name: String,
    val donTime: Duration?,
    val doffTime: Duration?
) {
    constructor(armorCategory: ArmorCategory) : this(
        armorCategory.getName(),
        null,
        null
    )
}
