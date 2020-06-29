package net.nyrheim.carrierpidgeon

import com.rpkit.core.bukkit.plugin.RPKBukkitPlugin
import net.nyrheim.carrierpidgeon.api.v1.character.CharacterHandler
import net.nyrheim.carrierpidgeon.api.v1.classes.ClassesHandler
import net.nyrheim.carrierpidgeon.api.v1.items.ItemsHandler
import net.nyrheim.carrierpidgeon.api.v1.login.LoginHandler
import net.nyrheim.carrierpidgeon.api.v1.races.RacesHandler
import net.nyrheim.carrierpidgeon.api.v1.tokenexchange.TokenExchangeHandler
import net.nyrheim.carrierpidgeon.auth.Authenticated
import net.nyrheim.carrierpidgeon.auth.Authenticator
import org.http4k.core.Method.*
import org.http4k.core.then
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import kotlin.concurrent.thread

class CarrierPidgeon : RPKBukkitPlugin() {

    override fun onEnable() {
        saveDefaultConfig()
        val authenticator = Authenticator(this)
        val loginHandler = LoginHandler(authenticator)
        val tokenExchangeHandler = TokenExchangeHandler(this, authenticator)
        val characterHandler = CharacterHandler(this, authenticator)
        val itemsHandler = ItemsHandler()
        val classesHandler = ClassesHandler()
        val racesHandler = RacesHandler()
        val routes = routes(
            "/api" bind routes(
                "/v1" bind routes(
                    "/login" bind POST to loginHandler::post,
                    "/tokenexchange" bind POST to tokenExchangeHandler::post,
                    "/character/{id}" bind GET to Authenticated(authenticator).then(characterHandler::get),
                    "/character" bind POST to Authenticated(authenticator).then(characterHandler::post),
                    "/character/{id}" bind PATCH to Authenticated(authenticator).then(characterHandler::patch),
                    "/character/{id}" bind PUT to Authenticated(authenticator).then(characterHandler::put),
                    "/items" bind GET to itemsHandler::get,
                    "/classes" bind GET to classesHandler::get,
                    "/races" bind GET to racesHandler::get
                )
            )
        )
        thread { routes.asServer(Jetty(config.getInt("web.port"))).start() }
    }

}