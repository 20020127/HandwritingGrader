package com.handwritinggrader.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class LlmConfig(
    val provider: String = "",
    val apiKey: String = "",
    val model: String = "",
    val baseUrl: String = ""
)

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val LLM_PROVIDER = stringPreferencesKey("llm_provider")
        private val LLM_API_KEY = stringPreferencesKey("llm_api_key")
        private val LLM_MODEL = stringPreferencesKey("llm_model")
        private val LLM_BASE_URL = stringPreferencesKey("llm_base_url")
    }

    val llmConfig: Flow<LlmConfig> = context.dataStore.data.map { prefs ->
        LlmConfig(
            provider = prefs[LLM_PROVIDER] ?: "",
            apiKey = prefs[LLM_API_KEY] ?: "",
            model = prefs[LLM_MODEL] ?: "",
            baseUrl = prefs[LLM_BASE_URL] ?: ""
        )
    }

    suspend fun saveLlmConfig(config: LlmConfig) {
        context.dataStore.edit { prefs ->
            prefs[LLM_PROVIDER] = config.provider
            prefs[LLM_API_KEY] = config.apiKey
            prefs[LLM_MODEL] = config.model
            prefs[LLM_BASE_URL] = config.baseUrl
        }
    }
}
