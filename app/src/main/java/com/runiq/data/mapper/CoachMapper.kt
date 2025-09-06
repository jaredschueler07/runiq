package com.runiq.data.mapper

import com.runiq.data.local.entities.CoachEntity
import com.runiq.data.local.entities.CoachTextLineEntity
import com.runiq.data.local.entities.CoachingStyle
import com.runiq.data.local.entities.ExperienceLevel
import com.runiq.data.local.entities.GpsTrackPointEntity
import com.runiq.data.local.entities.MotivationStyle
import com.runiq.domain.model.Coach
import com.runiq.domain.model.GpsTrackPoint
import com.runiq.domain.model.VoiceCharacteristics
import com.runiq.domain.repository.CoachingTextLine
import com.runiq.domain.repository.EmotionalTone

/**
 * Extension functions for mapping between Coach entity and domain models
 */

// Entity to Domain mapping
fun CoachEntity.toDomain(): Coach {
    return Coach(
        id = this.id,
        name = this.name,
        personality = mapPersonalityFromEntity(this.personalityTraits, this.coachingStyle),
        specialization = mapSpecializationFromEntity(this.specializations),
        voiceCharacteristics = mapVoiceCharacteristicsFromEntity(this.voiceCharacteristics),
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// List extension
fun List<CoachEntity>.toDomain(): List<Coach> = map { it.toDomain() }

// Domain to Entity mapping
fun Coach.toEntity(): CoachEntity {
    return CoachEntity(
        id = this.id,
        name = this.name,
        description = "${this.personality.name} coach specializing in ${this.specialization.name}",
        coachingStyle = mapPersonalityToCoachingStyle(this.personality),
        personalityTraits = listOf(this.personality.name.lowercase()),
        voiceCharacteristics = mapVoiceCharacteristicsToEntity(this.voiceCharacteristics),
        specializations = listOf(this.specialization.name.lowercase()),
        experienceLevel = ExperienceLevel.INTERMEDIATE,
        motivationStyle = MotivationStyle.BALANCED,
        isActive = this.isActive,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// List extension
fun List<Coach>.toEntity(): List<CoachEntity> = map { it.toEntity() }

// Helper mapping functions
private fun mapPersonalityFromEntity(traits: List<String>, coachingStyle: CoachingStyle): Coach.Personality {
    return when (coachingStyle) {
        CoachingStyle.MOTIVATIONAL -> Coach.Personality.MOTIVATIONAL
        CoachingStyle.ANALYTICAL -> Coach.Personality.ANALYTICAL
        CoachingStyle.SUPPORTIVE -> Coach.Personality.SUPPORTIVE
        CoachingStyle.TOUGH_LOVE,
        CoachingStyle.COMPETITIVE -> Coach.Personality.COMPETITIVE
        CoachingStyle.ZEN,
        CoachingStyle.EDUCATIONAL,
        CoachingStyle.ADAPTIVE -> Coach.Personality.ZEN
    }
}

private fun mapSpecializationFromEntity(specializations: List<String>): Coach.Specialization {
    return when (specializations.firstOrNull()?.lowercase()) {
        "beginner" -> Coach.Specialization.BEGINNER
        "intermediate" -> Coach.Specialization.INTERMEDIATE
        "advanced" -> Coach.Specialization.ADVANCED
        "marathon" -> Coach.Specialization.MARATHON
        "sprint" -> Coach.Specialization.SPRINT
        "endurance" -> Coach.Specialization.ENDURANCE
        "recovery" -> Coach.Specialization.RECOVERY
        else -> Coach.Specialization.INTERMEDIATE
    }
}

private fun mapPersonalityToCoachingStyle(personality: Coach.Personality): CoachingStyle {
    return when (personality) {
        Coach.Personality.MOTIVATIONAL -> CoachingStyle.MOTIVATIONAL
        Coach.Personality.ANALYTICAL -> CoachingStyle.ANALYTICAL
        Coach.Personality.SUPPORTIVE -> CoachingStyle.SUPPORTIVE
        Coach.Personality.COMPETITIVE -> CoachingStyle.COMPETITIVE
        Coach.Personality.ZEN -> CoachingStyle.ZEN
    }
}

// VoiceCharacteristics mapping functions
private fun mapVoiceCharacteristicsFromEntity(entity: VoiceCharacteristics): Coach.VoiceCharacteristics {
    return Coach.VoiceCharacteristics(
        voiceId = entity.voiceId,
        stability = entity.stability,
        clarity = entity.similarityBoost, // Using similarityBoost as clarity proxy
        style = entity.style,
        speakingRate = 1.0f // Default value as it's not in entity
    )
}

private fun mapVoiceCharacteristicsToEntity(domain: Coach.VoiceCharacteristics): VoiceCharacteristics {
    return VoiceCharacteristics(
        voiceId = domain.voiceId,
        voiceName = "Voice ${domain.voiceId}", // Generate a default name
        stability = domain.stability,
        similarityBoost = domain.clarity, // Using clarity as similarityBoost proxy
        style = domain.style,
        useSpeakerBoost = true // Default value
    )
}

// === CoachTextLine Mapping Functions ===

// Entity to Domain mapping
fun CoachTextLineEntity.toDomain(): CoachingTextLine {
    return CoachingTextLine(
        id = this.id,
        coachId = this.coachId,
        text = this.text,
        category = this.category,
        conditions = this.conditions,
        templateVariables = this.templateVariables,
        priority = this.priority,
        minIntervalSeconds = this.minIntervalSeconds,
        maxUsesPerRun = this.maxUsesPerRun,
        isActive = this.isActive,
        usageCount = this.usageCount,
        effectivenessScore = this.effectivenessScore,
        languageCode = this.languageCode,
        tags = this.tags,
        emotionalTone = this.emotionalTone.toDomain()
    )
}

// List extension
fun List<CoachTextLineEntity>.toDomain(): List<CoachingTextLine> = map { it.toDomain() }

// Domain to Entity mapping
fun CoachingTextLine.toEntity(): CoachTextLineEntity {
    return CoachTextLineEntity(
        id = this.id,
        coachId = this.coachId,
        text = this.text,
        category = this.category,
        conditions = this.conditions,
        templateVariables = this.templateVariables,
        priority = this.priority,
        minIntervalSeconds = this.minIntervalSeconds,
        maxUsesPerRun = this.maxUsesPerRun,
        isActive = this.isActive,
        usageCount = this.usageCount,
        effectivenessScore = this.effectivenessScore,
        languageCode = this.languageCode,
        tags = this.tags,
        emotionalTone = this.emotionalTone.toEntity()
    )
}

// List extension
fun List<CoachingTextLine>.toEntity(): List<CoachTextLineEntity> = map { it.toEntity() }

// EmotionalTone mappings
private fun com.runiq.data.local.entities.EmotionalTone.toDomain(): EmotionalTone {
    return when (this) {
        com.runiq.data.local.entities.EmotionalTone.ENCOURAGING -> EmotionalTone.ENCOURAGING
        com.runiq.data.local.entities.EmotionalTone.CHALLENGING -> EmotionalTone.CHALLENGING
        com.runiq.data.local.entities.EmotionalTone.CALMING -> EmotionalTone.CALMING
        com.runiq.data.local.entities.EmotionalTone.ENERGETIC -> EmotionalTone.ENERGETIC
        com.runiq.data.local.entities.EmotionalTone.NEUTRAL -> EmotionalTone.NEUTRAL
        com.runiq.data.local.entities.EmotionalTone.HUMOROUS -> EmotionalTone.HUMOROUS
        com.runiq.data.local.entities.EmotionalTone.SERIOUS -> EmotionalTone.SERIOUS
    }
}

private fun EmotionalTone.toEntity(): com.runiq.data.local.entities.EmotionalTone {
    return when (this) {
        EmotionalTone.ENCOURAGING -> com.runiq.data.local.entities.EmotionalTone.ENCOURAGING
        EmotionalTone.CHALLENGING -> com.runiq.data.local.entities.EmotionalTone.CHALLENGING
        EmotionalTone.CALMING -> com.runiq.data.local.entities.EmotionalTone.CALMING
        EmotionalTone.ENERGETIC -> com.runiq.data.local.entities.EmotionalTone.ENERGETIC
        EmotionalTone.NEUTRAL -> com.runiq.data.local.entities.EmotionalTone.NEUTRAL
        EmotionalTone.HUMOROUS -> com.runiq.data.local.entities.EmotionalTone.HUMOROUS
        EmotionalTone.SERIOUS -> com.runiq.data.local.entities.EmotionalTone.SERIOUS
    }
}

// === GpsTrackPoint Mapping Functions ===

// Entity to Domain mapping
fun GpsTrackPointEntity.toDomain(): GpsTrackPoint {
    return GpsTrackPoint(
        id = this.id.toString(),
        sessionId = this.sessionId,
        timestamp = this.timestamp,
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = this.altitude ?: 0.0,
        accuracy = this.accuracy ?: 0f,
        speed = this.speed ?: 0f,
        bearing = this.bearing ?: 0f,
        distanceFromStart = this.distance,
        elevationGain = 0f // Not stored in entity, would need calculation
    )
}

// List extension
fun List<GpsTrackPointEntity>.toDomain(): List<GpsTrackPoint> = map { it.toDomain() }

// Domain to Entity mapping
fun GpsTrackPoint.toEntity(sequenceNumber: Int = 0): GpsTrackPointEntity {
    return GpsTrackPointEntity(
        id = this.id.toLongOrNull() ?: 0L,
        sessionId = this.sessionId,
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = this.altitude,
        timestamp = this.timestamp,
        accuracy = this.accuracy,
        speed = this.speed,
        bearing = this.bearing,
        distance = this.distanceFromStart,
        sequenceNumber = sequenceNumber,
        isPausePoint = false,
        pace = null, // Would need calculation
        heartRate = null
    )
}

// List extension with sequence numbering
fun List<GpsTrackPoint>.toEntity(): List<GpsTrackPointEntity> = 
    mapIndexed { index, gpsPoint -> gpsPoint.toEntity(index) }

