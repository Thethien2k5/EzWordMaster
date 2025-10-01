package com.example.ezwordmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ezwordmaster.ui.intro.IntroScreen
import com.example.ezwordmaster.ui.theme.EzWordMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EzWordMasterTheme {
                IntroScreen()  // Màn hình intro đầu tiên
            }
        }
    }
}
