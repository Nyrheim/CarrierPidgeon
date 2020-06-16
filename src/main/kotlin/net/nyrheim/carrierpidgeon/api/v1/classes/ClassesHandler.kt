package net.nyrheim.carrierpidgeon.api.v1.classes

import net.nyrheim.penandpaper.clazz.PenClass
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

class ClassesHandler {

    fun get(request: Request): Response {
        return Response(OK).with(ClassesResponse.lens of ClassesResponse(PenClass.values().toList()))
    }

}