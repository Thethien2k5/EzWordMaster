// ui/settings/SettingsScreen.kt
@Composable
fun SettingsScreen() {
    // TODO: Lấy trạng thái cài đặt từ ViewModel
    val notificationsEnabled by remember { mutableStateOf(true) }
    val notificationInterval by remember { mutableStateOf("Every 6 hours") }

    Scaffold(topBar = { /* TopBar */ }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Notifications")
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { /* TODO: Lưu cài đặt mới */ }
                )
            }
            // TODO: Thêm phần chọn thời gian (ví dụ: dropdown menu)
        }
    }
}