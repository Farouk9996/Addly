package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.AppScreen
import com.example.ui.PortalViewModel
import com.example.ui.components.AddBookmarkDialog
import com.example.ui.components.AddEditPortalDialog
import com.example.ui.components.MoodleAccountSheet
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PortalsListScreen
import com.example.ui.screens.WebBrowserScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PortalViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val portals by viewModel.portals.collectAsState()
                val bookmarks by viewModel.bookmarks.collectAsState()
                val downloads by viewModel.downloads.collectAsState()
                val notifications by viewModel.notifications.collectAsState()
                val browserState by viewModel.browserState.collectAsState()
                val showAddPortal by viewModel.showAddPortalDialog.collectAsState()
                val editingPortal by viewModel.editingPortal.collectAsState()
                val showAddBookmark by viewModel.showAddBookmarkDialog.collectAsState()
                val showAccountSheet by viewModel.showAccountSheet.collectAsState()

                val unreadCount = notifications.count { it.isUnread }

                // BackHandler for secondary screens
                BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.BROWSER) {
                    viewModel.navigateTo(AppScreen.DASHBOARD)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF161616),
                    topBar = {
                        if (currentScreen != AppScreen.BROWSER) {
                            MoodleMainTopBar(
                                currentScreen = currentScreen,
                                onOpenAccount = { viewModel.toggleAccountSheet(true) },
                                onSearchClick = { viewModel.navigateTo(AppScreen.PORTALS) },
                                onDownloadsClick = { viewModel.navigateTo(AppScreen.DOWNLOADS) }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != AppScreen.BROWSER) {
                            MoodleBottomNavigationBar(
                                currentScreen = currentScreen,
                                unreadCount = unreadCount,
                                onTabSelected = { screen -> viewModel.navigateTo(screen) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel
                            )
                            AppScreen.PORTALS -> PortalsListScreen(
                                portals = portals,
                                viewModel = viewModel
                            )
                            AppScreen.BOOKMARKS -> BookmarksScreen(
                                bookmarks = bookmarks,
                                viewModel = viewModel
                            )
                            AppScreen.MESSAGES -> MessagesScreen(
                                viewModel = viewModel
                            )
                            AppScreen.NOTIFICATIONS -> NotificationsScreen(
                                notifications = notifications,
                                viewModel = viewModel
                            )
                            AppScreen.MORE -> MoreScreen(
                                viewModel = viewModel
                            )
                            AppScreen.DOWNLOADS -> DownloadsScreen(
                                downloads = downloads,
                                viewModel = viewModel
                            )
                            AppScreen.BROWSER -> WebBrowserScreen(
                                browserState = browserState,
                                viewModel = viewModel,
                                onCloseBrowser = { viewModel.navigateTo(AppScreen.PORTALS) }
                            )
                        }
                    }

                    // Dialogs
                    if (showAddPortal) {
                        AddEditPortalDialog(
                            initialPortal = editingPortal,
                            onDismiss = { viewModel.hideAddPortal() },
                            onSave = { title, url, institution, colorHex, username, isPinned ->
                                viewModel.savePortal(title, url, institution, colorHex, username, isPinned)
                            }
                        )
                    }

                    if (showAddBookmark) {
                        AddBookmarkDialog(
                            onDismiss = { viewModel.hideAddBookmarkDialog() },
                            onSave = { title, url, category ->
                                viewModel.addCustomBookmark(title, url, category)
                            }
                        )
                    }

                    if (showAccountSheet) {
                        MoodleAccountSheet(
                            onDismiss = { viewModel.toggleAccountSheet(false) },
                            onOpenUrl = { url, title ->
                                viewModel.openCustomUrl(url, title)
                            },
                            onNavigateShortcut = { shortcut ->
                                viewModel.navigateToMoodleShortcut(shortcut)
                            },
                            onOpenDownloads = {
                                viewModel.navigateTo(AppScreen.DOWNLOADS)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodleMainTopBar(
    currentScreen: AppScreen,
    onOpenAccount: () -> Unit,
    onSearchClick: () -> Unit,
    onDownloadsClick: () -> Unit = {}
) {
    Surface(
        color = Color(0xFF1E1E1E),
        shadowElevation = 4.dp
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UFC • SCIENCES POLITIQUES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            },
            navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Box(modifier = Modifier.size(36.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF263238))
                                .clickable(onClick = onOpenAccount),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "الملف الشخصي",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Green active dot
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                                .align(Alignment.BottomEnd)
                        )
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = onDownloadsClick,
                    modifier = Modifier.testTag("top_downloads_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "المستندات المحملة",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1E1E1E)
            )
        )
    }
}

@Composable
fun MoodleBottomNavigationBar(
    currentScreen: AppScreen,
    unreadCount: Int,
    onTabSelected: (AppScreen) -> Unit
) {
    Surface(
        color = Color(0xFF1E1E1E),
        shadowElevation = 8.dp
    ) {
        Column {
            HorizontalDivider(color = Color(0xFF2C2C2C))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoodleNavItem(
                    icon = Icons.Default.MoreHoriz,
                    isSelected = currentScreen == AppScreen.MORE,
                    testTag = "nav_more",
                    onClick = { onTabSelected(AppScreen.MORE) }
                )

                MoodleNavItem(
                    icon = Icons.Default.Notifications,
                    badgeCount = unreadCount,
                    isSelected = currentScreen == AppScreen.NOTIFICATIONS,
                    testTag = "nav_notifications",
                    onClick = { onTabSelected(AppScreen.NOTIFICATIONS) }
                )

                MoodleNavItem(
                    icon = Icons.Default.Bookmark,
                    isSelected = currentScreen == AppScreen.BOOKMARKS,
                    testTag = "nav_bookmarks",
                    onClick = { onTabSelected(AppScreen.BOOKMARKS) }
                )

                MoodleNavItem(
                    icon = Icons.Default.School,
                    isSelected = currentScreen == AppScreen.PORTALS,
                    testTag = "nav_portals",
                    onClick = { onTabSelected(AppScreen.PORTALS) }
                )

                MoodleNavItem(
                    icon = Icons.Default.Speed,
                    isSelected = currentScreen == AppScreen.DASHBOARD,
                    testTag = "nav_dashboard",
                    onClick = { onTabSelected(AppScreen.DASHBOARD) }
                )
            }
        }
    }
}

@Composable
fun MoodleNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        // Orange top indicator line matching Moodle screenshot
        Box(
            modifier = Modifier
                .height(3.dp)
                .size(width = 36.dp, height = 3.dp)
                .background(if (isSelected) Color(0xFFF97316) else Color.Transparent)
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color(0xFFF97316),
                        contentColor = Color.White
                    ) {
                        Text(text = badgeCount.toString(), fontWeight = FontWeight.Bold)
                    }
                }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color(0xFF9E9E9E),
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF9E9E9E),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
