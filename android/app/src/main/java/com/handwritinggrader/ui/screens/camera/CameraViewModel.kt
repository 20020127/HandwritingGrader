package com.handwritinggrader.ui.screens.camera

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.handwritinggrader.data.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

data class CameraUiState(
    val isOcrLoading: Boolean = false,
    val ocrText: String = "",
    val ocrError: String? = null,
    val hasImage: Boolean = false,
    val imageFile: File? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun processImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = CameraUiState(isOcrLoading = true, hasImage = true)
            try {
                val file = uriToFile(uri)
                _uiState.value = _uiState.value.copy(imageFile = file)
                doOcr(file)
            } catch (e: Throwable) {
                _uiState.value = _uiState.value.copy(
                    isOcrLoading = false,
                    ocrError = e.message ?: "处理图片失败"
                )
            }
        }
    }

    private suspend fun doOcr(file: File) {
        try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val result = apiService.recognizeText(filePart)
            _uiState.value = _uiState.value.copy(
                isOcrLoading = false,
                ocrText = result.fullText
            )
        } catch (e: Throwable) {
            _uiState.value = _uiState.value.copy(
                isOcrLoading = false,
                ocrError = e.message ?: "OCR识别失败"
            )
        }
    }

    fun reset() {
        _uiState.value = CameraUiState()
    }

    private fun uriToFile(uri: Uri): File {
        val input = context.contentResolver.openInputStream(uri)
            ?: throw Exception("无法读取图片，请检查权限")
        val file = File(context.cacheDir, "ocr_${System.currentTimeMillis()}.jpg")
        try {
            file.outputStream().use { out -> input.copyTo(out) }
        } finally {
            input.close()
        }
        if (!file.exists() || file.length() == 0L) {
            throw Exception("图片文件为空或写入失败")
        }
        return file
    }
}
