package com.handwritinggrader.ui.screens.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handwritinggrader.data.api.ApiService
import com.handwritinggrader.data.models.CheckResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

data class ResultUiState(
    val isLoading: Boolean = false,
    val result: CheckResponse? = null,
    val error: String? = null,
    val question: String = "",
    val questionType: String = "",
    val subject: String = ""
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    fun setQuestionInfo(question: String, questionType: String, subject: String) {
        _uiState.value = _uiState.value.copy(
            question = question,
            questionType = questionType,
            subject = subject
        )
    }
    
    fun checkAnswer(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                if (!imageFile.exists() || imageFile.length() == 0L) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "图片文件不存在或为空")
                    return@launch
                }
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                
                val questionBody = _uiState.value.question.toRequestBody("text/plain".toMediaTypeOrNull())
                val questionTypeBody = _uiState.value.questionType.toRequestBody("text/plain".toMediaTypeOrNull())
                val subjectBody = _uiState.value.subject.toRequestBody("text/plain".toMediaTypeOrNull())
                
                val response = apiService.checkAnswer(
                    file = filePart,
                    question = questionBody,
                    questionType = questionTypeBody,
                    subject = subjectBody
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    result = response
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "批改失败"
                )
            }
        }
    }
    
    fun retry() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
