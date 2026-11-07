package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.PortalEntity

val PALETTE_COLORS = listOf(
    0xFF133E87, // Academic Navy
    0xFFF59E0B, // Moodle Amber
    0xFF0D9488, // Teal
    0xFF7C3AED, // Violet
    0xFFE11D48, // Crimson
    0xFF059669, // Emerald
    0xFF2563EB  // Cobalt
)

@Composable
fun AddEditPortalDialog(
    initialPortal: PortalEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, url: String, institution: String, colorHex: Long, username: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initialPortal?.title ?: "") }
    var url by remember { mutableStateOf(initialPortal?.url ?: "") }
    var institution by remember { mutableStateOf(initialPortal?.institution ?: "") }
    var username by remember { mutableStateOf(initialPortal?.usernameNote ?: "") }
    var selectedColor by remember { mutableLongStateOf(initialPortal?.themeColorHex ?: PALETTE_COLORS[0]) }
    var isPinned by remember { mutableStateOf(initialPortal?.isPinned ?: false) }
    var isUrlError by remember { mutableStateOf(false) }
    var isTitleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (initialPortal == null) "إضافة بوابة تعليمية" else "تعديل البوابة",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = it.isBlank()
                    },
                    label = { Text("اسم البوابة / الموقع (مثال: مودل الجامعة)") },
                    leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    isError = isTitleError,
                    supportingText = if (isTitleError) { { Text("يرجى إدخال اسم البوابة") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("portal_title_input")
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        isUrlError = it.isBlank()
                    },
                    label = { Text("رابط الموقع (مثال: moodle.univ.edu)") },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                    isError = isUrlError,
                    supportingText = if (isUrlError) { { Text("يرجى إدخال الرابط الصحيح") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("portal_url_input")
                )

                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("الجامعة / الكلية / المؤسسة (اختياري)") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("portal_institution_input")
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("اسم المستخدم أو الرقم الجامعي (اختياري)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("portal_username_input")
                )

                Text(
                    text = "لون البوابة المميز:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PALETTE_COLORS.forEach { colorVal ->
                        val isSelected = selectedColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorVal }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تثبيت في أعلى القائمة",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        modifier = Modifier.testTag("pin_switch")
                    )
                }
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
                    onSave(title, url, institution, selectedColor, username, isPinned)
                },
                modifier = Modifier.testTag("save_portal_btn")
            ) {
                Text("حفظ البوابة")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_portal_btn")
            ) {
                Text("إلغاء")
            }
        }
    )
}
