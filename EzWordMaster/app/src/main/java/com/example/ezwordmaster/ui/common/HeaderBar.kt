package com.example.ezwordmaster.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ezwordmaster.R


@Composable
fun NameBar(text: String) {
    Row {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = (-24).dp),
            textAlign = TextAlign.Center,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            style = TextStyle(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF00BCD4), Color(0xFF2196F3))
                )
            )
        )

    }
}

@Composable
fun BackBar(navController: NavController, text: String) {
    Row {
        Image(
            painter = painterResource(id = R.drawable.return_),
            contentDescription = "Back",
            modifier = Modifier
                .size(45.dp)
                .offset(10.dp)
                .clickable { navController.popBackStack() }
        )
        NameBar(text)
    }
}

@Composable
fun HeaderBar(
    navController: NavController,
    text: String,
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    showSearchHeader: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        BackBar(navController, text)

        if (showSearchHeader) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Search bar với kính lúp
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Tìm kiếm", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFF00BCD4),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        trailingIcon = {
                            Box(modifier = Modifier.size(40.dp))
                        }
                    )
                    // Kính lúp
                    Icon(
                        painter = painterResource(id = R.drawable.magnifying_glass),
                        contentDescription = "Search Icon",
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier
                            .size(43.dp)
                            .align(Alignment.CenterEnd)
                            .offset(x = 1.dp, 11.dp)
                    )
                }
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .offset(x = (-30).dp) // Rất gần thanh tìm kiếm
                )
            }
        }
    }
}