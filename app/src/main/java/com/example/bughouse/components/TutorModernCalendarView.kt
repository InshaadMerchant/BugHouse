package com.example.bughouse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun TutorModernCalendarView(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    daysWithAppointments: List<Int>
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        // Month and Year header with navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    tint = Color(0xFF0795DD)
                )
            }
            
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = Color(0xFF0795DD)
                )
            }
        }
        
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        
        Column {
            var dayCounter = 1
            repeat((daysInMonth + firstDayOfWeek + 6) / 7) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(7) { dayOfWeek ->
                        val dayNumber = week * 7 + dayOfWeek - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val isSelected = selectedDate?.equals(date) == true
                            val hasAppointment = daysWithAppointments.contains(dayNumber)
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF0795DD)
                                            hasAppointment -> Color(0xFFE3F2FD)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    color = when {
                                        isSelected -> Color.White
                                        else -> Color.Black
                                    },
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
        
        // Today indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Today",
                color = Color(0xFF0795DD),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 