package net.nyrheim.carrierpidgeon

import org.http4k.core.Body
import net.nyrheim.carrierpidgeon.PidgeonGson.auto

data class ErrorInfo(val error: String) {
    companion object {
        val lens = Body.auto<ErrorInfo>().toLens()
    }
}