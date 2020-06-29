package net.nyrheim.carrierpidgeon.api.v1.tokenexchange

import net.nyrheim.carrierpidgeon.PidgeonGson.auto
import org.http4k.core.Body

data class TokenExchangeResponse(val jwt: String) {
    companion object {
        val lens = Body.auto<TokenExchangeResponse>().toLens()
    }
}