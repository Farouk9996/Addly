package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.example.data.model.PortalEntity
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.BrowserUiState
import com.example.ui.PortalViewModel
import com.example.ui.components.StayLoggedInDialog

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebBrowserScreen(
    browserState: BrowserUiState,
    viewModel: PortalViewModel,
    onCloseBrowser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showStayLoggedInDialog by remember { mutableStateOf(false) }

    // File upload callback for homework submission
    var uploadMessageCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uploadMessageCallback?.onReceiveValue(uris.toTypedArray())
        uploadMessageCallback = null
    }

    // Hardware back button navigation in WebView
    BackHandler(enabled = true) {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onCloseBrowser()
        }
    }

    // Effect to toggle desktop user agent
    LaunchedEffect(browserState.isDesktopMode) {
        webViewInstance?.let { webView ->
            val settings = webView.settings
            if (browserState.isDesktopMode) {
                settings.userAgentString = DESKTOP_USER_AGENT
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
            } else {
                settings.userAgentString = null // Reset to default mobile
                settings.useWideViewPort = true
            }
            webView.reload()
        }
    }

    // Effect for text zoom
    LaunchedEffect(browserState.textZoom) {
        webViewInstance?.settings?.textZoom = browserState.textZoom
    }

    // Effect for dark mode CSS inverter
    LaunchedEffect(browserState.isDarkModeInverted) {
        webViewInstance?.let { webView ->
            val js = if (browserState.isDarkModeInverted) {
                """
                (function() {
                    var style = document.getElementById('dark-invert-style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = 'dark-invert-style';
                        style.innerHTML = 'html { filter: invert(0.9) hue-rotate(180deg) !important; background-color: #121212 !important; } img, video, picture, iframe, canvas { filter: invert(1) hue-rotate(180deg) !important; }';
                        document.head.appendChild(style);
                    }
                })();
                """.trimIndent()
            } else {
                """
                (function() {
                    var style = document.getElementById('dark-invert-style');
                    if (style) { style.parentNode.removeChild(style); }
                })();
                """.trimIndent()
            }
            webView.evaluateJavascript(js, null)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top Browser Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (browserState.isSecure) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "آمن",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = browserState.pageTitle.ifBlank { browserState.activePortal?.title ?: "المتصفح الوسيط" },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = browserState.currentUrl.removePrefix("https://").removePrefix("http://"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onCloseBrowser,
                            modifier = Modifier.testTag("browser_close_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق والعودة للبوابات")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { webViewInstance?.reload() },
                            modifier = Modifier.testTag("browser_reload_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "إعادة تحميل")
                        }

                        IconButton(
                            onClick = {
                                viewModel.bookmarkCurrentPage()
                                Toast.makeText(context, "تم حفظ الصفحة في الإشارات المرجعية", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("browser_bookmark_btn")
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = "حفظ الصفحة")
                        }

                        Box {
                            IconButton(
                                onClick = { showMoreMenu = true },
                                modifier = Modifier.testTag("browser_more_btn")
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "المزيد")
                            }

                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(if (browserState.isDesktopMode) "التحويل لوضع الهاتف" else "طلب موقع سطح المكتب")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (browserState.isDesktopMode) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.toggleDesktopMode()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(if (browserState.isDarkModeInverted) "إلغاء الوضع الليلي" else "تفعيل الوضع الليلي للقراءة")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            if (browserState.isDarkModeInverted) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.toggleDarkModeInvert()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("مشاركة الرابط") },
                                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, browserState.currentUrl)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الرابط"))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("نسخ الرابط") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        clipboardManager.setText(AnnotatedString(browserState.currentUrl))
                                        Toast.makeText(context, "تم نسخ الرابط للحافظة", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("إضافة للمفضلة") },
                                    leadingIcon = { Icon(Icons.Default.BookmarkAdd, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.bookmarkCurrentPage()
                                        Toast.makeText(context, "تم حفظ الصفحة في المفضلة", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("إعدادات البقاء قيد تسجيل الدخول") },
                                    leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        showStayLoggedInDialog = true
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("مسح بيانات التصفح والكوكيز") },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.clearBrowserData(context)
                                        Toast.makeText(context, "تم مسح ملفات الكوكيز والذاكرة المؤقتة", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Animated Web Loading Progress Bar
                if (browserState.isLoading) {
                    LinearProgressIndicator(
                        progress = { browserState.progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = Color.Transparent
                    )
                }

                // Moodle Smart Shortcuts Quick Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { viewModel.navigateToMoodleShortcut("/my/") },
                        label = { Text("لوحة التحكم") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = { viewModel.navigateToMoodleShortcut("/my/courses.php") },
                        label = { Text("مقرراتي") }
                    )
                    AssistChip(
                        onClick = { viewModel.navigateToMoodleShortcut("/calendar/view.php") },
                        label = { Text("التقويم") }
                    )
                    AssistChip(
                        onClick = { viewModel.navigateToMoodleShortcut("/grade/report/overview/index.php") },
                        label = { Text("الدرجات") }
                    )
                    AssistChip(
                        onClick = { viewModel.navigateToMoodleShortcut("/message/index.php") },
                        label = { Text("الرسائل") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.setTextZoom(browserState.textZoom - 15) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = "تصغير النص", modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "${browserState.textZoom}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                    IconButton(
                        onClick = { viewModel.setTextZoom(browserState.textZoom + 15) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.ZoomIn, contentDescription = "تكبير النص", modifier = Modifier.size(18.dp))
                    }
                }

                // Stay Logged In & Quick Login Bar
                val effectiveUser = browserState.savedUsername.ifBlank { browserState.activePortal?.usernameNote ?: "ZIANE Mohammed" }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF222222))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (browserState.isStayLoggedInEnabled) Color(0xFF22C55E).copy(alpha = 0.2f) else Color(0xFF374151))
                            .clickable { showStayLoggedInDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("stay_logged_in_chip")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockClock,
                            contentDescription = null,
                            tint = if (browserState.isStayLoggedInEnabled) Color(0xFF4ADE80) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (browserState.isStayLoggedInEnabled) "البقاء متصلاً: مفعل ✓" else "البقاء متصلاً: معطل",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (browserState.isStayLoggedInEnabled) Color(0xFF4ADE80) else Color(0xFF9CA3AF),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF97316).copy(alpha = 0.2f))
                            .clickable {
                                val uName = effectiveUser
                                val pWord = browserState.savedPassword
                                val js = """
                                (function() {
                                    var rem = document.querySelector('input[name="rememberusername"], #rememberusername');
                                    if (rem) { rem.checked = true; }
                                    var u = document.querySelector('input[name="username"], input[id="username"], input[name="login"], input[id="login"], input[name="login_username"], input[type="text"]');
                                    if (u) {
                                        u.value = '$uName';
                                        u.dispatchEvent(new Event('input', { bubbles: true }));
                                        u.dispatchEvent(new Event('change', { bubbles: true }));
                                    }
                                    if ('$pWord'.length > 0) {
                                        var p = document.querySelector('input[name="password"], input[id="password"]');
                                        if (p) {
                                            p.value = '$pWord';
                                            p.dispatchEvent(new Event('input', { bubbles: true }));
                                            p.dispatchEvent(new Event('change', { bubbles: true }));
                                        }
                                    }
                                })();
                                """.trimIndent()
                                webViewInstance?.evaluateJavascript(js, null)
                                viewModel.flushCookies()
                                Toast.makeText(context, "تم تعبئة حساب $uName وتثبيت الجلسة", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("autofill_login_btn")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(16.dp))
                        Text(
                            text = "تعبئة سريعة: $effectiveUser",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFED7AA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // The Central WebView Container
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("browser_webview"),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            allowFileAccess = true
                            allowContentAccess = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            mediaPlaybackRequiresUserGesture = false
                        }

                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this@apply, true)

                        addJavascriptInterface(object {
                            @android.webkit.JavascriptInterface
                            fun onCredentialsSubmitted(username: String, password: String) {
                                if (username.isNotBlank()) {
                                    viewModel.updateCredentials(username, password)
                                    viewModel.flushCookies()
                                }
                            }
                        }, "AndroidLoginBridge")

                        webViewClient = object : WebViewClient() {
                            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                                try {
                                    val parent = view?.parent as? android.view.ViewGroup
                                    parent?.removeView(view)
                                    view?.destroy()
                                } catch (_: Exception) {}
                                webViewInstance = null
                                viewModel.updateWebProgress(0, "تم استرداد المتصفح بنجاح", browserState.currentUrl)
                                return true
                            }

                            @SuppressLint("WebViewClientOnReceivedSslError")
                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                handler?.proceed()
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                viewModel.updateWebProgress(20, view?.title, url)
                                viewModel.updateNavState(view?.canGoBack() == true, view?.canGoForward() == true)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                viewModel.updateWebProgress(100, view?.title, url)
                                viewModel.updateNavState(view?.canGoBack() == true, view?.canGoForward() == true)

                                // Re-inject dark mode if enabled
                                if (browserState.isDarkModeInverted) {
                                    val js = """
                                    (function() {
                                        var style = document.getElementById('dark-invert-style');
                                        if (!style) {
                                            style = document.createElement('style');
                                            style.id = 'dark-invert-style';
                                            style.innerHTML = 'html { filter: invert(0.9) hue-rotate(180deg) !important; background-color: #121212 !important; } img, video, picture, iframe, canvas { filter: invert(1) hue-rotate(180deg) !important; }';
                                            document.head.appendChild(style);
                                        }
                                    })();
                                    """.trimIndent()
                                    view?.evaluateJavascript(js, null)
                                }

                                // Flush cookies to persistent disk storage
                                viewModel.flushCookies()

                                // Auto remember me and auto-fill if stay logged in is enabled
                                if (browserState.isStayLoggedInEnabled) {
                                    val uName = browserState.savedUsername.ifBlank { browserState.activePortal?.usernameNote ?: "" }
                                    val pWord = browserState.savedPassword
                                    val autoFill = browserState.autoFillEnabled
                                    val jsLogin = """
                                    (function() {
                                        var rem = document.querySelector('input[name="rememberusername"], #rememberusername');
                                        if (rem) {
                                            rem.checked = true;
                                            rem.dispatchEvent(new Event('change', { bubbles: true }));
                                        }
                                        if ($autoFill && '$uName'.length > 0) {
                                            var u = document.querySelector('input[name="username"], input[id="username"], input[name="login"], input[id="login"], input[name="login_username"]');
                                            if (u && !u.value) {
                                                u.value = '$uName';
                                                u.dispatchEvent(new Event('input', { bubbles: true }));
                                                u.dispatchEvent(new Event('change', { bubbles: true }));
                                            }
                                        }
                                        if ($autoFill && '$pWord'.length > 0) {
                                            var p = document.querySelector('input[name="password"], input[id="password"]');
                                            if (p && !p.value) {
                                                p.value = '$pWord';
                                                p.dispatchEvent(new Event('input', { bubbles: true }));
                                                p.dispatchEvent(new Event('change', { bubbles: true }));
                                            }
                                        }
                                        var form = document.querySelector('form#login, form[action*="login"]');
                                        if (form && !form.hasAttribute('data-persisted')) {
                                            form.setAttribute('data-persisted', 'true');
                                            form.addEventListener('submit', function() {
                                                var r = document.querySelector('input[name="rememberusername"], #rememberusername');
                                                if (r) r.checked = true;
                                                var uIn = document.querySelector('input[name="username"], input[id="username"], input[name="login"]');
                                                var pIn = document.querySelector('input[name="password"], input[id="password"]');
                                                if (uIn && window.AndroidLoginBridge) {
                                                    window.AndroidLoginBridge.onCredentialsSubmitted(uIn.value || '', pIn ? pIn.value || '' : '');
                                                }
                                            });
                                        }
                                    })();
                                    """.trimIndent()
                                    view?.evaluateJavascript(jsLogin, null)
                                }
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val targetUri = request?.url ?: return false
                                val scheme = targetUri.scheme ?: ""
                                if (scheme != "http" && scheme != "https") {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, targetUri)
                                        ctx.startActivity(intent)
                                        return true
                                    } catch (_: Exception) {
                                        return true
                                    }
                                }
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                viewModel.updateWebProgress(newProgress, view?.title, view?.url)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                viewModel.updateWebProgress(browserState.progress, title, view?.url)
                            }

                            // Homework and assignment upload support
                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                uploadMessageCallback?.onReceiveValue(null)
                                uploadMessageCallback = filePathCallback
                                filePickerLauncher.launch(arrayOf("*/*"))
                                return true
                            }
                        }

                        // Lecture materials and syllabus download handler
                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            val guessedFilename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                            viewModel.registerDownload(
                                fileName = guessedFilename,
                                fileUri = url,
                                fileSizeBytes = contentLength,
                                mimeType = mimetype ?: "application/octet-stream"
                            )
                            Toast.makeText(
                                ctx,
                                "تم رصد تحميل ملف المقرر: $guessedFilename",
                                Toast.LENGTH_LONG
                            ).show()

                            // Hand over to system DownloadManager
                            try {
                                val request = android.app.DownloadManager.Request(Uri.parse(url)).apply {
                                    setMimeType(mimetype)
                                    addRequestHeader("User-Agent", userAgent)
                                    val cookie = CookieManager.getInstance().getCookie(url)
                                    addRequestHeader("Cookie", cookie)
                                    setDescription("جاري تحميل ملف من البوابة التعليمية...")
                                    setTitle(guessedFilename)
                                    setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    setDestinationInExternalPublicDir(
                                        android.os.Environment.DIRECTORY_DOWNLOADS,
                                        guessedFilename
                                    )
                                }
                                val dm = ctx.getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                                dm.enqueue(request)
                            } catch (e: Exception) {
                                // Fallback intent
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    ctx.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }

                        loadUrl(browserState.currentUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                    if (webView.url != browserState.currentUrl && browserState.currentUrl.isNotBlank()) {
                        webView.loadUrl(browserState.currentUrl)
                    }
                }
            )
        }

        // Bottom Browser Navigation Bar (Back, Forward, Refresh, Home)
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { webViewInstance?.goBack() },
                    enabled = browserState.canGoBack,
                    modifier = Modifier.testTag("browser_back_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "السابق")
                }

                IconButton(
                    onClick = { webViewInstance?.goForward() },
                    enabled = browserState.canGoForward,
                    modifier = Modifier.testTag("browser_forward_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "التالي")
                }

                IconButton(
                    onClick = {
                        val portal = browserState.activePortal
                        if (portal != null) {
                            viewModel.openPortal(portal)
                        } else {
                            webViewInstance?.loadUrl(browserState.currentUrl)
                        }
                    },
                    modifier = Modifier.testTag("browser_home_btn")
                ) {
                    Icon(Icons.Default.School, contentDescription = "الصفحة الرئيسية للبوابة")
                }

                IconButton(
                    onClick = onCloseBrowser,
                    modifier = Modifier.testTag("browser_exit_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "الخروج للقائمة")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.flushCookies()
            try {
                webViewInstance?.stopLoading()
                webViewInstance?.loadUrl("about:blank")
                val parent = webViewInstance?.parent as? android.view.ViewGroup
                parent?.removeView(webViewInstance)
                webViewInstance?.destroy()
            } catch (_: Exception) {}
            webViewInstance = null
        }
    }

    if (showStayLoggedInDialog) {
        StayLoggedInDialog(
            initialStayLoggedIn = browserState.isStayLoggedInEnabled,
            initialUsername = browserState.savedUsername,
            initialPassword = browserState.savedPassword,
            initialAutoFill = browserState.autoFillEnabled,
            onDismiss = { showStayLoggedInDialog = false },
            onSave = { stayLoggedIn, username, password, autoFill ->
                viewModel.toggleStayLoggedIn(stayLoggedIn)
                viewModel.updateCredentials(username, password)
                viewModel.toggleAutoFill(autoFill)
                Toast.makeText(context, "تم حفظ وتطبيق إعدادات البقاء في وضع تسجيل الدخول", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
