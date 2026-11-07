package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portals")
data class PortalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val institution: String = "",
    val themeColorHex: Long = 0xFFF97316, // Moodle orange default
    val usernameNote: String = "",
    val passwordHint: String = "",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val sortOrder: Int = 0,
    val lastVisitedAt: Long = System.currentTimeMillis(),
    val isDesktopModeDefault: Boolean = false,
    val visitCount: Int = 0
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portalId: Long = 0L,
    val portalTitle: String = "",
    val title: String,
    val url: String,
    val isFavorite: Boolean = true,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloaded_files")
data class DownloadedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portalId: Long = 0L,
    val portalTitle: String = "",
    val fileName: String,
    val fileUri: String,
    val fileSizeBytes: Long = 0L,
    val mimeType: String = "application/pdf",
    val description: String = "",
    val courseCode: String = "",
    val downloadStatus: String = "مكتمل",
    val downloadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_notes")
data class CourseNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val portalId: Long = 0L,
    val title: String,
    val content: String,
    val dueDate: Long? = null,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class NotificationItem(
    val id: String,
    val title: String,
    val sender: String,
    val timeAgo: String,
    val isUnread: Boolean,
    val initials: String
)
