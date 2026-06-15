package com.handwritinggrader.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handwritinggrader.data.api.ApiService
import com.handwritinggrader.data.local.LlmConfig
import com.handwritinggrader.data.local.SettingsDataStore
import com.handwritinggrader.data.models.ModelInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val provider: String = "",
    val apiKey: String = "",
    val model: String = "",
    val baseUrl: String = "",
    val saved: Boolean = false,
    val models: List<ModelInfo> = emptyList(),
    val isLoadingModels: Boolean = false,
    val modelError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataStore.llmConfig.collect { config ->
                _uiState.value = _uiState.value.copy(
                    provider = config.provider,
                    apiKey = config.apiKey,
                    model = config.model,
                    baseUrl = config.baseUrl
                )
            }
        }
    }

    fun updateProvider(value: String) { _uiState.value = _uiState.value.copy(provider = value, saved = false) }
    fun updateApiKey(value: String) { _uiState.value = _uiState.value.copy(apiKey = value, saved = false) }
    fun updateModel(value: String) { _uiState.value = _uiState.value.copy(model = value, saved = false) }
    fun updateBaseUrl(value: String) { _uiState.value = _uiState.value.copy(baseUrl = value, saved = false) }

    fun selectModel(modelId: String) {
        _uiState.value = _uiState.value.copy(model = modelId, saved = false)
    }

    fun save() {
        viewModelScope.launch {
            val state = _uiState.value
            settingsDataStore.saveLlmConfig(LlmConfig(provider = state.provider, apiKey = state.apiKey, model = state.model, baseUrl = state.baseUrl))
            _uiState.value = state.copy(saved = true)
        }
    }

    fun fetchModels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingModels = true, modelError = null)
            try {
                val response = apiService.getModels()
                _uiState.value = _uiState.value.copy(isLoadingModels = false, models = response.models)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingModels = false, modelError = e.message ?: "获取模型列表失败")
            }
        }
    }
}
