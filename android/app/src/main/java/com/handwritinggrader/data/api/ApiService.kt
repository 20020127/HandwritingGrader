package com.handwritinggrader.data.api

import com.handwritinggrader.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("api/ocr/recognize")
    suspend fun recognizeText(
        @Part file: MultipartBody.Part
    ): OcrResult

    @Multipart
    @POST("api/grading/check")
    suspend fun checkAnswer(
        @Part file: MultipartBody.Part,
        @Part("question") question: RequestBody,
        @Part("question_type") questionType: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("correct_answer") correctAnswer: RequestBody? = null
    ): CheckResponse

    @Multipart
    @POST("api/grading/check-text")
    suspend fun checkTextAnswer(
        @Part("question") question: RequestBody,
        @Part("student_answer") studentAnswer: RequestBody,
        @Part("question_type") questionType: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("correct_answer") correctAnswer: RequestBody? = null
    ): CheckResponse

    @GET("api/models/models")
    suspend fun getModels(): ModelListResponse

    @GET("api/wrong-questions/")
    suspend fun getWrongQuestions(
        @Query("subject") subject: String? = null,
        @Query("question_type") questionType: String? = null,
        @Query("is_mastered") isMastered: Boolean? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): List<WrongQuestion>

    @POST("api/wrong-questions/")
    suspend fun createWrongQuestion(
        @Body wrongQuestion: WrongQuestionCreate
    ): WrongQuestion

    @PUT("api/wrong-questions/{id}")
    suspend fun updateWrongQuestion(
        @Path("id") id: Int,
        @Body update: WrongQuestionUpdate
    ): WrongQuestion

    @DELETE("api/wrong-questions/{id}")
    suspend fun deleteWrongQuestion(
        @Path("id") id: Int
    ): Map<String, String>

    @POST("api/wrong-questions/{id}/review")
    suspend fun reviewWrongQuestion(
        @Path("id") id: Int
    ): Map<String, Any>

    @GET("api/statistics/overview")
    suspend fun getStatisticsOverview(
        @Query("days") days: Int = 30
    ): StatisticsOverview

    @GET("api/statistics/by-subject")
    suspend fun getSubjectStatistics(
        @Query("days") days: Int = 30
    ): List<SubjectStatistics>

    @GET("api/statistics/daily")
    suspend fun getDailyStatistics(
        @Query("days") days: Int = 30,
        @Query("subject") subject: String? = null
    ): List<DailyStatistics>

    @GET("api/statistics/by-question-type")
    suspend fun getQuestionTypeStatistics(
        @Query("days") days: Int = 30,
        @Query("subject") subject: String? = null
    ): List<QuestionTypeStatistics>
}

data class WrongQuestionCreate(
    val question_content: String,
    val correct_answer: String,
    val student_answer: String,
    val question_type: String,
    val subject: String,
    val error_reason: String? = null,
    val question_number: Int? = null,
    val homework_title: String? = null
)

data class WrongQuestionUpdate(
    val is_mastered: Boolean? = null,
    val review_count: Int? = null
)
