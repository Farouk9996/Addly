package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.model.BookmarkEntity
import com.example.data.model.DownloadedFileEntity
import com.example.data.model.PortalEntity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreSyncService(private val context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun signInWithGoogle(
        activity: Activity,
        coroutineScope: CoroutineScope,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val clientId = try {
            activity.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            onError("تعذر العثور على معرّف العميل Google Web Client ID")
            return
        }

        val credentialManager = CredentialManager.create(activity)
        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    auth.signInWithCredential(authCredential).await()
                    _syncMessage.value = "تم تسجيل الدخول بنجاح بحساب Google"
                    onSuccess()
                } else {
                    onError("نوع الاعتماد غير مدعوم")
                }
            } catch (e: GetCredentialCancellationException) {
                Log.w("FirestoreSync", "Google Sign-In cancelled: ${e.message}")
            } catch (e: Exception) {
                Log.e("FirestoreSync", "Google Sign-In error", e)
                onError(e.localizedMessage ?: "فشل تسجيل الدخول بواسطة Google")
            }
        }
    }

    fun signOut(
        coroutineScope: CoroutineScope,
        onComplete: () -> Unit = {}
    ) {
        val credentialManager = CredentialManager.create(context)
        coroutineScope.launch {
            withContext(NonCancellable) {
                try {
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: Exception) {
                    Log.w("FirestoreSync", "Failed to clear credential state", e)
                } finally {
                    auth.signOut()
                    _currentUser.value = null
                    _syncMessage.value = "تم تسجيل الخروج من سحابة Firebase"
                    onComplete()
                }
            }
        }
    }

    suspend fun syncUserData(
        stayLoggedIn: Boolean,
        portals: List<PortalEntity>,
        bookmarks: List<BookmarkEntity>,
        downloads: List<DownloadedFileEntity>
    ): Boolean = withContext(Dispatchers.IO) {
        val user = auth.currentUser ?: return@withContext false
        _isSyncing.value = true
        try {
            // 1. Sync User Profile
            db.collection("users").document(user.uid).set(
                mapOf(
                    "userId" to user.uid,
                    "displayName" to (user.displayName ?: "طالب جامعي"),
                    "email" to (user.email ?: ""),
                    "stayLoggedIn" to stayLoggedIn,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            // 2. Sync Portals
            for (portal in portals) {
                db.collection("users")
                    .document(user.uid)
                    .collection("cloud_portals")
                    .document(portal.id.toString())
                    .set(
                        mapOf(
                            "userId" to user.uid,
                            "title" to portal.title,
                            "url" to portal.url,
                            "institution" to portal.institution,
                            "isFavorite" to portal.isFavorite,
                            "sortOrder" to portal.sortOrder,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
            }

            // 3. Sync Bookmarks
            for (bookmark in bookmarks) {
                db.collection("users")
                    .document(user.uid)
                    .collection("cloud_bookmarks")
                    .document(bookmark.id.toString())
                    .set(
                        mapOf(
                            "userId" to user.uid,
                            "portalTitle" to bookmark.portalTitle,
                            "title" to bookmark.title,
                            "url" to bookmark.url,
                            "isFavorite" to bookmark.isFavorite,
                            "sortOrder" to bookmark.sortOrder,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
            }

            // 4. Sync Downloads metadata
            for (download in downloads) {
                db.collection("users")
                    .document(user.uid)
                    .collection("cloud_downloads")
                    .document(download.id.toString())
                    .set(
                        mapOf(
                            "userId" to user.uid,
                            "portalTitle" to download.portalTitle,
                            "fileName" to download.fileName,
                            "fileUri" to download.fileUri,
                            "fileSizeBytes" to download.fileSizeBytes,
                            "mimeType" to download.mimeType,
                            "courseCode" to download.courseCode,
                            "downloadStatus" to download.downloadStatus
                        )
                    ).await()
            }

            _syncMessage.value = "تمت مزامنة بياناتك مع سحابة Firebase بنجاح!"
            true
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Sync failed", e)
            _syncMessage.value = "خطأ في المزامنة السحابية: ${e.localizedMessage}"
            false
        } finally {
            _isSyncing.value = false
        }
    }
}
