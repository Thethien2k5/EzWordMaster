package com.example.ezwordmaster.domain.repository

import com.example.ezwordmaster.model.StudyResult

/**
 * Hợp đồng (Interface) cho việc thao tác với StudyResult trên Cloud (Firestore)
 */
interface ICloudStudyResultRepository {
    suspend fun saveStudyResults(results: List<StudyResult>): Result<Unit>
    suspend fun loadStudyResults(): Result<List<StudyResult>>
    suspend fun hasData(): Boolean
}