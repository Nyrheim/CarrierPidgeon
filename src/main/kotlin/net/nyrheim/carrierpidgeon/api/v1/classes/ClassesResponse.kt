package net.nyrheim.carrierpidgeon.api.v1.classes

import net.nyrheim.penandpaper.clazz.PenClass
import org.http4k.core.Body
import net.nyrheim.carrierpidgeon.PidgeonGson.auto

data class ClassesResponse(val classes: List<PenClass>) {
    companion object {
        val lens = Body.auto<ClassesResponse>().toLens()
    }
}