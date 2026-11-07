package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.PortalEntity
import com.example.ui.PortalViewModel
import com.example.ui.components.PortalCard

data class FeaturedPortal(
    val title: String,
    val institution: String,
    val url: String,
    val colorHex: Long
)

val FEATURED_PORTALS = listOf(
    FeaturedPortal(
        title = "جامعة التكوين المتواصل (UFC S1)",
        institution = "ماستر قانون الأعمال - عن بعد",
        url = "https://mast-da.ufc.dz/s1/login/index.php",
        colorHex = 0xFFF97316
    ),
    FeaturedPortal(
        title = "جامعة مستغانم - الحقوق",
        institution = "LA FACULTE DE DROIT ET SCIENCES POLITIQUES",
        url = "https://ead.univ-mosta.dz/fdsp",
        colorHex = 0xFFEA580C
    ),
    FeaturedPortal(
        title = "مودل التجريبي المفتوح",
        institution = "Moodle HQ Sandbox",
        url = "https://sandbox.moodledemo.net",
        colorHex = 0xFF2563EB
    ),
    FeaturedPortal(
        title = "منصة إدراك التعليمية",
        institution = "مؤسسة الملكة رانيا للتعليم",
        url = "https://www.edraak.org",
        colorHex = 0xFF1E3A8A
    ),
    FeaturedPortal(
        title = "خان أكاديمي بالعربية",
        institution = "Khan Academy Global",
        url = "https://ar.khanacademy.org",
        colorHex = 0xFF0D9488
    ),
    FeaturedPortal(
        title = "أكاديمية حسوب",
        institution = "دورات البرمجة والعلوم",
        url = "https://academy.hsoub.com",
        colorHex = 0xFF059669
    )
)

@Composable
fun PortalsListScreen(
    portals: List<PortalEntity>,
    viewModel: PortalViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var quickUrlInput by remember { mutableStateOf("") }
    var isReorderMode by remember { mutableStateOf(false) }

    val filteredPortals = remember(portals, searchQuery) {
        if (searchQuery.isBlank()) {
            portals
        } else {
            portals.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.institution.contains(searchQuery, ignoreCase = true) ||
                it.url.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner Card
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.portal_hero_banner_1794000151854),
                            contentDescription = "بوابة التعلم الإلكتروني",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xCC1E1E1E),
                                            Color(0xF51E1E1E)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF97316)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "تطبيق وسيط لبوابات مودل والجامعات",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "تصفح مقرراتك الجامعية ومواقعك المفضلة وأعد ترتيبها وحفظها بكل حرية",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Quick Direct URL Opener
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF262626)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "دخول سريع لرابط مودل أو أي منصة:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = quickUrlInput,
                                onValueChange = { quickUrlInput = it },
                                placeholder = { Text("أدخل رابط الموقع، مثلاً: ead.univ-mosta.dz/fdsp") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Language,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316)
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_url_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1E1E1E),
                                    unfocusedContainerColor = Color(0xFF1E1E1E)
                                ),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        if (quickUrlInput.isNotBlank()) {
                                            viewModel.openCustomUrl(quickUrlInput)
                                        }
                                    }
                                )
                            )
                            Button(
                                onClick = {
                                    if (quickUrlInput.isNotBlank()) {
                                        viewModel.openCustomUrl(quickUrlInput)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF97316)
                                ),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("quick_go_btn")
                            ) {
                                Text("فتح", color = Color.White)
                            }
                        }
                    }
                }
            }

            // Featured Portals Section (Carousel)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFFF97316),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "بوابات ومنصات مقترحة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(FEATURED_PORTALS) { featured ->
                            ElevatedCard(
                                modifier = Modifier
                                    .width(190.dp)
                                    .clickable {
                                        viewModel.openCustomUrl(featured.url, featured.title)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = Color(0xFF262626)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(featured.colorHex).copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.School,
                                            contentDescription = null,
                                            tint = Color(featured.colorHex),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = featured.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = featured.institution,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF9E9E9E),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "دخول سريع",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFF97316),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFFF97316),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Portals List Header with Reorder button
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "بواباتي التعليمية (${portals.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isReorderMode = !isReorderMode },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("toggle_portal_reorder_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DragHandle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isReorderMode) "تم" else "ترتيب")
                            }

                            Text(
                                text = "+ إضافة",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color(0xFFF97316),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.showAddPortal() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("header_add_portal_btn")
                            )
                        }
                    }

                    if (portals.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث في بواباتي...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_portals_input")
                        )
                    }
                }
            }

            // Portals List
            if (filteredPortals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF242424)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF9E9E9E),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (searchQuery.isBlank()) "لم تقم بإضافة بوابات بعد" else "لا توجد نتائج مطابقة لبحثك",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "أضف موقع مودل كليتك للوصول السريع والترتيب المخصص",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9E9E9E)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.showAddPortal() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF97316)
                                ),
                                modifier = Modifier.testTag("empty_add_portal_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إضافة أول بوابة")
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = filteredPortals,
                    key = { _, portal -> portal.id }
                ) { index, portal ->
                    PortalCard(
                        portal = portal,
                        index = index,
                        totalCount = filteredPortals.size,
                        isReorderMode = isReorderMode,
                        onOpen = { viewModel.openPortal(portal) },
                        onEdit = { viewModel.showAddPortal(portal) },
                        onDelete = { viewModel.deletePortal(portal) },
                        onMoveUp = { viewModel.movePortalUp(portal) },
                        onMoveDown = { viewModel.movePortalDown(portal) },
                        onToggleFavorite = { viewModel.togglePortalFavorite(portal) }
                    )
                }
            }
        }

        // Floating Action Button to Add Portal
        FloatingActionButton(
            onClick = { viewModel.showAddPortal() },
            containerColor = Color(0xFFF97316),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_portal_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة بوابة")
        }
    }
}
