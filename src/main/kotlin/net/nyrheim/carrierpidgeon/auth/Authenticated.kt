package net.nyrheim.carrierpidgeon.auth

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.UNAUTHORIZED

class Authenticated(val authenticator: Authenticator) : Filter {

    override fun invoke(next: HttpHandler): HttpHandler {
        return { request -> handle(next, request) }
    }

    fun handle(next: HttpHandler, request: Request): Response {
        try {
            request.authorizationToken()?.let { authenticator.verify(it) }
        } catch (exception: Exception) {
            return Response(UNAUTHORIZED)
        } ?: return Response(UNAUTHORIZED)
        return next(request)
    }

}