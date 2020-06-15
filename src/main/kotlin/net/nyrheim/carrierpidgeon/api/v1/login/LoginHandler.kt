package net.nyrheim.carrierpidgeon.api.v1.login

import net.nyrheim.carrierpidgeon.auth.Authenticator
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with

class LoginHandler(val authenticator: Authenticator) {

    fun post(request: Request): Response {
        val loginRequest = LoginRequest.lens(request)
        val username = loginRequest.username
        val password = loginRequest.password
        val jwt = authenticator.authenticate(username, password)
        return if (jwt != null) {
            Response(Status.OK)
                .with(LoginResponse.lens of LoginResponse(jwt))
        } else {
            Response(Status.UNAUTHORIZED)
        }
    }

}