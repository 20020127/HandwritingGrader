package com.handwritinggrader.data.models

import com.google.gson.annotations.SerializedName

data class OcrResult(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("texts")
    val texts: List<TextItem> = emptyList(),
    @SerializedName("full_text")
    val fullText: String = "",
    @SerializedName("text_count")
    val textCount: Int = 0,
    @SerializedName("error")
    val error: String? = null
)

data class TextItem(
    @SerializedName("text")
    val text: String = "",
    @SerializedName("confidence")
    val confidence: Double = 0.0,
    @SerializedName("box")
    val box: List<List<Double>> = emptyList()
)

data class GradingResult(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("is_correct")
    val isCorrect: Boolean = false,
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("max_score")
    val maxScore: Int = 100,
    @SerializedName("feedback")
    val feedback: String = "",
    @SerializedName("error_type")
    val errorType: String? = null,
    @SerializedName("key_points")
    val keyPoints: List<String> = emptyList(),
    @SerializedName("error")
    val error: String? = null
)

data class CheckResponse(
    @SerializedName("ocr_result")
    val ocrResult: OcrResult? = null,
    @SerializedName("grading_result")
    val gradingResult: GradingResult = GradingResult(),
    @SerializedName("student_answer")
    val studentAnswer: String = ""
)

data class WrongQuestion(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("question_content")
    val questionContent: String = "",
    @SerializedName("correct_answer")
    val correctAnswer: String = "",
    @SerializedName("student_answer")
    val studentAnswer: String = "",
    @SerializedName("question_type")
    val questionType: String = "",
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("error_reason")
    val errorReason: String? = null,
    @SerializedName("question_number")
    val questionNumber: Int? = null,
    @SerializedName("homework_title")
    val homeworkTitle: String? = null,
    @SerializedName("create_time")
    val createTime: String = "",
    @SerializedName("review_count")
    val reviewCount: Int = 0,
    @SerializedName("is_mastered")
    val isMastered: Boolean = false,
    @SerializedName("last_review_time")
    val lastReviewTime: String? = null
)

data class StatisticsOverview(
    @SerializedName("period_days")
    val periodDays: Int = 30,
    @SerializedName("total_submissions")
    val totalSubmissions: Int = 0,
    @SerializedName("correct_count")
    val correctCount: Int = 0,
    @SerializedName("wrong_count")
    val wrongCount: Int = 0,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double = 0.0,
    @SerializedName("average_score")
    val averageScore: Double = 0.0,
    @SerializedName("unmastered_wrong_questions")
    val unmasteredWrongQuestions: Int = 0
)

data class SubjectStatistics(
    @SerializedName("subject")
    val subject: String = "",
    @SerializedName("total_questions")
    val totalQuestions: Int = 0,
    @SerializedName("correct_count")
    val correctCount: Int = 0,
    @SerializedName("wrong_count")
    val wrongCount: Int = 0,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double = 0.0
)

data class DailyStatistics(
    @SerializedName("date")
    val date: String = "",
    @SerializedName("total_questions")
    val totalQuestions: Int = 0,
    @SerializedName("correct_count")
    val correctCount: Int = 0,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double = 0.0
)

data class QuestionTypeStatistics(
    @SerializedName("question_type")
    val questionType: String = "",
    @SerializedName("total_questions")
    val totalQuestions: Int = 0,
    @SerializedName("correct_count")
    val correctCount: Int = 0,
    @SerializedName("wrong_count")
    val wrongCount: Int = 0,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double = 0.0
)

data class ModelInfo(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("is_vision")
    val isVision: Boolean = false
)

data class ModelListResponse(
    @SerializedName("models")
    val models: List<ModelInfo> = emptyList()
)
