package net.nyrheim.carrierpidgeon

import org.http4k.core.Body
import org.http4k.format.Gson.auto

data class ErrorInfo(val error: String) {
    companion object {
        val lens = Body.auto<ErrorInfo>().toLens()
    }
}