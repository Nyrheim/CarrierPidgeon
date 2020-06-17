package net.nyrheim.carrierpidgeon.api.v1.races

import net.nyrheim.penandpaper.race.Race
import org.http4k.core.Body
import net.nyrheim.carrierpidgeon.PidgeonGson.auto

data class RacesResponse(val races: List<Race>) {
    companion object {
        val lens = Body.auto<RacesResponse>().toLens()
    }
}