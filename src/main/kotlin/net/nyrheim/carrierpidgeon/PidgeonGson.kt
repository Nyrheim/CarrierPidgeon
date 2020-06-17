package net.nyrheim.carrierpidgeon

import com.google.gson.GsonBuilder
import net.nyrheim.penandpaper.dice.Roll
import net.nyrheim.penandpaper.dice.Roll.parse
import org.http4k.format.AutoMappingConfiguration
import org.http4k.format.ConfigurableGson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping

object PidgeonGson : ConfigurableGson(
    GsonBuilder()
        .asConfigurable()
        .withStandardMappings()
        .withPidgeonMappings()
        .done()
)

fun AutoMappingConfiguration<GsonBuilder>.withPidgeonMappings()= apply {
    text(BiDiMapping(Roll::parse, Roll::toString))
}