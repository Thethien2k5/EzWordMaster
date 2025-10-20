package com.example.ezwordmaster.domain.repository

import android.content.Context
import android.util.Log
import com.example.ezwordmaster.domain.model.Topic
import com.example.ezwordmaster.domain.model.Word
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

class TopicRepository(private val context: Context) {

    private val FILE_NAME = "topics.json"
    private val json = Json { prettyPrint = true }

    // Đường dẫn tới file topics.json trong thư mục riêng của app
    private fun getTopicsFile(): File = File(context.filesDir, FILE_NAME)

    // Kiểm tra file có tồn tại không
    fun isTopicsFileExists(): Boolean {
        val exists = getTopicsFile().exists()
        Log.d("TopicRepo", "File tồn tại: $exists")
        return exists
    }
    // Đọc dữ liệu từ file
    fun loadTopics(): List<Topic> {
        createTopicsFileIfMissing()
        val file = getTopicsFile()

        return try {
            val jsonString = file.readText()
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Log.e("TopicRepo", "Lỗi đọc file: ${e.message}")
            emptyList()
        }
    }
    // Ghi đè toàn bộ danh sách (chỉ dùng nội bộ)
    private fun saveTopics(topics: List<Topic>) {
        try {
            val jsonString = json.encodeToString(topics)
            getTopicsFile().writeText(jsonString)
            Log.d("TopicRepo", "Đã lưu ${topics.size} topics vào file.")
        } catch (e: Exception) {
            Log.e("TopicRepo", " Lỗi khi ghi file: ${e.message}")
        }
    }
    //***** ====== TẠO ============ ********
    //  Tạo file mặc định nếu chưa có
    fun createTopicsFileIfMissing() {
        val file = getTopicsFile()
        if (!file.exists()) {
            val defaultTopics = listOf(
                Topic(
                    id = "1",
                    name = "Learning environment",
                    words = listOf(
                        Word("Student", "Học sinh"),
                        Word("Teacher", "Giáo viên"),
                        Word("Classroom", "Lớp học"),
                        Word("School", "Trường học"),
                        Word("Homework", "Bài tập về nhà"),
                        Word("Exam", "Kỳ thi"),
                        Word("Test", "Bài kiểm tra"),
                        Word("Grade", "Điểm số"),
                        Word("Subject", "Môn học"),
                        Word("Lesson", "Bài học"),
                        Word("Book", "Sách"),
                        Word("Notebook", "Vở ghi"),
                        Word("Pen", "Bút mực"),
                        Word("Pencil", "Bút chì"),
                        Word("Eraser", "Cục tẩy"),
                        Word("Ruler", "Thước kẻ"),
                        Word("Bag", "Cặp sách"),
                        Word("Uniform", "Đồng phục"),
                        Word("Break", "Giờ giải lao"),
                        Word("Lunch", "Bữa trưa"),
                        Word("Library", "Thư viện"),
                        Word("Laboratory", "Phòng thí nghiệm"),
                        Word("Playground", "Sân chơi"),
                        Word("Friend", "Bạn bè"),
                        Word("Classmate", "Bạn cùng lớp"),
                        Word("Principal", "Hiệu trưởng"),
                        Word("Study", "Học tập"),
                        Word("Learn", "Học hỏi"),
                        Word("Teach", "Dạy"),
                        Word("Read", "Đọc"),
                        Word("Write", "Viết"),
                        Word("Calculate", "Tính toán"),
                        Word("Remember", "Ghi nhớ"),
                        Word("Understand", "Hiểu"),
                        Word("Practice", "Luyện tập"),
                        Word("Project", "Dự án"),
                        Word("Presentation", "Bài thuyết trình"),
                        Word("Group", "Nhóm"),
                        Word("Teamwork", "Làm việc nhóm"),
                        Word("Knowledge", "Kiến thức")
                    )
                ),
                Topic(
                    id = "2",
                    name = "Food & Drinks",
                    words = listOf(
                        Word("Apple", "Táo"),
                        Word("Banana", "Chuối"),
                        Word("Orange", "Cam"),
                        Word("Water", "Nước"),
                        Word("Coffee", "Cà phê"),
                        Word("Tea", "Trà"),
                        Word("Bread", "Bánh mì"),
                        Word("Rice", "Cơm"),
                        Word("Meat", "Thịt"),
                        Word("Fish", "Cá"),
                        Word("Vegetable", "Rau củ"),
                        Word("Fruit", "Trái cây"),
                        Word("Milk", "Sữa"),
                        Word("Egg", "Trứng"),
                        Word("Cheese", "Phô mai"),
                        Word("Butter", "Bơ"),
                        Word("Sugar", "Đường"),
                        Word("Salt", "Muối"),
                        Word("Pepper", "Tiêu"),
                        Word("Oil", "Dầu ăn"),
                        Word("Soup", "Súp"),
                        Word("Salad", "Salad"),
                        Word("Pizza", "Pizza"),
                        Word("Burger", "Burger"),
                        Word("Sandwich", "Bánh sandwich"),
                        Word("Cake", "Bánh ngọt"),
                        Word("Cookie", "Bánh quy"),
                        Word("Ice cream", "Kem"),
                        Word("Chocolate", "Sô cô la"),
                        Word("Candy", "Kẹo")
                    )
                ),
                Topic(
                    id = "3",
                    name = "Technology",
                    words = listOf(
                        Word("Computer", "Máy tính"),
                        Word("Phone", "Điện thoại"),
                        Word("Internet", "Internet"),
                        Word("Website", "Trang web"),
                        Word("Email", "Email"),
                        Word("Password", "Mật khẩu"),
                        Word("Software", "Phần mềm"),
                        Word("Hardware", "Phần cứng"),
                        Word("Keyboard", "Bàn phím"),
                        Word("Mouse", "Chuột"),
                        Word("Screen", "Màn hình"),
                        Word("Monitor", "Màn hình"),
                        Word("Laptop", "Laptop"),
                        Word("Tablet", "Máy tính bảng"),
                        Word("Camera", "Máy ảnh"),
                        Word("Video", "Video"),
                        Word("Audio", "Âm thanh"),
                        Word("File", "Tệp tin"),
                        Word("Folder", "Thư mục"),
                        Word("Download", "Tải xuống"),
                        Word("Upload", "Tải lên"),
                        Word("Search", "Tìm kiếm"),
                        Word("Browser", "Trình duyệt"),
                        Word("App", "Ứng dụng"),
                        Word("Program", "Chương trình"),
                        Word("Code", "Mã code"),
                        Word("Data", "Dữ liệu"),
                        Word("Database", "Cơ sở dữ liệu"),
                        Word("Network", "Mạng"),
                        Word("Server", "Máy chủ")
                    )
                ),
                Topic(
                    id = "4",
                    name = "Animals",
                    words = listOf(
                        Word("Dog", "Chó"),
                        Word("Cat", "Mèo"),
                        Word("Bird", "Chim"),
                        Word("Fish", "Cá"),
                        Word("Lion", "Sư tử"),
                        Word("Tiger", "Hổ"),
                        Word("Elephant", "Voi"),
                        Word("Bear", "Gấu"),
                        Word("Rabbit", "Thỏ"),
                        Word("Horse", "Ngựa"),
                        Word("Cow", "Bò"),
                        Word("Pig", "Heo"),
                        Word("Sheep", "Cừu"),
                        Word("Goat", "Dê"),
                        Word("Chicken", "Gà"),
                        Word("Duck", "Vịt"),
                        Word("Monkey", "Khỉ"),
                        Word("Snake", "Rắn"),
                        Word("Frog", "Ếch"),
                        Word("Butterfly", "Bướm"),
                        Word("Spider", "Nhện"),
                        Word("Ant", "Kiến"),
                        Word("Bee", "Ong"),
                        Word("Fly", "Ruồi"),
                        Word("Mosquito", "Muỗi")
                    )
                ),
                Topic(
                    id = "5",
                    name = "Sports",
                    words = listOf(
                        Word("Football", "Bóng đá"),
                        Word("Basketball", "Bóng rổ"),
                        Word("Tennis", "Quần vợt"),
                        Word("Swimming", "Bơi lội"),
                        Word("Running", "Chạy bộ"),
                        Word("Cycling", "Đạp xe"),
                        Word("Golf", "Golf"),
                        Word("Baseball", "Bóng chày"),
                        Word("Volleyball", "Bóng chuyền"),
                        Word("Badminton", "Cầu lông"),
                        Word("Table tennis", "Bóng bàn"),
                        Word("Boxing", "Quyền anh"),
                        Word("Karate", "Karate"),
                        Word("Judo", "Judo"),
                        Word("Taekwondo", "Taekwondo"),
                        Word("Yoga", "Yoga"),
                        Word("Pilates", "Pilates"),
                        Word("Gym", "Phòng gym"),
                        Word("Exercise", "Tập thể dục"),
                        Word("Training", "Luyện tập"),
                        Word("Coach", "Huấn luyện viên"),
                        Word("Player", "Cầu thủ"),
                        Word("Team", "Đội"),
                        Word("Match", "Trận đấu"),
                        Word("Championship", "Giải vô địch")
                    )
                ),
                Topic(
                    id = "6",
                    name = "Weather",
                    words = listOf(
                        Word("Sunny", "Nắng"),
                        Word("Cloudy", "Có mây"),
                        Word("Rainy", "Mưa"),
                        Word("Snowy", "Tuyết"),
                        Word("Windy", "Gió"),
                        Word("Hot", "Nóng"),
                        Word("Cold", "Lạnh"),
                        Word("Warm", "Ấm"),
                        Word("Cool", "Mát"),
                        Word("Humid", "Ẩm ướt"),
                        Word("Dry", "Khô"),
                        Word("Wet", "Ướt"),
                        Word("Storm", "Bão"),
                        Word("Thunder", "Sấm"),
                        Word("Lightning", "Sét"),
                        Word("Rainbow", "Cầu vồng"),
                        Word("Fog", "Sương mù"),
                        Word("Ice", "Băng"),
                        Word("Frost", "Sương giá"),
                        Word("Temperature", "Nhiệt độ"),
                        Word("Forecast", "Dự báo thời tiết"),
                        Word("Season", "Mùa"),
                        Word("Spring", "Mùa xuân"),
                        Word("Summer", "Mùa hè"),
                        Word("Autumn", "Mùa thu"),
                        Word("Winter", "Mùa đông")
                    )
                )
            )
            saveTopics(defaultTopics)
            Log.d("TopicRepo", "Đã tạo file topics.json mặc định")
        }
    }

