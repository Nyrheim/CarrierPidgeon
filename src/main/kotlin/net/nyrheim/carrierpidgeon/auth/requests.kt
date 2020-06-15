package net.nyrheim.carrierpidgeon.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import net.nyrheim.penandpaper.player.PlayerId
import org.http4k.core.HttpMessage
import org.http4k.core.Request

fun Request.playerId(authenticator: Authenticator): PlayerId? = token(authenticator)?.playerId

fun Request.token(authenticator: Authenticator): Token? = authorizationToken()?.let { authenticator.verify(it) }?.toToken()

fun HttpMessage.authorizationToken(scheme: String = "Bearer") = header("Authorization")?.let {
        if (it.startsWith("$scheme ", ignoreCase = true)) {
                it.substring(scheme.length + 1)
        } else null
}

fun Jws<Claims>.toToken(): Token = if (body.containsKey("player_id")) {
        Token(PlayerId(body.subject.toInt()), PlayerId((body["player_id"] as Double).toInt()))
} else {
        Token(PlayerId(body.subject.toInt()), PlayerId(body.subject.toInt()))
}
