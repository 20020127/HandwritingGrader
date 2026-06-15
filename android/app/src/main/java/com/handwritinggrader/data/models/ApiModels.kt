package com.handwritinggrader.data.models

import com.google.gson.annotations.SerializedName

data class OcrResult(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("texts")
    val texts: List<TextItem>,
    @SerializedName("full_text")
    val fullText: String,
    @SerializedName("text_count")
    val textCount: Int,
    @SerializedName("error")
    val error: String? = null
)

data class TextItem(
    @SerializedName("text")
    val text: String,
    @SerializedName("confidence")
    val confidence: Double,
    @SerializedName("box")
    val box: List<List<Double>>
)

data class GradingResult(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    @SerializedName("score")
    val score: Int,
    @SerializedName("max_score")
    val maxScore: Int,
    @SerializedName("feedback")
    val feedback: String,
    @SerializedName("error_type")
    val errorType: String? = null,
    @SerializedName("key_points")
    val keyPoints: List<String> = emptyList(),
    @SerializedName("error")
    val error: String? = null
)

data class CheckResponse(
    @SerializedName("ocr_result")
    val ocrResult: OcrResult,
    @SerializedName("grading_result")
    val gradingResult: GradingResult,
    @SerializedName("student_answer")
    val studentAnswer: String
)

data class WrongQuestion(
    @SerializedName("id")
    val id: Int,
    @SerializedName("question_content")
    val questionContent: String,
    @SerializedName("correct_answer")
    val correctAnswer: String,
    @SerializedName("student_answer")
    val studentAnswer: String,
    @SerializedName("question_type")
    val questionType: String,
    @SerializedName("subject")
    val subject: String,
    @SerializedName("error_reason")
    val errorReason: String? = null,
    @SerializedName("question_number")
    val questionNumber: Int? = null,
    @SerializedName("homework_title")
    val homeworkTitle: String? = null,
    @SerializedName("create_time")
    val createTime: String,
    @SerializedName("review_count")
    val reviewCount: Int = 0,
    @SerializedName("is_mastered")
    val isMastered: Boolean = false,
    @SerializedName("last_review_time")
    val lastReviewTime: String? = null
)

data class StatisticsOverview(
    @SerializedName("period_days")
    val periodDays: Int,
    @SerializedName("total_submissions")
    val totalSubmissions: Int,
    @SerializedName("correct_count")
    val correctCount: Int,
    @SerializedName("wrong_count")
    val wrongCount: Int,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double,
    @SerializedName("average_score")
    val averageScore: Double,
    @SerializedName("unmastered_wrong_questions")
    val unmasteredWrongQuestions: Int
)

data class SubjectStatistics(
    @SerializedName("subject")
    val subject: String,
    @SerializedName("total_questions")
    val totalQuestions: Int,
    @SerializedName("correct_count")
    val correctCount: Int,
    @SerializedName("wrong_count")
    val wrongCount: Int,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double
)

data class DailyStatistics(
    @SerializedName("date")
    val date: String,
    @SerializedName("total_questions")
    val totalQuestions: Int,
    @SerializedName("correct_count")
    val correctCount: Int,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double
)

data class QuestionTypeStatistics(
    @SerializedName("question_type")
    val questionType: String,
    @SerializedName("total_questions")
    val totalQuestions: Int,
    @SerializedName("correct_count")
    val correctCount: Int,
    @SerializedName("wrong_count")
    val wrongCount: Int,
    @SerializedName("accuracy_rate")
    val accuracyRate: Double
)