    // Tạo ID mới cho topic, tạo id nhỏ ch tồn tại ( lấy đầy khoảng trống id )
    fun generateNewTopicId(): String {
        val topics = loadTopics()
        val existingIds = topics.mapNotNull { it.id?.toIntOrNull() }.sorted()

        var newId = 1
        for (id in existingIds) {
            if (id == newId) {
                newId++
            } else if (id > newId) {
                break
            }
        }
        return newId.toString()
    }

    //******* ========== THÊM =================== **************
    //  Thêm hoặc cập nhật một topic (thông minh)
    fun addOrUpdateTopic(newTopic: Topic) {
        val currentTopics = loadTopics().toMutableList()
        val existing = currentTopics.find {
            it.id == newTopic.id || it.name.equals(newTopic.name, ignoreCase = true)
        }

        if (existing == null) {
            //  Nếu chưa tồn tại → thêm mới
            currentTopics.add(newTopic)
            Log.d("TopicRepo", "Đã thêm chủ đề mới: ${newTopic.name}")
        } else {
            // Kiểm tra danh sách từ có giống hệt không
            val sameWords = existing.words.size == newTopic.words.size &&
                    existing.words.containsAll(newTopic.words)

            if (sameWords) {
                Log.d("TopicRepo", "Chủ đề '${newTopic.name}' đã tồn tại và giống hệt, bỏ qua.")
                return
            } else {
                // Cập nhật chủ đề (thay thế danh sách từ)
                val index = currentTopics.indexOf(existing)
                currentTopics[index] = newTopic
                Log.d("TopicRepo", " Cập nhật chủ đề '${newTopic.name}' với danh sách từ mới.")
            }
        }
        saveTopics(currentTopics)
    }
    // Thêm từ vào chủ đề
    fun addWordToTopic(topicId: String, word: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            updatedWords.add(word)
            topics[index] = topics[index].copy(words = updatedWords)
            saveTopics(topics)
            Log.d("TopicRepo", "➕ Đã thêm từ '${word.word}' vào chủ đề")
        }
    }
    //Thêm tên chủ đề mới
    fun addNameTopic(newName: String) {
        val topics = loadTopics().toMutableList()
        val newId = generateNewTopicId()

        val newTopic = Topic(
            id = newId,
            name = newName,
            words = emptyList()
        )

        topics.add(newTopic)
        saveTopics(topics)

        Log.d("TopicRepo", "🆕 Đã thêm chủ đề mới: id=$newId, name=$newName")
    }

    //*** ================= XÓA ===============================
    //  Xóa một topic theo id
    fun deleteTopicById(id: String) {
        val currentTopics = loadTopics().filterNot { it.id == id }
        saveTopics(currentTopics)
        Log.d("TopicRepo", "🗑 Đã xóa chủ đề có id=$id")
    }
    // Xóa từ khỏi chủ đề
    fun deleteWordFromTopic(topicId: String, word: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            updatedWords.removeAll { it.word == word.word && it.meaning == word.meaning }
            topics[index] = topics[index].copy(words = updatedWords)
            saveTopics(topics)
            Log.d("TopicRepo", "🗑️ Đã xóa từ '${word.word}' khỏi chủ đề")
        }
    }


    // *** =============== CẬP NHẬT  =========================
    // Cập nhật tên chủ đề
    fun updateTopicName(id: String, newName: String) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == id }

        if (index != -1) {
            topics[index] = topics[index].copy(name = newName)
            saveTopics(topics)
            Log.d("TopicRepo", "✏️ Đã cập nhật tên chủ đề: $newName")
        }
    }

    // Cập nhật từ trong chủ đề
    fun updateWordInTopic(topicId: String, oldWord: Word, newWord: Word) {
        val topics = loadTopics().toMutableList()
        val index = topics.indexOfFirst { it.id == topicId }

        if (index != -1) {
            val updatedWords = topics[index].words.toMutableList()
            val wordIndex = updatedWords.indexOfFirst {
                it.word == oldWord.word && it.meaning == oldWord.meaning
            }

            if (wordIndex != -1) {
                updatedWords[wordIndex] = newWord
                topics[index] = topics[index].copy(words = updatedWords)
                saveTopics(topics)
                Log.d("TopicRepo", "✏️ Đã cập nhật từ '${newWord.word}'")
            }
        }
    }

    // Lấy một topic theo ID
    fun getTopicById(id: String): Topic? {
        return loadTopics().find { it.id == id }
    }

    // Kiểm tra trùng ID hoặc tên (public dùng cho form thêm chủ đề)
    fun isTopicDuplicate(topic: Topic): Boolean {
        val topics = loadTopics()
        return topics.any {
            it.id == topic.id || it.name.equals(topic.name, ignoreCase = true)
        }
    }
}
