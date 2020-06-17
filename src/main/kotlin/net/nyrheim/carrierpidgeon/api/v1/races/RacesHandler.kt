package net.nyrheim.carrierpidgeon.api.v1.races

import net.nyrheim.carrierpidgeon.ErrorInfo
import net.nyrheim.carrierpidgeon.services.Services
import net.nyrheim.penandpaper.race.PenRaceService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

class RacesHandler {

    fun get(request: Request): Response {
        val raceService = Services[PenRaceService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Race service unavailable"))
        return Response(OK).with(RacesResponse.lens of RacesResponse(raceService.races))
    }

}