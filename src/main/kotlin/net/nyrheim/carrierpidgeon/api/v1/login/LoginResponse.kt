package net.nyrheim.carrierpidgeon.api.v1.login

import org.http4k.core.Body
import net.nyrheim.carrierpidgeon.PidgeonGson.auto

data class LoginResponse(val jwt: String) {
    companion object {
        val lens = Body.auto<LoginResponse>().toLens()
    }
}