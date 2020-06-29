package net.nyrheim.carrierpidgeon.api.v1.tokenexchange

import com.rpkit.permissions.bukkit.group.RPKGroupProvider
import com.rpkit.players.bukkit.profile.RPKProfileProvider
import net.nyrheim.carrierpidgeon.CarrierPidgeon
import net.nyrheim.carrierpidgeon.ErrorInfo
import net.nyrheim.carrierpidgeon.auth.Authenticator
import net.nyrheim.carrierpidgeon.auth.authorizationToken
import net.nyrheim.carrierpidgeon.auth.playerId
import net.nyrheim.carrierpidgeon.services.Services
import net.nyrheim.penandpaper.player.PenPlayerService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with

class TokenExchangeHandler(private val plugin: CarrierPidgeon, private val authenticator: Authenticator) {

    fun post(request: Request): Response {
        val token = request.authorizationToken()
            ?: return Response(UNAUTHORIZED)
        val playerId = request.playerId(authenticator)
            ?: return Response(UNAUTHORIZED)
        val profileProvider = plugin.core.serviceManager.getServiceProvider(RPKProfileProvider::class.java)
        val profile = profileProvider.getProfile(playerId.value) ?: return Response(UNAUTHORIZED)
        val permissionsProvider = plugin.core.serviceManager.getServiceProvider(RPKGroupProvider::class.java)
        if (!permissionsProvider.hasPermission(profile, "carrierpidgeon.onbehalfof")) {
            return Response(UNAUTHORIZED)
        }
        val exchangeRequest = TokenExchangeRequest.lens(request)
        val username = exchangeRequest.username
        val playerService = Services[PenPlayerService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Player service unavailable"))
        val oboPlayerId = playerService.getPlayer(plugin.server.getOfflinePlayer(username)).playerId
        return Response(OK).with(
            TokenExchangeResponse.lens of TokenExchangeResponse(authenticator.exchangeForOnBehalfOf(token, oboPlayerId))
        )
    }
}