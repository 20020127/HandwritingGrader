package com.handwritinggrader.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    question: String,
    questionType: String,
    subject: String,
    filePath: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(question, questionType, subject, filePath) {
        viewModel.setQuestionInfo(question, questionType, subject)
        if (filePath.isNotBlank()) {
            val file = File(filePath)
            if (file.exists()) {
                viewModel.checkAnswer(file)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("批改结果") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Outlined.Home, contentDescription = "首页")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                        Text("正在批改中...", style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.errorContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null,
                                modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.error)
                        }
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = { viewModel.retry() }, shape = RoundedCornerShape(12.dp)) {
                            Text("重试")
                        }
                    }
                }
            }

            uiState.result != null -> {
                val result = uiState.result!!
                val isCorrect = result.gradingResult.isCorrect

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(72.dp).clip(CircleShape)
                                    .background(
                                        if (isCorrect) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null, modifier = Modifier.size(40.dp),
                                    tint = if (isCorrect) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isCorrect) "回答正确" else "回答错误",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${result.gradingResult.score}",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "/ ${result.gradingResult.maxScore} 分",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isCorrect) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Info, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("题目信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoRow("科目", subject)
                            InfoRow("题型", questionType)
                            InfoRow("题目", question)
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Edit, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("学生答案", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(result.studentAnswer, style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Comment, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("批改意见", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(result.gradingResult.feedback, style = MaterialTheme.typography.bodyLarge)

                            if (result.gradingResult.errorType != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("错误类型: ${result.gradingResult.errorType}") },
                                    icon = { Icon(Icons.Outlined.Warning, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                )
                            }

                            if (result.gradingResult.keyPoints.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("知识点", style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                result.gradingResult.keyPoints.forEach { point ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Text("  ", color = MaterialTheme.colorScheme.primary)
                                        Text(point, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)) { Text("继续批改") }
                        Button(onClick = onNavigateHome, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)) { Text("返回首页") }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
