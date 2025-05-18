package com.example.bughouse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Admin Navigation Drawer content
 */
@Composable
fun AdminNavigationDrawerContent(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit,
    currentScreen: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
    ) {
        // Header with admin info and avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0795DD))
                .padding(vertical = 32.dp, horizontal = 16.dp)
        ) {
            Column {
                // Profile image with initials
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AD",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Admin User",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    text = "admin@bughouse.edu",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items with proper icons
        AdminNavigationMenuItem(
            icon = Icons.Default.Home,
            title = "Admin Dashboard",
            onClick = onHomeClick,
            isSelected = currentScreen == "Admin Dashboard"
        )

        AdminNavigationMenuItem(
            icon = Icons.Default.Person,
            title = "Profile",
            onClick = onProfileClick,
            isSelected = currentScreen == "Profile"
        )

        AdminNavigationMenuItem(
            icon = Icons.Default.Email,
            title = "Contact Us",
            onClick = onContactUsClick,
            isSelected = currentScreen == "Contact Us"
        )

        AdminNavigationMenuItem(
            icon = Icons.Default.Info,
            title = "About",
            onClick = onAboutClick,
            isSelected = currentScreen == "About"
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.LightGray
        )

        // Logout with icon
        AdminNavigationMenuItem(
            icon = Icons.Default.ExitToApp,
            title = "Logout",
            onClick = onLogoutClick,
            tint = Color(0xFFE53935) // Red color for logout
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Admin Navigation Item to be used in the navigation drawer
 */
@Composable
fun AdminNavigationMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    tint: Color = Color(0xFF0795DD)
) {
    val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF0795DD) else Color.DarkGray
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}