# EzWordMaster
![EzWordMaster Logo](app/src/main/res/drawable/logo.png)

Ứng dụng học từ vựng trên thiết bị di động cho phép bạn tạo và quản lý chủ đề (thư mục) từ vựng, thêm từ mới kèm nghĩa/ví dụ, và ôn tập bằng nhiều chế độ trực quan (flashcard, lật thẻ, quiz). Ứng dụng cũng ghi lại lịch sử học tập để theo dõi tiến độ.

## Tính năng chính
- **Quản lý chủ đề**
    - Tạo, xem danh sách, chỉnh sửa, xóa chủ đề
    - Quản lý từ vựng trong từng chủ đề (thêm, sửa, xóa)
- **Ôn tập**
    - **Flashcard**: đánh dấu từ đã biết/đang học, xem kết quả tổng hợp
    - **Lật thẻ**: chọn tập từ, chơi lật thẻ ghép từ-nghĩa, xem kết quả ghép đúng
    - **Quiz**: True/False, Essay, Multiple Choice; có màn hình thiết lập trước khi làm
- **Lịch sử học tập**
    - Xem lại điểm/tiến độ theo thời gian
    - Theo dõi kết quả theo từng chế độ học (flashcard, quiz, flip card)
- **Cài đặt**
    - Bật/tắt nhắc nhở học tập; chọn chu kỳ nhắc (giờ)
- **Màn hình hỗ trợ**
    - Màn hình giới thiệu (Intro)
    - Màn hình trợ giúp (Help)
    - Màn hình thông tin (About)
    - Màn hình thông báo (Notification)

**Lưu ý**: Nhắc nhở/Thông báo phụ thuộc quyền POST_NOTIFICATIONS (Android 13+) và lập lịch qua WorkManager. Một số thiết bị/thiết lập tiết kiệm pin có thể trì hoãn job nền.

## Kiến trúc & Công nghệ
- **UI Framework**: Kotlin, Jetpack Compose (Material 3), Navigation Compose
- **Architecture**: MVVM (ViewModel) với Repository Pattern
- **Data Storage**:
    - Room Database (SQLite) - lưu trữ topics, words, study results
    - DataStore Preferences - lưu cài đặt thông báo
    - Tự động migrate từ JSON sang Room Database khi khởi động lần đầu
- **Concurrency**: Kotlin Coroutines, Flow
- **Serialization**: kotlinx-serialization (JSON) - hỗ trợ migrate dữ liệu
- **Background Work**: WorkManager (lập lịch nhắc nhở định kỳ)
- **Image Loading**: Coil (hỗ trợ GIF)
- **UI Effects**: Konfetti Compose (hiệu ứng pháo bông)
- **Network**: Retrofit + Gson converter (đã khai báo, sẵn sàng tích hợp API dịch từ vựng)
- **Database Tool**: KSP (Kotlin Symbol Processing) thay vì KAPT
- **Cấu hình Android**: compileSdk 34, targetSdk 34, minSdk 24, JDK 17

