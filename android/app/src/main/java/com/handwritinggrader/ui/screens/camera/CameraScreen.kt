package com.handwritinggrader.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    onImageCaptured: (String, String, String, String) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasCameraPermission = isGranted }

    var showQuestionDialog by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            showQuestionDialog = true
            viewModel.startOcr()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.setImageUri(it)
            showQuestionDialog = true
            viewModel.startOcr()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照批改") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "选择图片来源",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "拍摄或选择手写作业图片，AI将自动识别并批改",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                onClick = {
                    if (hasCameraPermission) {
                        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(
                            context, "${context.packageName}.fileprovider", file
                        )
                        viewModel.setImageUri(uri)
                        takePictureLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("拍照", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("使用相机拍摄作业", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Card(
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Image, contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("从相册选择", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("从手机相册选取图片", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showQuestionDialog) {
        QuestionInputDialog(
            uiState = uiState,
            onDismiss = {
                showQuestionDialog = false
                viewModel.clearOcr()
            },
            onConfirm = { question, questionType, subject, filePath ->
                showQuestionDialog = false
                onImageCaptured(question, questionType, subject, filePath)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionInputDialog(
    uiState: CameraUiState,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var question by remember { mutableStateOf("") }
    var questionType by remember { mutableStateOf("选择题") }
    var subject by remember { mutableStateOf("数学") }
    var studentAnswer by remember { mutableStateOf("") }

    val questionTypes = listOf("选择题", "填空题", "计算题", "问答题", "判断题", "应用题", "几何题")
    val subjects = listOf("数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治")

    LaunchedEffect(uiState.ocrText) {
        if (uiState.ocrText.isNotBlank()) {
            studentAnswer = uiState.ocrText
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("输入题目信息", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("题目内容") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                var questionTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = questionTypeExpanded,
                    onExpandedChange = { questionTypeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = questionType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("题目类型") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = questionTypeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = questionTypeExpanded,
                        onDismissRequest = { questionTypeExpanded = false }
                    ) {
                        questionTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { questionType = type; questionTypeExpanded = false }
                            )
                        }
                    }
                }

                var subjectExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("科目") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text(subj) },
                                onClick = { subject = subj; subjectExpanded = false }
                            )
                        }
                    }
                }

                Divider()

                Text(
                    text = "OCR识别结果",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                when {
                    uiState.isOcrLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("正在识别...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    uiState.ocrError != null -> {
                        Text(
                            text = "识别失败: ${uiState.ocrError}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    uiState.ocrText.isNotBlank() -> {
                        Text(
                            text = "已识别到文字，可编辑下方内容：",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = studentAnswer,
                    onValueChange = { studentAnswer = it },
                    label = { Text("学生答案（可编辑）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("OCR识别结果将自动填入，也可手动输入") }
                )
            }
        },
        confirmButton = {
            val filePath = uiState.imageFile?.absolutePath ?: ""
            Button(
                onClick = { onConfirm(question, questionType, subject, filePath) },
                enabled = question.isNotBlank() && studentAnswer.isNotBlank() && !uiState.isOcrLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("开始批改")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
