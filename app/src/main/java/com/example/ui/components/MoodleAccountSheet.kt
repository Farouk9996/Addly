package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import android.app.Activity
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.ui.PortalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodleAccountSheet(
    viewModel: PortalViewModel,
    onDismiss: () -> Unit,
    onOpenUrl: (String, String) -> Unit = { _, _ -> },
    onNavigateShortcut: (String) -> Unit,
    onOpenDownloads: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Selection between the requested UFC S1 and FDSP Mosta
    var selectedSite by remember { mutableStateOf("ufc") }
    var showSwitchDialog by remember { mutableStateOf(false) }

    val ufcLoginUrl = "https://mast-da.ufc.dz/s1/login/index.php"
    val mostaUrl = "https://ead.univ-mosta.dz/fdsp"

    val firebaseUser by viewModel.firebaseUser.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val syncMessage by viewModel.cloudSyncMessage.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E),
        contentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Close and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_account_sheet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = Color.White
                    )
                }
                Text(
                    text = "حساب المستخدم والمزامنة السحابية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Firebase Cloud Sync Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (firebaseUser != null) Color(0xFF22C55E).copy(alpha = 0.5f) else Color(0xFF38BDF8).copy(alpha = 0.4f),
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF24282F))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (firebaseUser != null) Color(0xFF16A34A) else Color(0xFF0284C7))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (firebaseUser != null) "المزامنة السحابية نشطة (Firebase) ✓" else "مزامنة سحابية (Firebase Firestore)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = if (firebaseUser != null) Icons.Default.CloudDone else Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = if (firebaseUser != null) Color(0xFF4ADE80) else Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (firebaseUser != null) {
                        Text(
                            text = firebaseUser?.displayName ?: "مستخدم مسجل في السحابة",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        firebaseUser?.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9E9E9E)
                            )
                        }

                        if (!syncMessage.isNullOrBlank()) {
                            Text(
                                text = syncMessage ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF86EFAC)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.syncWithCloud() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isCloudSyncing,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("sync_cloud_now_btn")
                            ) {
                                if (isCloudSyncing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Text("مزامنة البيانات الآن", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.signOutGoogle() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(40.dp)
                                    .testTag("sign_out_google_btn")
                            ) {
                                Text("خروج", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text(
                            text = "سجل الدخول بحساب Google لمزامنة بواباتك ومفضلاتك ومستنداتك سحابياً عبر Firebase Firestore والوصول إليها بأمان من أي جهاز.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD1D5DB)
                        )

                        if (!syncMessage.isNullOrBlank()) {
                            Text(
                                text = syncMessage ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFCA5A5)
                            )
                        }

                        Button(
                            onClick = {
                                activity?.let { act ->
                                    viewModel.signInWithGoogle(act)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("google_signin_sync_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "تسجيل الدخول بواسطة Google للمزامنة السحابية",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Moodle Logo in Orange
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFFF97316),
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "moodle",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF97316)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "مدمج داخل التطبيق",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Connected Institution Card: UFC S1 (Directly requested)
            if (selectedSite == "ufc") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFF97316).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF97316))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "بوابة الماستر S1 المعتمدة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "UNIVERSITE DE LA FORMATION CONTINUE - UFC ALGERIE",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "ماستر التعليم عن بعد - السداسي الأول (S1)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFED7AA)
                        )

                        Text(
                            text = ufcLoginUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.clickable {
                                onOpenUrl(ufcLoginUrl, "بوابة جامعة التكوين المتواصل UFC S1")
                                onDismiss()
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Big Direct Login Button to Embed the UFC Website
                        Button(
                            onClick = {
                                onOpenUrl(ufcLoginUrl, "بوابة جامعة التكوين المتواصل UFC S1")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("ufc_direct_login_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "فتح صفحة الدخول المدمجة (UFC Login)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Secondary Institution: Univ-Mosta FDSP
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF262626))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "LA FACULTE DE DROIT ET DES SCIENCES POLITIQUES",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = mostaUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF60A5FA),
                            modifier = Modifier.clickable {
                                onOpenUrl(mostaUrl, "كلية الحقوق والعلوم السياسية")
                                onDismiss()
                            }
                        )
                        Button(
                            onClick = {
                                onOpenUrl(mostaUrl, "كلية الحقوق والعلوم السياسية")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("فتح بوابة الكلية المدمجة", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(10.dp))

            // User Info with Avatar and Active dot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ZIANE Mohammed",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedSite == "ufc") "طالب ماستر UFC S1" else "طالب حقوق وعلوم سياسية",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF9E9E9E)
                        )
                    }

                    Box(modifier = Modifier.size(42.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF263238)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF4DD0E1),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        // Green online badge
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Links to Moodle portal endpoints
            AccountMenuItem(
                title = "تسجيل الدخول المباشر للبوابة (UFC S1)",
                icon = Icons.AutoMirrored.Filled.Login
            ) {
                onOpenUrl(ufcLoginUrl, "جامعة التكوين المتواصل UFC S1")
                onDismiss()
            }

            AccountMenuItem("لوحة تحكم المقررات (Dashboard)", Icons.Default.School) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/my/" else "https://ead.univ-mosta.dz/fdsp/my/"
                onOpenUrl(url, "لوحة التحكم")
                onDismiss()
            }

            AccountMenuItem("المقررات المسجلة (My Courses)", Icons.Default.MenuBook) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/my/courses.php" else "https://ead.univ-mosta.dz/fdsp/my/courses.php"
                onOpenUrl(url, "مقرراتي")
                onDismiss()
            }

            AccountMenuItem("التقديرات وكشف النقاط", Icons.Default.Assessment) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/grade/report/overview/index.php" else "https://ead.univ-mosta.dz/fdsp/grade/report/overview/index.php"
                onOpenUrl(url, "الدرجات")
                onDismiss()
            }

            AccountMenuItem("ملفات ومستندات المقررات المحملة (Download Manager)", Icons.Default.Folder) {
                onOpenDownloads()
                onDismiss()
            }

            AccountMenuItem("التقويم والمواعيد الجامعية", Icons.Default.DateRange) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/calendar/view.php" else "https://ead.univ-mosta.dz/fdsp/calendar/view.php"
                onOpenUrl(url, "التقويم")
                onDismiss()
            }

            AccountMenuItem("التقارير وسجل النشاط", Icons.Default.Assessment) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/report/outline/index.php" else "https://ead.univ-mosta.dz/fdsp/report/outline/index.php"
                onOpenUrl(url, "التقارير")
                onDismiss()
            }

            AccountMenuItem("الشارات والإنجازات", Icons.Default.EmojiEvents) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/badges/mybadges.php" else "https://ead.univ-mosta.dz/fdsp/badges/mybadges.php"
                onOpenUrl(url, "الشارات")
                onDismiss()
            }

            AccountMenuItem("مشاركات المدونة والمنتديات", Icons.Default.Language) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/blog/index.php" else "https://ead.univ-mosta.dz/fdsp/blog/index.php"
                onOpenUrl(url, "المدونة")
                onDismiss()
            }

            AccountMenuItem("التفضيلات وإعدادات الحساب", Icons.Default.Settings) {
                val url = if (selectedSite == "ufc") "https://mast-da.ufc.dz/s1/user/preferences.php" else "https://ead.univ-mosta.dz/fdsp/user/preferences.php"
                onOpenUrl(url, "التفضيلات")
                onDismiss()
            }

            AccountMenuItem("تبديل المؤسسة / الحساب", Icons.Default.SwapHoriz) {
                selectedSite = if (selectedSite == "ufc") "mosta" else "ufc"
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Red Log Out Button matching screenshot
            Button(
                onClick = {
                    onOpenUrl(ufcLoginUrl, "بوابة جامعة التكوين المتواصل UFC")
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_sheet_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الخروج والعودة لصفحة الدخول",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = Color(0xFF757575),
            modifier = Modifier.size(16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
