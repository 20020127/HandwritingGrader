package com.handwritinggrader.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(7 to "近7天", 30 to "近30天", 90 to "近90天").forEach { (days, label) ->
                    FilterChip(
                        selected = selectedDays == days,
                        onClick = { selectedDays = days },
                        label = { Text(label) },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Button(
                                onClick = { viewModel.loadStatistics(selectedDays) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("重试")
                            }
                        }
                    }
                }

                uiState.overview != null -> {
                    val overview = uiState.overview!!

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "总览",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    title = "总批改",
                                    value = "${overview.totalSubmissions}",
                                    icon = Icons.Outlined.Assignment,
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    iconColor = MaterialTheme.colorScheme.primary
                                )
                                StatItem(
                                    title = "正确",
                                    value = "${overview.correctCount}",
                                    icon = Icons.Filled.CheckCircle,
                                    containerColor = CorrectGreenLight,
                                    iconColor = CorrectGreen
                                )
                                StatItem(
                                    title = "错误",
                                    value = "${overview.wrongCount}",
                                    icon = Icons.Filled.Cancel,
                                    containerColor = WrongRedLight,
                                    iconColor = WrongRed
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
                                    icon = Icons.Outlined.TrendingUp,
                                    containerColor = InfoBlueLight,
                                    iconColor = InfoBlue
                                )
                                StatItem(
                                    title = "平均分",
                                    value = "${overview.averageScore}",
                                    icon = Icons.Outlined.Star,
                                    containerColor = WarningOrangeLight,
                                    iconColor = WarningOrange
                                )
                                StatItem(
                                    title = "待掌握",
                                    value = "${overview.unmasteredWrongQuestions}",
                                    icon = Icons.Outlined.Warning,
                                    containerColor = WrongRedLight,
                                    iconColor = WrongRed
                                )
                            }
                        }
                    }

                    if (uiState.subjectStats.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "按科目统计",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                uiState.subjectStats.forEach { stat ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stat.subject,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${stat.accuracyRate}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { (stat.accuracyRate / 100).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    if (uiState.questionTypeStats.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Quiz,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "按题型统计",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                uiState.questionTypeStats.forEach { stat ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stat.questionType,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${stat.accuracyRate}%",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.tertiary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { (stat.accuracyRate / 100).toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
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
private fun StatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = iconColor
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val CorrectGreenLight = androidx.compose.ui.graphics.Color(0xFFE8F5E9)
private val WrongRedLight = androidx.compose.ui.graphics.Color(0xFFFFEBEE)
private val WarningOrangeLight = androidx.compose.ui.graphics.Color(0xFFFFF3E0)
private val InfoBlueLight = androidx.compose.ui.graphics.Color(0xFFE3F2FD)
private val CorrectGreen = androidx.compose.ui.graphics.Color(0xFF4CAF50)
private val WrongRed = androidx.compose.ui.graphics.Color(0xFFF44336)
private val WarningOrange = androidx.compose.ui.graphics.Color(0xFFFF9800)
private val InfoBlue = androidx.compose.ui.graphics.Color(0xFF2196F3)
