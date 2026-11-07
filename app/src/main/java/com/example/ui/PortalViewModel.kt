package com.example.ui

import android.app.Application
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PortalRepository
import com.example.data.model.BookmarkEntity
import com.example.data.model.CourseNoteEntity
import com.example.data.model.DownloadedFileEntity
import com.example.data.model.NotificationItem
import com.example.data.model.PortalEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    PORTALS,
    BOOKMARKS,
    MESSAGES,
    NOTIFICATIONS,
    MORE,
    BROWSER,
    DOWNLOADS
}

data class BrowserUiState(
    val currentUrl: String = "",
    val pageTitle: String = "",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val isSecure: Boolean = true,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false,
    val isDarkModeInverted: Boolean = false,
    val textZoom: Int = 100,
    val activePortal: PortalEntity? = null,
    val isStayLoggedInEnabled: Boolean = true,
    val savedUsername: String = "ZIANE Mohammed",
    val savedPassword: String = "",
    val autoFillEnabled: Boolean = true
)

class PortalViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PortalRepository
    private val authPrefs = application.getSharedPreferences("moodle_auth_prefs", Context.MODE_PRIVATE)

    init {
        val database = AppDatabase.getInstance(application)
        repository = PortalRepository(database.portalDao())

        val stayLoggedIn = authPrefs.getBoolean("stay_logged_in", true)
        val savedUser = authPrefs.getString("saved_username", "ZIANE Mohammed") ?: "ZIANE Mohammed"
        val savedPass = authPrefs.getString("saved_password", "") ?: ""
        val autoFill = authPrefs.getBoolean("auto_fill", true)

        _browserState.update {
            it.copy(
                isStayLoggedInEnabled = stayLoggedIn,
                savedUsername = savedUser,
                savedPassword = savedPass,
                autoFillEnabled = autoFill
            )
        }
    }

    val portals: StateFlow<List<PortalEntity>> = repository.allPortals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadedFileEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<CourseNoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _browserState = MutableStateFlow(BrowserUiState())
    val browserState: StateFlow<BrowserUiState> = _browserState.asStateFlow()

    private val _editingPortal = MutableStateFlow<PortalEntity?>(null)
    val editingPortal: StateFlow<PortalEntity?> = _editingPortal.asStateFlow()

    private val _showAddPortalDialog = MutableStateFlow(false)
    val showAddPortalDialog: StateFlow<Boolean> = _showAddPortalDialog.asStateFlow()

    private val _showAddBookmarkDialog = MutableStateFlow(false)
    val showAddBookmarkDialog: StateFlow<Boolean> = _showAddBookmarkDialog.asStateFlow()

    private val _showAccountSheet = MutableStateFlow(false)
    val showAccountSheet: StateFlow<Boolean> = _showAccountSheet.asStateFlow()

    private val _isReorderMode = MutableStateFlow(false)
    val isReorderMode: StateFlow<Boolean> = _isReorderMode.asStateFlow()

    // Sample Moodle notifications inspired by screenshots
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(
                id = "1",
                title = "تسجيل دخول جديد لحسابك في LA FACULTE DE DROIT ET DES SCIENCES POLITIQUES",
                sender = "بوابة مودل المركزية",
                timeAgo = "منذ 12 يوماً",
                isUnread = true,
                initials = "FD"
            ),
            NotificationItem(
                id = "2",
                title = "تم نشر موضوع جديد في منتدى: الذكاء الاصطناعي وتطبيقاته",
                sender = "أستاذ المادة - د. بن قتات",
                timeAgo = "منذ 16 يوماً",
                isUnread = true,
                initials = "AI"
            ),
            NotificationItem(
                id = "3",
                title = "مرحبا في منهجية البحث العلمي 1 - يرجى مراجعة المطبوعة",
                sender = "AICHA DOUIDI",
                timeAgo = "منذ 5 أشهر",
                isUnread = true,
                initials = "AD"
            ),
            NotificationItem(
                id = "4",
                title = "مرحبا في مطبوعة منهجية البحث العلمي",
                sender = "amina touaoula",
                timeAgo = "منذ 5 أشهر",
                isUnread = true,
                initials = "AT"
            ),
            NotificationItem(
                id = "5",
                title = "مرحبا في تنازع الاختصاص س02",
                sender = "BENAZZOUZ Bensabeur",
                timeAgo = "منذ 5 أشهر",
                isUnread = true,
                initials = "BB"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleReorderMode() {
        _isReorderMode.update { !it }
    }

    fun toggleAccountSheet(show: Boolean) {
        _showAccountSheet.value = show
    }

    // Portal Reordering
    fun movePortalUp(portal: PortalEntity) {
        val list = portals.value.toMutableList()
        val index = list.indexOfFirst { it.id == portal.id }
        if (index > 0) {
            val prev = list[index - 1]
            list[index - 1] = portal
            list[index] = prev
            viewModelScope.launch {
                repository.reorderPortals(list)
            }
        }
    }

    fun movePortalDown(portal: PortalEntity) {
        val list = portals.value.toMutableList()
        val index = list.indexOfFirst { it.id == portal.id }
        if (index >= 0 && index < list.size - 1) {
            val next = list[index + 1]
            list[index + 1] = portal
            list[index] = next
            viewModelScope.launch {
                repository.reorderPortals(list)
            }
        }
    }

    fun togglePortalFavorite(portal: PortalEntity) {
        viewModelScope.launch {
            repository.togglePortalFavorite(portal.id, !portal.isFavorite)
        }
    }

    // Bookmark Reordering
    fun moveBookmarkUp(bookmark: BookmarkEntity) {
        val list = bookmarks.value.toMutableList()
        val index = list.indexOfFirst { it.id == bookmark.id }
        if (index > 0) {
            val prev = list[index - 1]
            list[index - 1] = bookmark
            list[index] = prev
            viewModelScope.launch {
                repository.reorderBookmarks(list)
            }
        }
    }

    fun moveBookmarkDown(bookmark: BookmarkEntity) {
        val list = bookmarks.value.toMutableList()
        val index = list.indexOfFirst { it.id == bookmark.id }
        if (index >= 0 && index < list.size - 1) {
            val next = list[index + 1]
            list[index + 1] = bookmark
            list[index] = next
            viewModelScope.launch {
                repository.reorderBookmarks(list)
            }
        }
    }

    // Browser Management
    fun openPortal(portal: PortalEntity, targetUrl: String? = null) {
        viewModelScope.launch {
            repository.recordVisit(portal.id)
            val initialUrl = targetUrl ?: portal.url.trim()
            val formattedUrl = if (!initialUrl.startsWith("http://") && !initialUrl.startsWith("https://")) {
                "https://$initialUrl"
            } else {
                initialUrl
            }
            _browserState.update {
                it.copy(
                    activePortal = portal,
                    currentUrl = formattedUrl,
                    pageTitle = portal.title,
                    isDesktopMode = portal.isDesktopModeDefault,
                    progress = 0,
                    isLoading = true,
                    isSecure = formattedUrl.startsWith("https://")
                )
            }
            _currentScreen.value = AppScreen.BROWSER
        }
    }

    fun openCustomUrl(inputUrl: String, title: String? = null) {
        val trimmed = inputUrl.trim()
        val url = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else if (trimmed.contains(".") && !trimmed.contains(" ")) {
            "https://$trimmed"
        } else {
            "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }

        _browserState.update {
            it.copy(
                currentUrl = url,
                pageTitle = title ?: trimmed,
                isLoading = true,
                progress = 0,
                isSecure = url.startsWith("https://")
            )
        }
        _currentScreen.value = AppScreen.BROWSER
    }

    fun updateWebProgress(progress: Int, title: String?, url: String?) {
        _browserState.update {
            it.copy(
                progress = progress,
                isLoading = progress < 100,
                pageTitle = if (!title.isNullOrBlank()) title else it.pageTitle,
                currentUrl = if (!url.isNullOrBlank()) url else it.currentUrl,
                isSecure = (url ?: it.currentUrl).startsWith("https://")
            )
        }
    }

    fun updateNavState(canGoBack: Boolean, canGoForward: Boolean) {
        _browserState.update {
            it.copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
        }
    }

    fun toggleDesktopMode() {
        _browserState.update { it.copy(isDesktopMode = !it.isDesktopMode) }
    }

    fun toggleDarkModeInvert() {
        _browserState.update { it.copy(isDarkModeInverted = !it.isDarkModeInverted) }
    }

    fun setTextZoom(zoomPercent: Int) {
        _browserState.update { it.copy(textZoom = zoomPercent.coerceIn(70, 180)) }
    }

    fun navigateToMoodleShortcut(shortcutPath: String) {
        val current = _browserState.value.currentUrl
        if (current.isBlank()) return

        try {
            val uri = android.net.Uri.parse(current)
            val scheme = uri.scheme ?: "https"
            val host = uri.host ?: return
            val port = if (uri.port != -1) ":${uri.port}" else ""
            val cleanPath = if (shortcutPath.startsWith("/")) shortcutPath else "/$shortcutPath"
            val newUrl = "$scheme://$host$port$cleanPath"
            _browserState.update { it.copy(currentUrl = newUrl, isLoading = true, progress = 0) }
        } catch (_: Exception) {}
    }

    // Portal CRUD
    fun showAddPortal(portal: PortalEntity? = null) {
        _editingPortal.value = portal
        _showAddPortalDialog.value = true
    }

    fun hideAddPortal() {
        _editingPortal.value = null
        _showAddPortalDialog.value = false
    }

    fun savePortal(
        title: String,
        url: String,
        institution: String,
        themeColorHex: Long,
        usernameNote: String,
        isPinned: Boolean
    ) {
        viewModelScope.launch {
            val cleanUrl = url.trim()
            val finalUrl = if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
                "https://$cleanUrl"
            } else {
                cleanUrl
            }

            val existing = _editingPortal.value
            val portal = existing?.copy(
                title = title.trim(),
                url = finalUrl,
                institution = institution.trim(),
                themeColorHex = themeColorHex,
                usernameNote = usernameNote.trim(),
                isPinned = isPinned
            ) ?: PortalEntity(
                title = title.trim(),
                url = finalUrl,
                institution = institution.trim(),
                themeColorHex = themeColorHex,
                usernameNote = usernameNote.trim(),
                isPinned = isPinned,
                sortOrder = portals.value.size
            )

            repository.savePortal(portal)
            hideAddPortal()
        }
    }

    fun deletePortal(portal: PortalEntity) {
        viewModelScope.launch {
            repository.deletePortal(portal)
            if (_browserState.value.activePortal?.id == portal.id) {
                _browserState.update { it.copy(activePortal = null) }
            }
        }
    }

    // Bookmarks Management
    fun showAddBookmarkDialog() {
        _showAddBookmarkDialog.value = true
    }

    fun hideAddBookmarkDialog() {
        _showAddBookmarkDialog.value = false
    }

    fun addCustomBookmark(title: String, url: String, portalTitle: String) {
        viewModelScope.launch {
            val cleanUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://${url.trim()}" else url.trim()
            repository.saveBookmark(
                BookmarkEntity(
                    portalId = 0L,
                    portalTitle = portalTitle.ifBlank { "موقع مفضل" },
                    title = title.trim(),
                    url = cleanUrl,
                    isFavorite = true,
                    sortOrder = bookmarks.value.size
                )
            )
            hideAddBookmarkDialog()
        }
    }

    fun bookmarkCurrentPage() {
        val state = _browserState.value
        val url = state.currentUrl
        if (url.isBlank()) return

        viewModelScope.launch {
            repository.saveBookmark(
                BookmarkEntity(
                    portalId = state.activePortal?.id ?: 0L,
                    portalTitle = state.activePortal?.title ?: "البوابة الحالية",
                    title = state.pageTitle.ifBlank { "صفحة دراسية محفوظة" },
                    url = url,
                    isFavorite = true,
                    sortOrder = bookmarks.value.size
                )
            )
        }
    }

    fun toggleBookmarkFavorite(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.saveBookmark(bookmark.copy(isFavorite = !bookmark.isFavorite))
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
        }
    }

    // Downloads
    fun registerDownload(fileName: String, fileUri: String, fileSizeBytes: Long, mimeType: String) {
        viewModelScope.launch {
            repository.saveDownload(
                DownloadedFileEntity(
                    portalId = _browserState.value.activePortal?.id ?: 0L,
                    fileName = fileName,
                    fileUri = fileUri,
                    fileSizeBytes = fileSizeBytes,
                    mimeType = mimeType
                )
            )
        }
    }

    fun deleteDownload(download: DownloadedFileEntity) {
        viewModelScope.launch {
            repository.deleteDownload(download)
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        _notifications.update { list ->
            list.map { it.copy(isUnread = false) }
        }
    }

    // Clear Cache & Data
    fun clearBrowserData(context: Context) {
        try {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
        } catch (_: Exception) {}
    }

    // Stay Logged In & Credentials Management
    fun toggleStayLoggedIn(enabled: Boolean) {
        authPrefs.edit().putBoolean("stay_logged_in", enabled).apply()
        _browserState.update { it.copy(isStayLoggedInEnabled = enabled) }
        flushCookies()
    }

    fun updateCredentials(username: String, password: String) {
        authPrefs.edit()
            .putString("saved_username", username.trim())
            .putString("saved_password", password)
            .apply()
        _browserState.update {
            it.copy(
                savedUsername = username.trim(),
                savedPassword = password
            )
        }
    }

    fun toggleAutoFill(enabled: Boolean) {
        authPrefs.edit().putBoolean("auto_fill", enabled).apply()
        _browserState.update { it.copy(autoFillEnabled = enabled) }
    }

    fun flushCookies() {
        try {
            CookieManager.getInstance().flush()
        } catch (_: Exception) {}
    }
}
