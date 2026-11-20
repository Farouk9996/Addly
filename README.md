# LearnBridge — تطبيق البوابة الجامعية الذكية (Moodle & UFC)

تطبيق أندرويد متكامل مبني بلغة **Kotlin** و **Jetpack Compose** يعمل كواجهة وسيطة ذكية ومتصفح تعليمي مدمج يشبه آلية عمل **Moodle**، مصمم خصيصاً لطلبة الجامعات الجزائرية (جامعة التكوين المتواصل **UFC S1** وكلية الحقوق والعلوم السياسية **FDSP - جامعة مستغانم**)، مع مزامنة سحابية عبر **Firebase** ومدير ملفات ومستندات.

---

## ✨ المميزات الرئيسية (Features)

- **متصفح تعليمي مدمج (Moodle WebView Integration)**:
  - اتصال مباشر ومندمج مع بوابة جامعة التكوين المتواصل `https://mast-da.ufc.dz/s1/login/index.php`.
  - اتصال مباشر مع بوابة كلية الحقوق والعلوم السياسية `https://ead.univ-mosta.dz/fdsp`.
  - شريط اختصارات حي مرتبط مباشرة بنقاط Moodle الحقيقية (مقرراتي، لوحة التحكم، كشف النقاط، المنتديات، التقويم).
- **البقاء في وضع تسجيل الدخول (Stay Logged In)**:
  - حفظ دائم لملفات تعريف الارتباط (`CookieManager.flush()`) وبيانات الجلسة (`MoodleSession`) حتى بعد إغلاق التطبيق وإعادة فتحه.
  - التحديد التلقائي لخيار **تذكر اسم المستخدم (`#rememberusername`)** وتعبئة الحقول السريعة بضغطة زر عبر `AndroidLoginBridge`.
- **مدير التحميلات والمستندات الجامعية (Download Manager)**:
  - تتبع، استيراد، معاينة، وفرز ملفات الـ PDF ومستندات Word المحملة من البوابات التعليمية للوصول إليها دون إنترنت.
- **الإشعارات والرسائل الحية**:
  - ربط كافة عناصر الإشعارات، الرسائل، والبحث بالروابط الفعلية في منصة Moodle.
- **المزامنة السحابية (Firebase Firestore & Google Sign-In)**:
  - تسجيل الدخول بحساب Google عبر Jetpack `CredentialManager` لمزامنة البوابات والمفضلات والمستندات سحابياً.
- **أدوات قراءة متقدمة**:
  - وضع سطح المكتب (Desktop Mode)، الوضع الليلي لقراءة الصفحات، التحكم بحجم الخط، وحفظ الإشارات المرجعية.

---

## 🚀 طريقة التشغيل السريع (Quick Start)

يمكن لأي شخص تشغيل المشروع مباشرة بعد تحميله أو استنساخه من GitHub عبر طريقتين:

### الطريقة 1: تحميل ملف التثبيت الجاهز (APK) عبر GitHub Actions
يحتوي المستودع على سير عمل تلقائي (`.github/workflows/android-build.yml`) يقوم ببناء ملف `app-debug.apk` تلقائياً عند كل تحديث.
1. ادخل إلى تبويب **Actions** في صفحة المستودع على GitHub.
2. اختر أحدث عملية بناء ناجحة (`Android Build & Test`).
3. حمّل الملف من قسم **Artifacts** باسم **`LearnBridge-debug-apk`** وثبته مباشرة على هاتفك الأندرويد.

### الطريقة 2: التشغيل والتطوير في Android Studio
1. **استنساخ المستودع (Clone):**
   ```bash
   git clone <رابط-المستودع-على-GitHub>
   cd LearnBridge
   ```
2. **تهيئة مفتاح التوقيع للتطوير (خطوة واحدة مطلوبة قبل أول تشغيل):**
   لأن ملف `debug.keystore` مستبعد من Git للأمان، قم بفك تشفيره من الملف المرفق `debug.keystore.base64` في المجلد الرئيسي للمشروع:
   - **على Linux / macOS:**
     ```bash
     base64 -d debug.keystore.base64 > debug.keystore
     ```
   - **على Windows (PowerShell):**
     ```powershell
     [IO.File]::WriteAllBytes("debug.keystore", [Convert]::FromBase64String((Get-Content debug.keystore.base64 -Raw)))
     ```
3. **الفتح والتشغيل:**
   - افتح مجلد المشروع في **Android Studio** (إصدار حديث يدعم AGP 9.1+ و JDK 17).
   - انتظر انتهاء **Gradle Sync** ثم اضغط على **Run (▶)** لتشغيل التطبيق على المحاكي أو الهاتف.

---

## 🛠️ التقنيات المستخدمة (Tech Stack)

| التقنية | الاستخدام |
| :--- | :--- |
| **Kotlin & Jetpack Compose** | بناء واجهات Material Design 3 متجاوبة |
| **Android WebView + JS Bridge** | عرض البوابات التعليمية وحفظ الجلسات والملء التلقائي |
| **Room Database + KSP** | قاعدة البيانات المحلية للبوابات، المفضلات، والتحميلات |
| **Firebase Auth & Firestore** | تسجيل الدخول عبر Google والمزامنة السحابية |
| **Credential Manager** | المصادقة الحديثة لحسابات Google |
| **Robolectric & JUnit4** | اختبارات الوحدات المحلية |

---

## 📁 هيكل المشروع (Project Structure)

```text
├── app/
│   ├── google-services.json          # إعدادات مشروع Firebase
│   └── src/main/java/com/example/
│       ├── MainActivity.kt           # نقطة الدخول وشريط التنقل الرئيسي
│       ├── data/                     # Room DAO, Entities, Repository, FirestoreSyncService
│       └── ui/
│           ├── PortalViewModel.kt    # إدارة الحالة والمنطق
│           ├── screens/              # الشاشات (Dashboard, Browser, Downloads, Messages, Notifications, More)
│           └── components/           # النوافذ المنبثقة (AccountSheet, StayLoggedInDialog, DocumentViewer)
├── .github/workflows/android-build.yml  # البناء التلقائي للـ APK على GitHub
├── firestore.rules                   # قواعد الأمان لقاعدة بيانات Firestore
└── gradle/libs.versions.toml         # إدارة المكتبات والإصدارات
```

---

## ⚙️ إعداد Firebase الخاص بك (اختياري)

المشروع مهيأ للعمل مباشرة، وإذا أردت ربطه بمشروع Firebase مستقل خاص بك:
1. أنشئ تطبيق Android جديد في [Firebase Console](https://console.firebase.google.com/) بمعرف الحزمة `com.aistudio.moodleportal.kvyptx`.
2. فعّل **Firestore Database** و **Authentication (Google Sign-In)**.
3. استبدل ملف `app/google-services.json` بالملف الخاص بمشروعك، وانشر قواعد `firestore.rules`.

---

## English Summary

**LearnBridge** is an Android Jetpack Compose Moodle companion & academic portal browser designed for Algerian universities (UFC S1 & FDSP Mostaganem). It features persistent WebView login sessions (`CookieManager` + auto-remember/autofill), a built-in Document Download Manager, live Moodle deep-links, and Firebase Firestore cloud sync via Google Sign-In.

### Instant Build & Run
1. Decode `debug.keystore.base64` into `debug.keystore` in the root folder (see commands above).
2. Open the project in Android Studio (JDK 17+, Gradle 9.3.1) and press **Run**.
3. Or download the auto-built APK directly from the repository's **GitHub Actions** artifacts.
