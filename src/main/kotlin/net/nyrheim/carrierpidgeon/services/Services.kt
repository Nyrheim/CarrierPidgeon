package net.nyrheim.carrierpidgeon.services

import org.bukkit.Bukkit
import kotlin.reflect.KClass

object Services {

    operator fun <T: Any> get(type: KClass<T>): T? {
        return Bukkit.getServicesManager().getRegistration(type.java)?.provider
    }

}