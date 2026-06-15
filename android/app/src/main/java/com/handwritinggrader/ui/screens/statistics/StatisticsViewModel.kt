package com.handwritinggrader.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handwritinggrader.data.api.ApiService
import com.handwritinggrader.data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val overview: StatisticsOverview? = null,
    val subjectStats: List<SubjectStatistics> = emptyList(),
    val dailyStats: List<DailyStatistics> = emptyList(),
    val questionTypeStats: List<QuestionTypeStatistics> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    fun loadStatistics(days: Int = 30) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val overview = apiService.getStatisticsOverview(days)
                val subjectStats = apiService.getSubjectStatistics(days)
                val questionTypeStats = apiService.getQuestionTypeStatistics(days)
                val dailyStats = apiService.getDailyStatistics(days)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    overview = overview,
                    subjectStats = subjectStats,
                    questionTypeStats = questionTypeStats,
                    dailyStats = dailyStats
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载统计数据失败"
                )
            }
        }
    }
}
