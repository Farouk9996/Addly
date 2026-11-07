package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun AddBookmarkDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, url: String, portalCategory: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(false) }
    var isUrlError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(text = "إضافة موقع أو صفحة للمفضلة", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = it.isBlank()
                    },
                    label = { Text("اسم الموقع أو المقرر (مثال: مقرر الذكاء الاصطناعي)") },
                    isError = isTitleError,
                    supportingText = if (isTitleError) { { Text("يرجى إدخال اسم الموقع") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("bookmark_title_input")
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        isUrlError = it.isBlank()
                    },
                    label = { Text("رابط الموقع (مثال: https://...)") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                    isError = isUrlError,
                    supportingText = if (isUrlError) { { Text("يرجى إدخال الرابط") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("bookmark_url_input")
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("التصنيف أو الكلية (اختياري، مثلاً: كلية الحقوق)") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("bookmark_category_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        isTitleError = true
                        return@Button
                    }
                    if (url.isBlank()) {
                        isUrlError = true
                        return@Button
                    }
                    onSave(title, url, category)
                },
                modifier = Modifier.testTag("save_bookmark_dialog_btn")
            ) {
                Text("حفظ في المفضلة")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_bookmark_dialog_btn")
            ) {
                Text("إلغاء")
            }
        }
    )
}
