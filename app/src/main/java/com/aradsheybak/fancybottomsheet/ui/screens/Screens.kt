package com.aradsheybak.fancybottomsheet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    PageContent(
        title = "Home",
        description = "Your dashboard and latest activity.",
        accent = Color(0xFFE53946),
        modifier = modifier,
    )
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    PageContent(
        title = "Search",
        description = "Find people, tracks and messages.",
        accent = Color(0xFF4FA3FF),
        modifier = modifier,
    )
}

@Composable
fun InboxScreen(modifier: Modifier = Modifier) {
    PageContent(
        title = "Inbox",
        description = "You have 3 unread messages.",
        accent = Color(0xFFFFA726),
        modifier = modifier,
    )
}

@Composable
fun CreateScreen(modifier: Modifier = Modifier) {
    PageContent(
        title = "Create",
        description = "Start something new.",
        accent = Color(0xFF9C6BFF),
        modifier = modifier,
    )
}

@Composable
fun SavedScreen(modifier: Modifier = Modifier) {
    PageContent(
        title = "Saved",
        description = "Everything you bookmarked, in one place.",
        accent = Color(0xFF41D9A5),
        modifier = modifier,
    )
}

@Composable
private fun PageContent(
    title: String,
    description: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 148.dp),
    ) {
        Text(
            text = "FANCY BOTTOM SHEET",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(cardShape)
                .background(accent.copy(alpha = 0.14f))
                .border(width = 1.dp, color = accent.copy(alpha = 0.45f), shape = cardShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title.uppercase(),
                color = accent,
                fontSize = 22.sp,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
