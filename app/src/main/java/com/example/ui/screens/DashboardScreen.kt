package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ui.PortalViewModel

data class MoodleCourseHub(
    val id: String,
    val title: String,
    val code: String,
    val colorHex: Long,
    val url: String
)

val REAL_MOODLE_HUBS = listOf(
    MoodleCourseHub(
        id = "ufc_my_courses",
        title = "مقرراتي الدراسية المسجلة (My Courses)",
        code = "جامعة التكوين المتواصل - ماستر S1",
        colorHex = 0xFFF97316,
        url = "https://mast-da.ufc.dz/s1/my/courses.php"
    ),
    MoodleCourseHub(
        id = "ufc_catalog",
        title = "دليل وتصنيفات المقاييس (Catalog)",
        code = "تصفح جميع أقسام وتخصصات التكوين",
        colorHex = 0xFF0284C7,
        url = "https://mast-da.ufc.dz/s1/course/index.php"
    ),
    MoodleCourseHub(
        id = "ufc_dashboard",
        title = "لوحة المتابعة والمخطط (Dashboard)",
        code = "متابعة الواجبات والأنشطة القادمة",
        colorHex = 0xFF10B981,
        url = "https://mast-da.ufc.dz/s1/my/"
    ),
    MoodleCourseHub(
        id = "ufc_grades",
        title = "كشف النقاط والتقييمات (Grades)",
        code = "علامات الأعمال الموجهة والامتحانات",
        colorHex = 0xFF8B5CF6,
        url = "https://mast-da.ufc.dz/s1/grade/report/overview/index.php"
    ),
    MoodleCourseHub(
        id = "ufc_news",
        title = "منتدى الإعلانات الرسمية (Announcements)",
        code = "بيانات الإدارة وجداول الامتحانات",
        colorHex = 0xFFF59E0B,
        url = "https://mast-da.ufc.dz/s1/mod/forum/index.php?id=1"
    ),
    MoodleCourseHub(
        id = "fdsp_mosta",
        title = "كلية الحقوق والعلوم السياسية (FDSP)",
        code = "جامعة مستغانم - العلوم القانونية",
        colorHex = 0xFFEA580C,
        url = "https://ead.univ-mosta.dz/fdsp/course/index.php"
    )
)

