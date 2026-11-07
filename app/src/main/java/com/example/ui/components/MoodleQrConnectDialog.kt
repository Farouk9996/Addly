package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MoodleQrConnectDialog(
    onDismiss: () -> Unit,
    onConnect: (url: String, title: String) -> Unit
) {
    var inputCodeOrUrl by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color(0xFFF97316),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "مسح ترميز QR أو رابط مودل",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "أدخل رابط بوابة مودل الخاصة بجامعتك أو الصق محتوى رمز QR للاتصال الفوري:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = inputCodeOrUrl,
                    onValueChange = {
                        inputCodeOrUrl = it
                        isError = false
                    },
                    placeholder = { Text("مثال: https://mast-da.ufc.dz/s1/") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                    singleLine = true,
                    isError = isError,
                    supportingText = if (isError) { { Text("يرجى إدخال رابط أو رمز صحيح") } } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("qr_input_field")
                )

                Text(
                    text = "بوابات جامعية سريعة:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF97316)
                )

                QuickCampusChip("جامعة التكوين المتواصل UFC S1", "https://mast-da.ufc.dz/s1/") {
                    inputCodeOrUrl = "https://mast-da.ufc.dz/s1/"
                }

                QuickCampusChip("جامعة مستغانم - كلية الحقوق", "https://ead.univ-mosta.dz/fdsp") {
                    inputCodeOrUrl = "https://ead.univ-mosta.dz/fdsp"
                }

                QuickCampusChip("مودل التجريبي المفتوح Moodle Sandbox", "https://sandbox.moodledemo.net") {
                    inputCodeOrUrl = "https://sandbox.moodledemo.net"
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputCodeOrUrl.isBlank()) {
                        isError = true
                        return@Button
                    }
                    onConnect(inputCodeOrUrl.trim(), "بوابة مودل")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                modifier = Modifier.testTag("connect_qr_btn")
            ) {
                Text("اتصال وفتح", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun QuickCampusChip(
    title: String,
    url: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF262626))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = url, style = MaterialTheme.typography.labelSmall, color = Color(0xFF9E9E9E))
        }
    }
}
