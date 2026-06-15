package com.handwritinggrader.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.handwritinggrader.data.models.ModelInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("大模型配置") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "返回") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Info, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("配置大模型API，支持图片识别的模型可直接批改手写作业", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("API 设置", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = uiState.provider, onValueChange = viewModel::updateProvider, label = { Text("服务商") }, placeholder = { Text("zhipu / qwen / openai") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Outlined.Business, null) })
                    OutlinedTextField(value = uiState.apiKey, onValueChange = viewModel::updateApiKey, label = { Text("API Key") }, placeholder = { Text("输入API密钥") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Outlined.Key, null) })
                    OutlinedTextField(value = uiState.baseUrl, onValueChange = viewModel::updateBaseUrl, label = { Text("Base URL") }, placeholder = { Text("https://open.bigmodel.cn/api/paas/v4") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Outlined.Link, null) })

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = uiState.model, onValueChange = viewModel::updateModel, label = { Text("模型名称") }, placeholder = { Text("手动输入或从下方选择") }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Outlined.SmartToy, null) })
                        FilledTonalButton(onClick = { viewModel.fetchModels() }, enabled = uiState.apiKey.isNotBlank() && !uiState.isLoadingModels, shape = RoundedCornerShape(12.dp), modifier = Modifier.height(56.dp)) {
                            if (uiState.isLoadingModels) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Icon(Icons.Default.Refresh, "获取模型")
                        }
                    }
                }
            }

            if (uiState.modelError != null) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(uiState.modelError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            if (uiState.models.isNotEmpty()) {
                Text("可用模型（点击选择）", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp))

                val visionModels = uiState.models.filter { it.isVision }
                val textModels = uiState.models.filter { !it.isVision }

                if (visionModels.isNotEmpty()) {
                    Text("支持图片识别", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, top = 4.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(4.dp)) {
                            visionModels.forEach { model ->
                                ModelItem(model = model, isSelected = model.id == uiState.model, onClick = { viewModel.selectModel(model.id) })
                            }
                        }
                    }
                }

                if (textModels.isNotEmpty()) {
                    Text("仅文本模型", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, top = 8.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(4.dp)) {
                            textModels.forEach { model ->
                                ModelItem(model = model, isSelected = model.id == uiState.model, onClick = { viewModel.selectModel(model.id) })
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("保存配置", style = MaterialTheme.typography.titleSmall)
            }

            if (uiState.saved) {
                Snackbar(shape = RoundedCornerShape(12.dp), containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("配置已保存")
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelItem(model: ModelInfo, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(model.id, style = MaterialTheme.typography.bodyMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
        if (model.isVision) {
            SuggestionChip(onClick = {}, label = { Text("支持图片", style = MaterialTheme.typography.labelSmall) }, icon = { Icon(Icons.Outlined.Visibility, null, modifier = Modifier.size(14.dp)) }, modifier = Modifier.height(28.dp))
        }
        if (isSelected) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.CheckCircle, "已选", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
    }
}
