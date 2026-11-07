package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookmarkEntity
import com.example.data.model.CourseNoteEntity
import com.example.data.model.DownloadedFileEntity
import com.example.data.model.PortalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PortalDao {
    @Query("SELECT * FROM portals ORDER BY isPinned DESC, sortOrder ASC, id ASC")
    fun getAllPortals(): Flow<List<PortalEntity>>

    @Query("SELECT * FROM portals WHERE id = :id LIMIT 1")
    fun getPortalById(id: Long): Flow<PortalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortal(portal: PortalEntity): Long

    @Update
    suspend fun updatePortal(portal: PortalEntity)

    @Update
    suspend fun updatePortals(portals: List<PortalEntity>)

    @Query("UPDATE portals SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updatePortalSortOrder(id: Long, sortOrder: Int)

    @Query("UPDATE portals SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updatePortalFavorite(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun deletePortal(portal: PortalEntity)

    @Query("UPDATE portals SET lastVisitedAt = :timestamp, visitCount = visitCount + 1 WHERE id = :id")
    suspend fun recordVisit(id: Long, timestamp: Long)

    // Bookmarks
    @Query("SELECT * FROM bookmarks WHERE portalId = :portalId ORDER BY sortOrder ASC, createdAt DESC")
    fun getBookmarksForPortal(portalId: Long): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks ORDER BY sortOrder ASC, createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Update
    suspend fun updateBookmark(bookmark: BookmarkEntity)

    @Update
    suspend fun updateBookmarks(bookmarks: List<BookmarkEntity>)

    @Query("UPDATE bookmarks SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateBookmarkSortOrder(id: Long, sortOrder: Int)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    // Downloads
    @Query("SELECT * FROM downloaded_files ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadedFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(file: DownloadedFileEntity): Long

    @Delete
    suspend fun deleteDownload(file: DownloadedFileEntity)

    // Notes & Tasks
    @Query("SELECT * FROM course_notes WHERE portalId = :portalId ORDER BY isDone ASC, dueDate ASC, createdAt DESC")
    fun getNotesForPortal(portalId: Long): Flow<List<CourseNoteEntity>>

    @Query("SELECT * FROM course_notes ORDER BY isDone ASC, dueDate ASC, createdAt DESC")
    fun getAllNotes(): Flow<List<CourseNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CourseNoteEntity): Long

    @Update
    suspend fun updateNote(note: CourseNoteEntity)

    @Delete
    suspend fun deleteNote(note: CourseNoteEntity)
}
