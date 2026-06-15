package com.handwritinggrader.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDays by remember { mutableIntStateOf(30) }
    
    LaunchedEffect(selectedDays) {
        viewModel.loadStatistics(selectedDays)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("成绩统计") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedDays == 7,
                    onClick = { selectedDays = 7 },
                    label = { Text("近7天") }
                )
                FilterChip(
                    selected = selectedDays == 30,
                    onClick = { selectedDays = 30 },
                    label = { Text("近30天") }
                )
                FilterChip(
                    selected = selectedDays == 90,
                    onClick = { selectedDays = 90 },
                    label = { Text("近90天") }
                )
            }
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadStatistics(selectedDays) }) {
                                Text("重试")
                            }
                        }
                    }
                }
                
                uiState.overview != null -> {
                    val overview = uiState.overview!!
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "总览",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    title = "总批改",
                                    value = "${overview.totalSubmissions}",
                                    icon = Icons.Default.Assignment
                                )
                                StatItem(
                                    title = "正确",
                                    value = "${overview.correctCount}",
                                    icon = Icons.Default.CheckCircle
                                )
                                StatItem(
                                    title = "错误",
                                    value = "${overview.wrongCount}",
                                    icon = Icons.Default.Cancel
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    title = "正确率",
                                    value = "${overview.accuracyRate}%",
                                    icon = Icons.Default.TrendingUp
                                )
                                StatItem(
                                    title = "平均分",
                                    value = "${overview.averageScore}",
                                    icon = Icons.Default.Star
                                )
                                StatItem(
                                    title = "待掌握",
                                    value = "${overview.unmasteredWrongQuestions}",
                                    icon = Icons.Default.Warning
                                )
                            }
                        }
                    }
                    
                    if (uiState.subjectStats.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "按科目统计",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                uiState.subjectStats.forEach { stat ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(stat.subject)
                                        Text("${stat.accuracyRate}%")
                                    }
                                    LinearProgressIndicator(
                                        progress = (stat.accuracyRate / 100).toFloat(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    
                    if (uiState.questionTypeStats.isNotEmpty()) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "按题型统计",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                uiState.questionTypeStats.forEach { stat ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(stat.questionType)
                                        Text("${stat.accuracyRate}%")
                                    }
                                    LinearProgressIndicator(
                                        progress = (stat.accuracyRate / 100).toFloat(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
