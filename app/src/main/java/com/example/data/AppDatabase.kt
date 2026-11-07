package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PortalDao
import com.example.data.model.BookmarkEntity
import com.example.data.model.CourseNoteEntity
import com.example.data.model.DownloadedFileEntity
import com.example.data.model.PortalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PortalEntity::class,
        BookmarkEntity::class,
        DownloadedFileEntity::class,
        CourseNoteEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portalDao(): PortalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "learnbridge_database.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).portalDao()

                            // Requested Portal: UFC Moodle Master S1
                            val ufcPortalId = dao.insertPortal(
                                PortalEntity(
                                    title = "جامعة التكوين المتواصل - ماستر (UFC S1)",
                                    url = "https://mast-da.ufc.dz/s1/login/index.php",
                                    institution = "Université de la Formation Continue - Algérie",
                                    themeColorHex = 0xFFF97316, // Moodle Orange
                                    usernameNote = "ZIANE Mohammed",
                                    isPinned = true,
                                    isFavorite = true,
                                    sortOrder = 0
                                )
                            )

                            // Portal from the user's Moodle screenshots
                            val univMostaId = dao.insertPortal(
                                PortalEntity(
                                    title = "كلية الحقوق والعلوم السياسية (Moodle FDSP)",
                                    url = "https://ead.univ-mosta.dz/fdsp",
                                    institution = "LA FACULTE DE DROIT ET DES SCIENCES POLITIQUES",
                                    themeColorHex = 0xFFEA580C,
                                    usernameNote = "ZIANE Mohammed",
                                    isPinned = true,
                                    isFavorite = true,
                                    sortOrder = 1
                                )
                            )

                            // Official Sandbox
                            val sandboxId = dao.insertPortal(
                                PortalEntity(
                                    title = "مودل التجريبي المفتوح (Moodle Sandbox)",
                                    url = "https://sandbox.moodledemo.net",
                                    institution = "Moodle HQ Open Sandbox",
                                    themeColorHex = 0xFF2563EB,
                                    usernameNote = "admin / sandbox",
                                    isPinned = false,
                                    isFavorite = false,
                                    sortOrder = 2
                                )
                            )

                            // Edraak
                            dao.insertPortal(
                                PortalEntity(
                                    title = "منصة إدراك للتعليم المفتوح (Edraak)",
                                    url = "https://www.edraak.org",
                                    institution = "مؤسسة الملكة رانيا للتعليم",
                                    themeColorHex = 0xFF1E3A8A,
                                    isPinned = false,
                                    isFavorite = false,
                                    sortOrder = 3
                                )
                            )

                            // Khan Academy
                            dao.insertPortal(
                                PortalEntity(
                                    title = "خان أكاديمي بالعربية (Khan Academy)",
                                    url = "https://ar.khanacademy.org",
                                    institution = "Khan Academy Global",
                                    themeColorHex = 0xFF0D9488,
                                    isPinned = false,
                                    isFavorite = false,
                                    sortOrder = 4
                                )
                            )

                            // UFC Bookmarks
                            dao.insertBookmark(
                                BookmarkEntity(
                                    portalId = ufcPortalId,
                                    portalTitle = "جامعة التكوين المتواصل UFC",
                                    title = "ماستر قانون الأعمال السداسي الأول (S1)",
                                    url = "https://mast-da.ufc.dz/s1/my/",
                                    isFavorite = true,
                                    sortOrder = 0
                                )
                            )
                            dao.insertBookmark(
                                BookmarkEntity(
                                    portalId = ufcPortalId,
                                    portalTitle = "جامعة التكوين المتواصل UFC",
                                    title = "مقررات السداسي الأول والامتحانات",
                                    url = "https://mast-da.ufc.dz/s1/course/index.php",
                                    isFavorite = true,
                                    sortOrder = 1
                                )
                            )

                            // Initial Bookmarks from Moodle courses shown in screenshots
                            dao.insertBookmark(
                                BookmarkEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    title = "الذكاء الاصطناعي - المحاضرات والتقييم",
                                    url = "https://ead.univ-mosta.dz/fdsp/course/view.php?id=ai",
                                    isFavorite = true,
                                    sortOrder = 2
                                )
                            )
                            dao.insertBookmark(
                                BookmarkEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    title = "البرمجيات الحرة والمصادر المفتوحة",
                                    url = "https://ead.univ-mosta.dz/fdsp/course/view.php?id=oss",
                                    isFavorite = true,
                                    sortOrder = 3
                                )
                            )
                            dao.insertBookmark(
                                BookmarkEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    title = "الاجتهاد القضائي في مسائل الأحوال الشخصية",
                                    url = "https://ead.univ-mosta.dz/fdsp/course/view.php?id=juris",
                                    isFavorite = true,
                                    sortOrder = 4
                                )
                            )

                            // Preload Downloaded University Documents & PDFs
                            dao.insertDownload(
                                DownloadedFileEntity(
                                    portalId = ufcPortalId,
                                    portalTitle = "جامعة التكوين المتواصل UFC S1",
                                    fileName = "مطبوعة قانون الأعمال وحوكمة الشركات - ماستر UFC.pdf",
                                    fileUri = "https://mast-da.ufc.dz/s1/files/business_law_s1.pdf",
                                    fileSizeBytes = 3450120L,
                                    mimeType = "application/pdf",
                                    description = "المطبوعة الرسمية المعتمدة لطلبة الماستر عن بعد في قانون الأعمال وحوكمة الشركات",
                                    courseCode = "قانون الأعمال S1",
                                    downloadStatus = "مكتمل",
                                    downloadedAt = System.currentTimeMillis() - 86400000L * 1
                                )
                            )
                            dao.insertDownload(
                                DownloadedFileEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    fileName = "مطبوعة منهجية البحث العلمي 1.pdf",
                                    fileUri = "https://ead.univ-mosta.dz/fdsp/files/methodology_part1.pdf",
                                    fileSizeBytes = 2569011L,
                                    mimeType = "application/pdf",
                                    description = "المطبوعة الرسمية المعتمدة في منهجية البحث القانوني للدكتورة عائشة دويدي",
                                    courseCode = "منهجية البحث العلمي",
                                    downloadStatus = "مكتمل",
                                    downloadedAt = System.currentTimeMillis() - 86400000L * 2
                                )
                            )
                            dao.insertDownload(
                                DownloadedFileEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    fileName = "محاضرات الذكاء الاصطناعي وتطبيقاته القانونية.pdf",
                                    fileUri = "https://ead.univ-mosta.dz/fdsp/files/ai_law_lecture.pdf",
                                    fileSizeBytes = 4325376L,
                                    mimeType = "application/pdf",
                                    description = "مجموعة محاضرات السداسي الثاني حول الذكاء الاصطناعي والمسؤولية المدنية",
                                    courseCode = "الذكاء الاصطناعي",
                                    downloadStatus = "مكتمل",
                                    downloadedAt = System.currentTimeMillis() - 86400000L * 4
                                )
                            )
                            dao.insertDownload(
                                DownloadedFileEntity(
                                    portalId = univMostaId,
                                    portalTitle = "كلية الحقوق والعلوم السياسية",
                                    fileName = "ملخص الاجتهاد القضائي في مسائل الأحوال الشخصية.docx",
                                    fileUri = "https://ead.univ-mosta.dz/fdsp/files/jurisprudence_summary.docx",
                                    fileSizeBytes = 891289L,
                                    mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                    description = "ملخص لأبرز القرارات القضائية الصادرة عن المحكمة العليا والغرف المدنية",
                                    courseCode = "الأحوال الشخصية",
                                    downloadStatus = "مكتمل",
                                    downloadedAt = System.currentTimeMillis() - 86400000L * 7
                                )
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
