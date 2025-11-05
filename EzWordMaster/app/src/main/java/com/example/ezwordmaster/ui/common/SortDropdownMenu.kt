package com.example.ezwordmaster.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ezwordmaster.R
import com.example.ezwordmaster.model.FilterSortType


@Composable
fun SortDropdownMenu(
    showDropdown: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    filterSortType: FilterSortType,
    onFilterSortTypeChange: (FilterSortType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Chủ đề với dropdown
        Row(
            modifier = Modifier
                .clickable { onDropdownToggle(!showDropdown) }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chủ Đề",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_triangle),
                contentDescription = "Sort",
                tint = Color.Black,
                modifier = Modifier
                    .size(14.dp)
                    .rotate(if (showDropdown) 180f else 0f)
            )
        }

        Text(
            text = "Số lượng từ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
    Menu(showDropdown, filterSortType, onFilterSortTypeChange, onDropdownToggle, modifier)
}

@Composable
fun Menu(
    showDropdown: Boolean,
    filterSortType: FilterSortType,
    onFilterSortTypeChange: (FilterSortType) -> Unit,
    onDropdownToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Menu thả xuống
    AnimatedVisibility(
        visible = showDropdown,
        enter = fadeIn(tween(500)) + expandVertically(tween(500)),
        exit = fadeOut(tween(300)) + shrinkVertically(tween(300)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xCCFFFFFF),
                            Color(0xAAFFFFFF),
                            Color(0x88FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0x40000000),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val AVAILABLESORTTYPES = listOf(
                FilterSortType.ALL,
                FilterSortType.Z_TO_A,
                FilterSortType.WORD_COUNT
            )
            AVAILABLESORTTYPES.forEach { type ->
                Box(
                    modifier = Modifier
                        .background(
                            color = if (filterSortType == type)
                                Color(0xFF00BCD4)
                            else
                                Color(0x30000000),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            onFilterSortTypeChange(type)
                            onDropdownToggle(false)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when (type) {
                            FilterSortType.ALL -> "Tất cả"
                            FilterSortType.Z_TO_A -> "Z - A"
                            FilterSortType.WORD_COUNT -> "Số lượng"
                        },
                        fontSize = 13.sp,
                        color = if (filterSortType == type) Color.White else Color.Black,
                        fontWeight = if (filterSortType == type) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

