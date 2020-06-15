package net.nyrheim.carrierpidgeon.api.v1.character

import net.nyrheim.penandpaper.ability.Ability
import org.http4k.core.Body
import org.http4k.format.Gson.auto

data class CharacterPatchRequest(
    val firstName: String?,
    val familyName: String?,
    val height: String?,
    val weight: String?,
    val appearance: String?,
    val presence: String?,
    val age: Int?,
    val abilityScores: Map<Ability, Int>?,
    val `class`: String?,
    val race: String?
) {
    companion object {
        val lens = Body.auto<CharacterPatchRequest>().toLens()
    }
}