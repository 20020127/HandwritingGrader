package com.handwritinggrader.ui.screens.wrongquestions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handwritinggrader.data.api.ApiService
import com.handwritinggrader.data.models.WrongQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WrongQuestionsUiState(
    val isLoading: Boolean = false,
    val wrongQuestions: List<WrongQuestion> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WrongQuestionsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WrongQuestionsUiState())
    val uiState: StateFlow<WrongQuestionsUiState> = _uiState.asStateFlow()
    
    fun loadWrongQuestions(subject: String? = null, questionType: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val response = apiService.getWrongQuestions(
                    subject = subject,
                    questionType = questionType
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wrongQuestions = response
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载失败"
                )
            }
        }
    }
    
    fun markAsMastered(id: Int) {
        viewModelScope.launch {
            try {
                apiService.updateWrongQuestion(
                    id = id,
                    update = com.handwritinggrader.data.api.WrongQuestionUpdate(is_mastered = true)
                )
                loadWrongQuestions()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "操作失败")
            }
        }
    }
    
    fun deleteWrongQuestion(id: Int) {
        viewModelScope.launch {
            try {
                apiService.deleteWrongQuestion(id)
                loadWrongQuestions()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "删除失败")
            }
        }
    }
}