@Composable
fun DashboardScreen(
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var timelineSearch by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF161616))
    ) {
        // Secondary Tabs: لوحة التحكم | الصفحة الرئيسية للموقع
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFFF97316),
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "لوحة التحكم",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) Color.White else Color(0xFF9E9E9E)
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "الصفحة الرئيسية للموقع",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) Color.White else Color(0xFF9E9E9E)
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                // Section: المخطط الزمني (Timeline)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "المخطط الزمني",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            OutlinedTextField(
                                value = timelineSearch,
                                onValueChange = { timelineSearch = it },
                                placeholder = { Text("بحث في مقاييس ومنصة UFC Moodle...", color = Color(0xFF757575)) },
                                leadingIcon = {
                                    IconButton(onClick = {
                                        if (timelineSearch.isNotBlank()) {
                                            viewModel.openCustomUrl(
                                                "https://mast-da.ufc.dz/s1/course/search.php?search=${timelineSearch.trim()}",
                                                "البحث: $timelineSearch"
                                            )
                                        }
                                    }) {
                                        Icon(Icons.Default.Search, contentDescription = "بحث", tint = Color(0xFFF97316))
                                    }
                                },
                                trailingIcon = {
                                    if (timelineSearch.isNotBlank()) {
                                        IconButton(onClick = {
                                            viewModel.openCustomUrl(
                                                "https://mast-da.ufc.dz/s1/course/search.php?search=${timelineSearch.trim()}",
                                                "البحث: $timelineSearch"
                                            )
                                        }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "انتقال للبحث", tint = Color(0xFFF97316))
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        if (timelineSearch.isNotBlank()) {
                                            viewModel.openCustomUrl(
                                                "https://mast-da.ufc.dz/s1/course/search.php?search=${timelineSearch.trim()}",
                                                "البحث: $timelineSearch"
                                            )
                                        }
                                    }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1E1E1E),
                                    unfocusedContainerColor = Color(0xFF1E1E1E),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("timeline_search_input")
                            )

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E1E1E))
                                    .clickable {
                                        viewModel.openCustomUrl(
                                            "https://mast-da.ufc.dz/s1/calendar/view.php?view=upcoming",
                                            "المخطط الزمني والأنشطة القادمة"
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
                                Text(
                                    text = "الثلاثون يوماً القادمة (عرض التكليفات في المنصة)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Active activities link button
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF1E1E1E))
                                    .clickable {
                                        viewModel.openCustomUrl(
                                            "https://mast-da.ufc.dz/s1/calendar/view.php?view=upcoming",
                                            "التكليفات والواجبات المطلوبة"
                                        )
                                    }
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF97316).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Text(
                                    text = "فتح المخطط الزمني والتكليفات المباشرة على UFC",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFFED7AA),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "انقر هنا لعرض الواجبات والمواعيد المقررة في حسابك الجامعي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }
                }

                // Section: مقررات دراسية تم الوصول إليها مؤخراً
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مقررات دراسية تم الوصول إليها مؤخراً",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9E9E9E))
                                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color(0xFF9E9E9E))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(REAL_MOODLE_HUBS) { hub ->
                                ElevatedCard(
                                    modifier = Modifier
                                        .width(230.dp)
                                        .clickable {
                                            viewModel.openCustomUrl(hub.url, hub.title)
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = Color(0xFF242424)
                                    )
                                ) {
                                    Column {
                                        // Colorful course top banner
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(75.dp)
                                                .background(Color(hub.colorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.9f),
                                                modifier = Modifier.size(38.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = hub.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF333333))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = hub.code,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFFFDE68A),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section: التقويم (Calendar shortcut)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateToMoodleShortcut("/calendar/view.php")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF9E9E9E))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "التقويم والمواعيد الجامعية",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFFF97316))
                            }
                        }
                    }
                }

                // Section: مدير المستندات والملفات المحملة
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateTo(com.example.ui.AppScreen.DOWNLOADS)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF9E9E9E))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "مدير ملفات ومستندات المقررات (PDF / Docs)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            } else {
                // Section: الصفحة الرئيسية للموقع (Site Home) inspired by Screenshot 5 & 9
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SiteHomeLinkCard(
                            title = "بوابة جامعة التكوين المتواصل - دخول ماستر S1 (UFC)",
                            url = "https://mast-da.ufc.dz/s1/login/index.php",
                            onOpen = { viewModel.openCustomUrl(it, "منصة UFC مودل") }
                        )

                        SiteHomeLinkCard(
                            title = "عبر الهاتف خاص بالطلبة MOODLE كيفية تحديث تطبيق",
                            url = "https://www.youtube.com/shorts/Q2dJ_Na3fyl",
                            onOpen = { viewModel.openCustomUrl(it, "فيديو تحديث مودل") }
                        )

                        SiteHomeLinkCard(
                            title = "رابط قناة كلية الحقوق والعلوم السياسية",
                            url = "https://ead.univ-mosta.dz/fdsp/channel",
                            onOpen = { viewModel.openCustomUrl(it, "قناة الكلية") }
                        )

                        SiteHomeLinkCard(
                            title = "رابط موقع كلية الحقوق والعلوم السياسية الرسمي",
                            url = "https://ead.univ-mosta.dz/fdsp",
                            onOpen = { viewModel.openCustomUrl(it, "موقع الكلية") }
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openCustomUrl("https://ead.univ-mosta.dz/fdsp/course/index.php") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF9E9E9E))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "تصنيفات المقررات",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFF97316))
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openCustomUrl("https://ead.univ-mosta.dz/fdsp/course/index.php") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF9E9E9E))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "المقررات المتاحة",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFF97316))
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
fun SiteHomeLinkCard(
    title: String,
    url: String,
    onOpen: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(url) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF9E9E9E))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF60A5FA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFF38BDF8))
            }
        }
    }
}
