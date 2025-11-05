package com.example.ezwordmaster.data.local.mapper

import com.example.ezwordmaster.data.local.entity.StudyResultEntity
import com.example.ezwordmaster.model.StudyResult

object StudyResultMapper {
    
    fun toDomain(entity: StudyResultEntity): StudyResult {
        return StudyResult(
            id = entity.id,
            topicId = entity.topicId,
            topicName = entity.topicName,
            studyMode = entity.studyMode,
            day = entity.day,
            duration = entity.duration,
            totalWords = entity.totalWords,
            knownWords = entity.knownWords,
            learningWords = entity.learningWords,
            accuracy = entity.accuracy,
            totalPairs = entity.totalPairs,
            matchedPairs = entity.matchedPairs,
            completionRate = entity.completionRate,
            playTime = entity.playTime
        )
    }
    
    fun toEntity(result: StudyResult): StudyResultEntity {
        return StudyResultEntity(
            id = result.id,
            topicId = result.topicId,
            topicName = result.topicName,
            studyMode = result.studyMode,
            day = result.day,
            duration = result.duration,
            totalWords = result.totalWords,
            knownWords = result.knownWords,
            learningWords = result.learningWords,
            accuracy = result.accuracy,
            totalPairs = result.totalPairs,
            matchedPairs = result.matchedPairs,
            completionRate = result.completionRate,
            playTime = result.playTime
        )
    }
}


