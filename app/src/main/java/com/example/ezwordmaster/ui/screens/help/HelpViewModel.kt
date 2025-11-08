package com.example.ezwordmaster.ui.screens.help

import androidx.lifecycle.ViewModel
import com.example.ezwordmaster.model.HelpItem
import com.example.ezwordmaster.model.HelpUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class HelpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HelpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHelpItems()
    }

    private fun loadHelpItems() {

        val items = listOf(
            HelpItem(
                1,
                "Tại sao tôi nên dùng EzWordMaster?",
                "Ứng dụng giúp bạn quản lý từ vựng cá nhân và ôn tập hiệu quả qua 3 chế độ (Flashcard, Quiz, Lật thẻ), biến việc học thành một trải nghiệm thú vị."
            ),
            HelpItem(
                2,
                "Làm thế nào để thêm một từ vựng mới?",
                "Bạn vào mục 'Quản lý', chọn một chủ đề, sau đó nhấn vào nút '+' (hoặc biểu tượng thêm) để nhập từ mới, ý nghĩa và ví dụ."
            ),
            HelpItem(
                3,
                "\"Chủ đề\" (Topic) là gì?",
                "\"Chủ đề\" là các thư mục do bạn tự tạo để phân loại và sắp xếp từ vựng, ví dụ: 'Từ vựng Giao tiếp', 'Từ vựng IELTS', 'Động từ bất quy tắc'."
            ),
            HelpItem(
                4,
                "Tôi có thể sửa hoặc xóa một từ vựng không?",
                "Có. Trong màn hình danh sách từ vựng của chủ đề, bạn có thể nhấn giữ (hoặc vuốt) vào từ muốn thao tác và chọn 'Sửa' hoặc 'Xóa'."
            ),
            HelpItem(
                5,
                "Làm sao để đổi tên hoặc xóa một \"Chủ đề\"?",
                "Tại màn hình 'Quản lý' (danh sách chủ đề), bạn có thể nhấn giữ vào chủ đề muốn chỉnh sửa để hiện ra các tùy chọn 'Đổi tên' hoặc 'Xóa'."
            ),
            HelpItem(
                6,
                "Chế độ \"Flashcard\" (Thẻ ghi nhớ) hoạt động ra sao?",
                "Chế độ này sẽ hiển thị mặt trước (từ) và mặt sau (nghĩa). Bạn tự đánh giá xem mình đã nhớ từ hay chưa và đánh dấu 'Đã biết' hoặc 'Đang học'."
            ),
            HelpItem(
                7,
                "Ứng dụng có bao nhiêu dạng \"Quiz\" (Trắc nghiệm)?",
                "Chúng tôi hỗ trợ nhiều dạng quiz như Trắc nghiệm (chọn 1 đáp án đúng), Đúng/Sai (True/False), và Tự luận (tự điền nghĩa của từ)."
            ),
            HelpItem(
                8,
                "Chế độ \"Lật thẻ\" (Giải trí) là gì?",
                "Đây là một trò chơi giúp bạn thư giãn. Bạn sẽ lật các ô để tìm các cặp Từ vựng - Ý nghĩa tương ứng trên một bảng lật."
            ),
            HelpItem(
                9,
                "Tôi có thể xem lại kết quả học tập của mình không?",
                "Có! Màn hình 'Lịch sử học tập' sẽ ghi lại điểm số, tiến độ, và kết quả của các phiên ôn tập (Quiz, Flashcard) mà bạn đã hoàn thành."
            ),
            HelpItem(
                10,
                "Chức năng \"Thông báo\" (Nhắc nhở) hoạt động thế nào?",
                "Ứng dụng có thể gửi nhắc nhở bạn vào học định kỳ. Bạn có thể bật/tắt và tùy chỉnh tần suất nhắc nhở (chu kỳ) trong mục 'Cài đặt'."
            ),
            HelpItem(
                11,
                "Dữ liệu của tôi có bị mất khi tôi tắt ứng dụng không?",
                "Không. Tất cả chủ đề và từ vựng của bạn đều được lưu trữ an toàn ngay trên thiết bị (lưu trữ cục bộ) và sẽ không bị mất khi tắt ứng dụng."
            ),
            HelpItem(
                12,
                "Ứng dụng này có miễn phí không?",
                "Ứng dụng này hoàn toàn miễn phí, được phát triển bởi HKT2 nhằm mục đích hỗ trợ cộng đồng học tập tiếng Anh hiệu quả hơn."
            )
        )
        _uiState.update { it.copy(helpItems = items) }
    }

    fun onQuestionClicked(item: HelpItem) {
        _uiState.update { currentState ->
            // Nếu câu hỏi đã được chọn, bỏ chọn nó. Nếu chưa, chọn nó.
            val newSelectedItem = if (currentState.selectedItem == item) null else item
            currentState.copy(selectedItem = newSelectedItem)
        }
    }
}

