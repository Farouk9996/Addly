package com.example.data

import com.example.data.dao.PortalDao
import com.example.data.model.BookmarkEntity
import com.example.data.model.CourseNoteEntity
import com.example.data.model.DownloadedFileEntity
import com.example.data.model.PortalEntity
import kotlinx.coroutines.flow.Flow

class PortalRepository(private val portalDao: PortalDao) {
    val allPortals: Flow<List<PortalEntity>> = portalDao.getAllPortals()
    val allBookmarks: Flow<List<BookmarkEntity>> = portalDao.getAllBookmarks()
    val allDownloads: Flow<List<DownloadedFileEntity>> = portalDao.getAllDownloads()
    val allNotes: Flow<List<CourseNoteEntity>> = portalDao.getAllNotes()

    fun getPortalById(id: Long): Flow<PortalEntity?> = portalDao.getPortalById(id)
    fun getBookmarks(portalId: Long): Flow<List<BookmarkEntity>> = portalDao.getBookmarksForPortal(portalId)
    fun getNotes(portalId: Long): Flow<List<CourseNoteEntity>> = portalDao.getNotesForPortal(portalId)

    suspend fun savePortal(portal: PortalEntity): Long {
        return if (portal.id == 0L) {
            portalDao.insertPortal(portal)
        } else {
            portalDao.updatePortal(portal)
            portal.id
        }
    }

    suspend fun deletePortal(portal: PortalEntity) = portalDao.deletePortal(portal)

    suspend fun togglePortalFavorite(portalId: Long, isFavorite: Boolean) {
        portalDao.updatePortalFavorite(portalId, isFavorite)
    }

    suspend fun reorderPortals(orderedList: List<PortalEntity>) {
        val updated = orderedList.mapIndexed { index, entity ->
            entity.copy(sortOrder = index)
        }
        portalDao.updatePortals(updated)
    }

    suspend fun recordVisit(portalId: Long) {
        portalDao.recordVisit(portalId, System.currentTimeMillis())
    }

    // Bookmarks
    suspend fun saveBookmark(bookmark: BookmarkEntity): Long {
        return if (bookmark.id == 0L) {
            portalDao.insertBookmark(bookmark)
        } else {
            portalDao.updateBookmark(bookmark)
            bookmark.id
        }
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) = portalDao.deleteBookmark(bookmark)

    suspend fun reorderBookmarks(orderedList: List<BookmarkEntity>) {
        val updated = orderedList.mapIndexed { index, entity ->
            entity.copy(sortOrder = index)
        }
        portalDao.updateBookmarks(updated)
    }

    // Downloads
    suspend fun saveDownload(file: DownloadedFileEntity) = portalDao.insertDownload(file)
    suspend fun deleteDownload(file: DownloadedFileEntity) = portalDao.deleteDownload(file)

    // Notes
    suspend fun saveNote(note: CourseNoteEntity): Long {
        return if (note.id == 0L) {
            portalDao.insertNote(note)
        } else {
            portalDao.updateNote(note)
            note.id
        }
    }

    suspend fun toggleNoteStatus(note: CourseNoteEntity) {
        portalDao.updateNote(note.copy(isDone = !note.isDone))
    }

    suspend fun deleteNote(note: CourseNoteEntity) = portalDao.deleteNote(note)
}
