package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.item.ItemType
import org.http4k.core.Body
import net.nyrheim.carrierpidgeon.PidgeonGson.auto

class ItemsResponse(items: List<ItemType>) {

    val items: List<ItemTypeDTO> = items.map { itemType -> ItemTypeDTO(itemType) }

    companion object {
        val lens = Body.auto<ItemsResponse>().toLens()
    }
}