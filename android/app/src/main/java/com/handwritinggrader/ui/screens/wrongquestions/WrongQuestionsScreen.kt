package com.handwritinggrader.ui.screens.wrongquestions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.handwritinggrader.data.models.WrongQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrongQuestionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: WrongQuestionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    val subjects = listOf("数学", "语文", "英语", "物理", "化学", "生物", "历史", "地理", "政治")
    val questionTypes = listOf("选择题", "填空题", "计算题", "问答题", "判断题", "应用题", "几何题")

    LaunchedEffect(selectedSubject, selectedType) {
        viewModel.loadWrongQuestions(selectedSubject, selectedType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("错题本") },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var subjectExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedSubject ?: "全部科目",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("科目") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("全部科目") },
                            onClick = {
                                selectedSubject = null
                                subjectExpanded = false
                            }
                        )
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject) },
                                onClick = {
                                    selectedSubject = subject
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedType ?: "全部题型",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("题型") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("全部题型") },
                            onClick = {
                                selectedType = null
                                typeExpanded = false
                            }
                        )
                        questionTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadWrongQuestions(selectedSubject, selectedType) }) {
                                Text("重试")
                            }
                        }
                    }
                }

                uiState.wrongQuestions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "暂无错题",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "继续保持，没有错题说明掌握得很好",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.wrongQuestions) { wrongQuestion ->
                            WrongQuestionCard(
                                wrongQuestion = wrongQuestion,
                                onMarkAsMastered = { viewModel.markAsMastered(wrongQuestion.id) },
                                onDelete = { viewModel.deleteWrongQuestion(wrongQuestion.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WrongQuestionCard(
    wrongQuestion: WrongQuestion,
    onMarkAsMastered: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(wrongQuestion.subject, style = MaterialTheme.typography.labelSmall) }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(wrongQuestion.questionType, style = MaterialTheme.typography.labelSmall) }
                    )
                }

                Row {
                    IconButton(
                        onClick = onMarkAsMastered,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "标记已掌握",
                            modifier = Modifier.size(20.dp),
                            tint = if (wrongQuestion.isMastered) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = wrongQuestion.questionContent,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "正确答案: ${wrongQuestion.correctAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "学生答案: ${wrongQuestion.studentAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )

            if (wrongQuestion.errorReason != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "错误原因: ${wrongQuestion.errorReason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "复习 ${wrongQuestion.reviewCount} 次",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (wrongQuestion.isMastered) "已掌握" else "未掌握",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    icon = {
                        Icon(
                            if (wrongQuestion.isMastered) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除", fontWeight = FontWeight.Bold) },
            text = { Text("确定要删除这道错题吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
