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
    val imageUri: Uri? = null,
    val imageFile: File? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun setImageUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(imageUri = uri, ocrText = "", ocrError = null)
    }

    fun startOcr() {
        val uri = _uiState.value.imageUri ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isOcrLoading = true, ocrError = null)
            try {
                val file = copyUriToFile(uri)
                _uiState.value = _uiState.value.copy(imageFile = file)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val result = apiService.recognizeText(filePart)
                _uiState.value = _uiState.value.copy(
                    isOcrLoading = false,
                    ocrText = result.fullText
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isOcrLoading = false,
                    ocrError = e.message ?: "OCR识别失败"
                )
            }
        }
    }

    fun clearOcr() {
        _uiState.value = CameraUiState()
    }

    private fun copyUriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("无法读取图片")
        val file = File(context.cacheDir, "ocr_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()
        return file
    }
}
