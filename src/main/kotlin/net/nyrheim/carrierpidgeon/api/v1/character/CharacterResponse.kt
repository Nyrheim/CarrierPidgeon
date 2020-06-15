package net.nyrheim.carrierpidgeon.api.v1.character

import net.nyrheim.penandpaper.ability.Ability
import net.nyrheim.penandpaper.character.PenCharacter
import org.http4k.core.Body
import org.http4k.format.Gson.auto

data class CharacterResponse(
    val id: Int,
    val playerId: Int,
    val name: String,
    val height: String,
    val weight: String,
    val appearance: String,
    val presence: String,
    val age: Int,
    val experience: Int,
    val exhaustion: Int,
    val abilityScores: Map<Ability, Int>,
    val tempScores: Map<Ability, Int>,
    val abilityScoreChoices: Map<Ability, Int>,
    val firstClass: String?,
    val classes: Map<String, Int>,
    val race: String?,
    val hp: Int,
    val maxHp: Int,
    val tempHp: Int
) {
    constructor(character: PenCharacter) : this(
        character.id.value,
        character.playerId.value,
        character.name,
        character.height,
        character.weight,
        character.appearance,
        character.presence,
        character.age,
        character.experience,
        character.exhaustion,
        Ability.values()
            .map { ability ->
                ability to character.getAbilityScore(ability)
            }
            .toMap(),
        Ability.values()
            .map { ability ->
                ability to character.getTempScore(ability)
            }
            .toMap(),
        Ability.values()
            .map { ability ->
                ability to character.getAbilityScoreChoice(ability)
            }
            .toMap(),
        character.firstClass?.name,
        character.classes()
            .map { `class` ->
                `class`.clazz.name to `class`.level
            }
            .toMap(),
        character.race?.name,
        character.hp,
        character.maxHP,
        character.tempHP
    )

    companion object {
        val lens = Body.auto<CharacterResponse>().toLens()
    }
}