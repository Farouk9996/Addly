package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.DownloadedFileEntity
import com.example.ui.PortalViewModel
import com.example.ui.components.DocumentViewerDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DownloadsScreen(
    downloads: List<DownloadedFileEntity>,
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    val importFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            var fileName = "مستند_مستورد_${System.currentTimeMillis()}.pdf"
            var fileSize = 1024L * 1024L
            try {
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                        if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                    }
                }
            } catch (_: Exception) {}

            val mime = context.contentResolver.getType(it) ?: "application/pdf"
            viewModel.registerDownload(
                fileName = fileName,
                fileUri = it.toString(),
                fileSizeBytes = fileSize,
                mimeType = mime
            )
            Toast.makeText(context, "تم استيراد المستند للمدير: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("الكل") }
    var viewingDocument by remember { mutableStateOf<DownloadedFileEntity?>(null) }

    // Total Storage calculation
    val totalSizeBytes = remember(downloads) { downloads.sumOf { it.fileSizeBytes } }
    val totalStorageFormatted = remember(totalSizeBytes) {
        val mb = totalSizeBytes / (1024.0 * 1024.0)
        String.format(Locale.US, "%.1f ميغابايت", mb)
    }

    val filteredDownloads = remember(downloads, searchQuery, selectedFilter) {
        downloads.filter { file ->
            val matchesSearch = searchQuery.isBlank() ||
                file.fileName.contains(searchQuery, ignoreCase = true) ||
                file.portalTitle.contains(searchQuery, ignoreCase = true) ||
                file.courseCode.contains(searchQuery, ignoreCase = true) ||
                file.description.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "ملفات PDF" -> file.fileName.endsWith(".pdf", ignoreCase = true) || file.mimeType.contains("pdf")
                "مستندات Word" -> file.fileName.endsWith(".doc", ignoreCase = true) || file.fileName.endsWith(".docx", ignoreCase = true) || file.mimeType.contains("word")
                "حقوق وسياسة" -> file.portalTitle.contains("الحقوق") || file.courseCode.contains("الحقوق") || file.courseCode.contains("البحث")
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF161616))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Title and Storage Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF97316).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "مدير الملفات والمستندات الجامعية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "تتبع وقراءة ملفات ومقررات مودل في أي وقت",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }

            // Storage Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF333333)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "$totalStorageFormatted مستخدمة",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${downloads.size} مستندات مخزنة ومفهرسة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF15803D).copy(alpha = 0.25f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "جاهزة دون إنترنت",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF86EFAC),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("بحث في المستندات أو المقررات...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9E9E9E)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_downloads_input")
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("الكل", "ملفات PDF", "مستندات Word", "حقوق وسياسة")
                    items(filters) { filterLabel ->
                        val isSelected = selectedFilter == filterLabel
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filterLabel },
                            label = { Text(filterLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF222222),
                                selectedContainerColor = Color(0xFFF97316),
                                labelColor = Color(0xFFD1D5DB),
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color(0xFF333333),
                                selectedBorderColor = Color(0xFFF97316)
                            )
                        )
                    }
                }
            }

            // Downloads List
            if (filteredDownloads.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color(0xFF757575),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (searchQuery.isBlank()) "لا توجد مستندات في هذا التصنيف" else "لا توجد نتائج مطابقة لبحثك",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "عند تنزيل أي ملف PDF أو مستند من بوابة مودل ستتم فهرسته وتتبعه تلقائياً هنا",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9E9E9E),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredDownloads,
                    key = { it.id }
                ) { file ->
                    val isPdf = file.fileName.endsWith(".pdf", ignoreCase = true) || file.mimeType.contains("pdf")
                    val isDoc = file.fileName.endsWith(".doc", ignoreCase = true) || file.fileName.endsWith(".docx", ignoreCase = true)
                    val cardIcon = when {
                        isPdf -> Icons.Default.PictureAsPdf
                        isDoc -> Icons.Default.Description
                        else -> Icons.Default.FolderZip
                    }
                    val cardIconColor = when {
                        isPdf -> Color(0xFFEF4444)
                        isDoc -> Color(0xFF38BDF8)
                        else -> Color(0xFFF59E0B)
                    }
                    val formattedFileSize = remember(file.fileSizeBytes) {
                        val bytes = file.fileSizeBytes
                        when {
                            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
                            bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
                            bytes > 0 -> "$bytes B"
                            else -> "حجم غير محدد"
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("download_card_${file.id}")
                            .clickable { viewingDocument = file },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cardIconColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cardIcon,
                                            contentDescription = null,
                                            tint = cardIconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        if (file.portalTitle.isNotBlank()) {
                                            Text(
                                                text = file.portalTitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFF97316),
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = file.fileName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = formattedFileSize,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFFD1D5DB)
                                            )
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF757575)
                                            )
                                            Text(
                                                text = dateFormat.format(Date(file.downloadedAt)),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color(0xFF9E9E9E)
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewingDocument = file },
                                        modifier = Modifier.testTag("preview_doc_btn_${file.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "معاينة المستند",
                                            tint = Color(0xFF38BDF8)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteDownload(file) },
                                        modifier = Modifier.testTag("delete_doc_btn_${file.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "حذف",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            if (file.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = file.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF9E9E9E),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
        // Import Document FAB
        FloatingActionButton(
            onClick = {
                importFileLauncher.launch(
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "*/*"
                    )
                )
            },
            containerColor = Color(0xFFF97316),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("import_doc_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "استيراد مستند")
        }
    }

    // Modal Document Viewer
    viewingDocument?.let { doc ->
        DocumentViewerDialog(
            document = doc,
            onDismiss = { viewingDocument = null }
        )
    }
}