## Cấu trúc thư mục (rút gọn)
- `app/src/main/java/com/example/ezwordmaster/`
    - `MainActivity.kt`: Entry point, xin quyền notification, khởi tạo `AppContainer` và `ViewModelFactory`
    - `utils/NotificationSettings.kt`: Application class tạo `NotificationChannel`
    - `ui/AppContainer.kt`: Dependency injection container, cung cấp repository cho ViewModel
    - `ui/ViewModelFactory.kt`: Factory khởi tạo ViewModel theo repository từ AppContainer
    - `ui/navigation/AppNavHost.kt`: Định nghĩa các tuyến màn hình (start destination: `home/MANAGEMENT` với tab mặc định là Quản lý)
    - `ui/screens/`: Các màn hình UI
        - `MainHomeScreen.kt`: Màn hình chính với 3 tab (Quản lý, Ôn tập, Cài đặt)
        - `IntroScreen.kt`: Màn hình giới thiệu
        - `about/AboutScreen.kt`: Màn hình thông tin về app
        - `help/HelpScreen.kt`: Màn hình trợ giúp
        - `notification/NotificationScreen.kt`: Màn hình quản lý thông báo
        - `topic_managment/`: Quản lý chủ đề (danh sách, chỉnh sửa)
        - `regime/`: Các màn hình ôn tập
            - `practice/flash/`: Flashcard
            - `practice/quiz/`: Quiz (True/False, Essay, Multiple Choice)
            - `entertainment/`: Lật thẻ (Flip Card)
        - `history/StudyHistoryScreen.kt`: Lịch sử học tập
        - `settings/SettingsScreen.kt`: Cài đặt
    - `data/`: Layer xử lý dữ liệu
        - `local/database/`: Room Database (EzWordMasterDatabase)
        - `local/dao/`: DAO interfaces (TopicDao, WordDao, StudyResultDao)
        - `local/entity/`: Room entities (TopicEntity, WordEntity, StudyResultEntity)
        - `local/mapper/`: Mapper chuyển đổi Entity ↔ Domain Model
        - `local/repository/`: Repository implementations (TopicRepositoryImpl, StudyResultRepositoryImpl, SettingsRepositoryImpl)
        - `local/SettingsDataStore.kt`: DataStore lưu cài đặt thông báo
        - `remote/DictionaryApi.kt`: Retrofit interface (sẵn sàng tích hợp API)
        - `worker/`: NotificationWorker, NotificationScheduler
    - `domain/repository/`: Repository interfaces (ITopicRepository, IStudyResultRepository, ISettingsRepository)
    - `model/`: Domain models và UI models (Topic, Word, StudyResult, etc.)
- `app/src/main/res/`: Tài nguyên giao diện (drawable, layout, values, xml, raw)
- `app/src/main/AndroidManifest.xml`: Quyền, `application`, `activity`
- `app/build.gradle.kts`: Cấu hình module
- `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`: Cấu hình dự án

## Yêu cầu
- Android Studio (phiên bản mới, hỗ trợ Kotlin 1.9.23+)
- JDK 17
- Android SDK 34 (minSdk 24)

## Cách chạy
1. Mở dự án bằng Android Studio và đồng bộ Gradle.
2. Chọn module `app`, chọn thiết bị (emulator/physical).
3. Run project (nút Run trên toolbar).

### Build APK nhanh (tùy chọn)
- Windows PowerShell tại thư mục gốc dự án:
```powershell
.\gradlew.bat :app:assembleDebug
```
- APK đầu ra: `app/build/outputs/apk/debug/app-debug.apk`

### Kiểm thử (nếu cần)
```powershell
# Unit test JVM
.\gradlew.bat testDebugUnitTest

# Instrumentation test trên thiết bị/emulator
.\gradlew.bat connectedDebugAndroidTest
```

## Quyền & Thông báo
- **Quyền**:
    - `INTERNET`: Kết nối mạng (sẵn sàng cho API dịch từ vựng)
    - `POST_NOTIFICATIONS`: Thông báo (Android 13+ yêu cầu runtime permission)
- `MainActivity` sẽ tự động xin quyền thông báo với Android 13+
- Nhắc nhở sử dụng WorkManager với periodic work; người dùng có thể bật/tắt và điều chỉnh chu kỳ (mặc định 4 giờ) trong màn hình Cài đặt

## Database & Migration
- **Room Database**: Sử dụng Room để lưu trữ dữ liệu topics, words, và study results
- **Auto Migration**: Tự động migrate dữ liệu từ file JSON cũ (`topics.json`) sang Room Database khi:
    - Database rỗng (lần đầu chạy app)
    - File JSON cũ tồn tại trong `filesDir`
- **Fallback**: Nếu không có file JSON cũ, app sẽ tạo dữ liệu mặc định (chủ đề "Chào mừng đến với EzWordMaster")
- **Schema**: Database version 1, sử dụng `fallbackToDestructiveMigration()` (có thể thêm migration strategy sau)

## Thông tin thêm
- [Danh sách thành viên](https://docs.google.com/spreadsheets/d/14NwdO01yHIBayl9eDs9CH-itm1TKDcyRPgflDGvMZtg/edit?usp=sharing)
- [Báo cáo](https://docs.google.com/document/d/17lVkj18YpAYfzYJi3OHWqpUKfqfTEoviIN8zyFwSTqg/edit?usp=sharing)
- [Design](https://www.figma.com/design/A6rw39IXQP0aWaGc4lGeKg/EzWordMaster?node-id=0-1&t=S1B6jdlF2ENFs4sG-1)