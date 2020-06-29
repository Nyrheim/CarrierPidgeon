package net.nyrheim.carrierpidgeon.api.v1.character

import net.nyrheim.carrierpidgeon.CarrierPidgeon
import net.nyrheim.carrierpidgeon.ErrorInfo
import net.nyrheim.carrierpidgeon.auth.Authenticator
import net.nyrheim.carrierpidgeon.auth.playerId
import net.nyrheim.carrierpidgeon.services.Services
import net.nyrheim.penandpaper.PenAndPaper
import net.nyrheim.penandpaper.ability.Ability
import net.nyrheim.penandpaper.ability.AbilityScoreCostLookupTable
import net.nyrheim.penandpaper.character.CharacterId
import net.nyrheim.penandpaper.character.PenCharacter
import net.nyrheim.penandpaper.character.PenCharacterService
import net.nyrheim.penandpaper.clazz.PenClass
import net.nyrheim.penandpaper.player.PenPlayerService
import net.nyrheim.penandpaper.race.PenRaceService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with
import org.http4k.routing.path

class CharacterHandler(private val plugin: CarrierPidgeon, private val authenticator: Authenticator) {

    fun get(request: Request): Response {
        val characterService = Services[PenCharacterService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Character service unavailable"))
        val characterId = request.path("id")?.toIntOrNull() ?: return Response(NOT_FOUND)
        val character = characterService.getCharacter(CharacterId(characterId))
            ?: return Response(NOT_FOUND)
        val playerId = request.playerId(authenticator)
            ?: return Response(UNAUTHORIZED)
        if (character.playerId != playerId) return Response(FORBIDDEN)
        return Response(OK)
            .with(CharacterResponse.lens of CharacterResponse(character))
    }

    fun post(request: Request): Response {
        val playerId = request.playerId(authenticator)
            ?: return Response(FORBIDDEN)
        val penAndPaper = plugin.server.pluginManager.getPlugin("PenAndPaper") as? PenAndPaper
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("PenAndPaper not found"))
        val playerService = Services[PenPlayerService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
        playerService.getPlayer(playerId)
            ?: return Response(FORBIDDEN)
        val characterService = Services[PenCharacterService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Character service unavailable"))
        val raceService = Services[PenRaceService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Race service unavailable"))
        val characterRequest = CharacterPostRequest.lens(request)
        if (characterRequest.name?.length ?: 0 > 128) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Name must not be longer than 128 characters"))
        if (characterRequest.height?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Height must not be longer than 16 characters"))
        if (characterRequest.weight?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Weight must not be longer than 16 characters"))
        if (characterRequest.appearance?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Appearance must not be longer than 4096 characters"))
        if (characterRequest.presence?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Presence must not be longer than 4096 characters"))
        val race = if (characterRequest.race == null) null else raceService.getRace(characterRequest.race)
        if (characterRequest.age ?: 20 < race?.minimumAge ?: 16 && characterRequest.age ?: 20 > 0) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be greater than ${race?.minimumAge ?: 16}"))
        if (characterRequest.age ?: 20 > race?.maximumAge ?: 2000) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be less than ${race?.maximumAge ?: 2000}"))
        if (characterRequest.abilityScores.any { entry -> entry.value > 15 }) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may exceed 15"))
        if (characterRequest.abilityScores.any { entry -> entry.value < 8 }) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may be under 8"))
        if (characterRequest.abilityScores
                .map { score -> score.value }
                .sumBy { AbilityScoreCostLookupTable.getAbilityScoreCost(it) }
            > AbilityScoreCostLookupTable.MAX_ABILITY_COST) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Ability scores must be lower."))
        val `class` = if (characterRequest.`class` != null) {
            PenClass.valueOf(characterRequest.`class`)
                ?: return Response(BAD_REQUEST)
                    .with(ErrorInfo.lens of ErrorInfo("Invalid class"))
        } else {
            null
        }
        val character = PenCharacter(
            penAndPaper,
            playerId
        )
        character.name = characterRequest.name ?: ""
        character.height = characterRequest.height ?: ""
        character.weight = characterRequest.weight ?: ""
        character.appearance = characterRequest.appearance ?: ""
        character.presence = characterRequest.presence ?: ""
        character.age = characterRequest.age ?: 20
        Ability.values().forEach { ability ->
            character.setAbilityScore(ability, characterRequest.abilityScores[ability] ?: 0)
            character.setAbilityScoreChoice(ability, characterRequest.abilityScores[ability] ?: 0)
        }
        if (`class` != null) {
            character.addClass(`class`)
        }
        character.race = race
        characterService.addCharacter(character)
        return Response(CREATED)
            .header("Location", "/api/v1/character/" + character.id.value)
            .with(CharacterResponse.lens of CharacterResponse(character))
    }

    fun put(request: Request): Response {
        val playerId = request.playerId(authenticator)
            ?: return Response(FORBIDDEN)
        val playerService = Services[PenPlayerService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
        playerService.getPlayer(playerId)
            ?: return Response(FORBIDDEN)
        val characterService = Services[PenCharacterService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Character service unavailable"))
        val raceService = Services[PenRaceService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Race service unavailable"))
        val characterId = request.path("id")?.toIntOrNull() ?: return Response(NOT_FOUND)
        val character = characterService.getCharacter(CharacterId(characterId))
            ?: return Response(NOT_FOUND)
        if (character.playerId != playerId)
            return Response(FORBIDDEN)
        val characterRequest = CharacterPutRequest.lens(request)
        if (characterRequest.name?.length ?: 0 > 128) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("First name must not be longer than 64 characters"))
        if (characterRequest.height?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Height must not be longer than 16 characters"))
        if (characterRequest.weight?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Weight must not be longer than 16 characters"))
        if (characterRequest.appearance?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Appearance must not be longer than 4096 characters"))
        if (characterRequest.presence?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Presence must not be longer than 4096 characters"))
        val race = if (characterRequest.race == null) null else raceService.getRace(characterRequest.race)
        if (characterRequest.age ?: 20 < race?.minimumAge ?: 16 && characterRequest.age ?: 20 > 0) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be greater than ${race?.minimumAge ?: 16}"))
        if (characterRequest.age ?: 20 > race?.maximumAge ?: 2000) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be less than ${race?.maximumAge ?: 2000}"))
        if (characterRequest.abilityScores.any { entry -> entry.value > 15 }) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may exceed 15"))
        if (characterRequest.abilityScores.any { entry -> entry.value < 8 }) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may be under 8"))
        if (characterRequest.abilityScores
                .map { score -> score.value }
                .sumBy { AbilityScoreCostLookupTable.getAbilityScoreCost(it) }
            > AbilityScoreCostLookupTable.MAX_ABILITY_COST) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Ability scores must be lower."))
        val `class` = if (characterRequest.`class` != null) {
            PenClass.valueOf(characterRequest.`class`)
                ?: return Response(BAD_REQUEST)
                    .with(ErrorInfo.lens of ErrorInfo("Invalid class"))
        } else {
            null
        }
        if (character.classes().isNotEmpty() && character.clazz(`class`) == null) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Class locked to " + character.firstClass.name))
        character.name = characterRequest.name ?: ""
        character.height = characterRequest.height ?: ""
        character.weight = characterRequest.weight ?: ""
        character.appearance = characterRequest.appearance ?: ""
        character.presence = characterRequest.presence ?: ""
        character.age = characterRequest.age ?: 20
        Ability.values().forEach { ability ->
            character.setAbilityScore(ability, characterRequest.abilityScores[ability] ?: 0)
            character.setAbilityScoreChoice(ability, characterRequest.abilityScores[ability] ?: 0)
        }
        if (character.classes().isEmpty() && `class` != null) {
            character.addClass(`class`)
        }
        character.race = race
        characterService.updateCharacter(character)
        return Response(NO_CONTENT)
    }

    fun patch(request: Request): Response {
        val playerId = request.playerId(authenticator)
            ?: return Response(FORBIDDEN)
        val playerService = Services[PenPlayerService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
        playerService.getPlayer(playerId)
            ?: return Response(FORBIDDEN)
        val characterService = Services[PenCharacterService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Character service unavailable"))
        val raceService = Services[PenRaceService::class]
            ?: return Response(INTERNAL_SERVER_ERROR)
                .with(ErrorInfo.lens of ErrorInfo("Race service unavailable"))
        val characterId = request.path("id")?.toIntOrNull() ?: return Response(NOT_FOUND)
        val character = characterService.getCharacter(CharacterId(characterId))
            ?: return Response(NOT_FOUND)
        if (character.playerId != playerId)
            return Response(FORBIDDEN)
        val characterRequest = CharacterPatchRequest.lens(request)
        if (characterRequest.name?.length ?: 0 > 128) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Name must not be longer than 64 characters"))
        if (characterRequest.height?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Height must not be longer than 16 characters"))
        if (characterRequest.weight?.length ?: 0 > 16) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Weight must not be longer than 16 characters"))
        if (characterRequest.appearance?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Appearance must not be longer than 4096 characters"))
        if (characterRequest.presence?.length ?: 0 > 4096) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Presence must not be longer than 4096 characters"))
        val race = if (characterRequest.race == null) null else raceService.getRace(characterRequest.race)
        if (characterRequest.age ?: 20 < race?.minimumAge ?: 16 && characterRequest.age ?: 20 > 0) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be greater than ${race?.minimumAge ?: 16}"))
        if (characterRequest.age ?: 20 > race?.maximumAge ?: 2000) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Age must be less than ${race?.maximumAge ?: 2000}"))
        if (characterRequest.abilityScores?.any { entry -> entry.value > 15 } == true) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may exceed 15"))
        if (characterRequest.abilityScores?.any { entry -> entry.value < 8 } == true) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("No ability score may be under 8"))
        if (characterRequest.abilityScores
                ?.map { score -> score.value }
                ?.sumBy { AbilityScoreCostLookupTable.getAbilityScoreCost(it) }
                ?: 0
            > AbilityScoreCostLookupTable.MAX_ABILITY_COST) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Ability scores must be lower."))
        val `class` = if (characterRequest.`class` != null) {
            PenClass.valueOf(characterRequest.`class`)
                ?: return Response(BAD_REQUEST)
                    .with(ErrorInfo.lens of ErrorInfo("Invalid class"))
        } else {
            null
        }
        if (character.classes().isNotEmpty() && character.clazz(`class`) == null) return Response(BAD_REQUEST)
            .with(ErrorInfo.lens of ErrorInfo("Class locked to " + character.firstClass.name))
        character.name = characterRequest.name ?: character.name
        character.height = characterRequest.height ?: character.height
        character.weight = characterRequest.weight ?: character.weight
        character.appearance = characterRequest.appearance ?: character.appearance
        character.presence = characterRequest.presence ?: character.presence
        character.age = characterRequest.age ?: character.age
        Ability.values().forEach { ability ->
            character.setAbilityScore(ability, characterRequest.abilityScores?.get(ability) ?: character.getAbilityScore(ability))
            character.setAbilityScoreChoice(ability, characterRequest.abilityScores?.get(ability) ?: character.getAbilityScoreChoice(ability))
        }
        if (character.classes().isEmpty() && `class` != null) {
            character.addClass(`class`)
        }
        character.race = race ?: character.race
        characterService.updateCharacter(character)
        return Response(NO_CONTENT)
    }

}