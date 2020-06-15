package net.nyrheim.carrierpidgeon

import net.nyrheim.carrierpidgeon.api.v1.character.CharacterHandler
import net.nyrheim.carrierpidgeon.api.v1.login.LoginHandler
import net.nyrheim.carrierpidgeon.auth.Authenticated
import net.nyrheim.carrierpidgeon.auth.Authenticator
import org.bukkit.plugin.java.JavaPlugin
import org.http4k.core.Method.*
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import kotlin.concurrent.thread

class CarrierPidgeon : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        val authenticator = Authenticator(this)
        val characterHandler = CharacterHandler(this, authenticator)
        val loginHandler = LoginHandler(authenticator)
        val routes = routes(
            "/api" bind routes(
                "/v1" bind routes(
                    "/login" bind POST to loginHandler::post,
                    "/character/{id}" bind GET to Authenticated(authenticator).then(characterHandler::get),
                    "/character" bind POST to Authenticated(authenticator).then(characterHandler::post),
                    "/character/{id}" bind PATCH to Authenticated(authenticator).then(characterHandler::patch),
                    "/character/{id}" bind PUT to Authenticated(authenticator).then(characterHandler::put)
                )
            )
        )
        thread { routes.asServer(Jetty(config.getInt("web.port"))).start() }
    }

}