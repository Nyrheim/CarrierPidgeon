package net.nyrheim.carrierpidgeon.api.v1.items

import net.nyrheim.penandpaper.item.ItemType
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

class ItemsHandler {

    fun get(request: Request): Response {
        return Response(OK).with(ItemsResponse.lens of ItemsResponse(ItemType.values().toList()))
    }

}