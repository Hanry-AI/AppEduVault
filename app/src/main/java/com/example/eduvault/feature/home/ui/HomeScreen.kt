package com.example.eduvault.feature.home.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eduvault.core.theme.ColorAmber
import com.example.eduvault.core.theme.ColorAmberDark
import com.example.eduvault.core.theme.ColorBorder
import com.example.eduvault.core.theme.ColorCream
import com.example.eduvault.core.theme.ColorForest
import com.example.eduvault.core.theme.ColorForestLight
import com.example.eduvault.core.theme.ColorInk
import com.example.eduvault.core.theme.ColorInkLight
import com.example.eduvault.core.theme.ColorInkLighter
import com.example.eduvault.core.theme.ColorTextOnDark
import com.example.eduvault.core.theme.ColorTextOnDarkSecondary
import com.example.eduvault.core.theme.ColorTextOnLight
import com.example.eduvault.core.theme.ColorTextOnLightSecondary
import com.example.eduvault.core.theme.DmSansFamily
import com.example.eduvault.core.theme.EduVaultTheme
import com.example.eduvault.core.theme.JetBrainsMonoFamily
import com.example.eduvault.core.theme.PlayfairDisplayFamily

// ─── Entry Point ──────────────────────────────────────────────────────────────


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScaffold(
        uiState = uiState,
        recentDocs = viewModel.recentDocs,
        activityItems = viewModel.activityItems,
        topicTags = viewModel.topicTags,
        onTabSelected = viewModel::onTabSelected,
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// SCAFFOLD: Bottom Navigation + FAB
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeScaffold(
    uiState: HomeUiState,
    recentDocs: List<RecentDoc>,
    activityItems: List<ActivityItem>,
    topicTags: List<TopicTag>,
    onTabSelected: (HomeTab) -> Unit,
) {
    Scaffold(
        containerColor = ColorCream,
        bottomBar = {
            HomeBottomNav(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected,
            )
        },
        floatingActionButton = {
            // FAB ở giữa bottom bar (bên trên)
        },
    ) { paddingValues ->
        when (uiState.selectedTab) {
            HomeTab.HOME -> HomeContent(
                uiState = uiState,
                recentDocs = recentDocs,
                activityItems = activityItems,
                topicTags = topicTags,
                paddingValues = paddingValues,
            )
            HomeTab.DOCUMENTS -> HomeContent(
                uiState = uiState,
                recentDocs = recentDocs,
                activityItems = activityItems,
                topicTags = topicTags,
                paddingValues = paddingValues,
            ) // TODO: DocumentsContent
            HomeTab.QUIZ -> HomeContent(
                uiState = uiState,
                recentDocs = recentDocs,
                activityItems = activityItems,
                topicTags = topicTags,
                paddingValues = paddingValues,
            ) // TODO: QuizContent
            HomeTab.PROFILE -> HomeContent(
                uiState = uiState,
                recentDocs = recentDocs,
                activityItems = activityItems,
                topicTags = topicTags,
                paddingValues = paddingValues,
            ) // TODO: ProfileContent
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────

@Composable
private fun HomeBottomNav(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
) {
    NavigationBar(
        containerColor = ColorInk,
        contentColor = ColorAmber,
        tonalElevation = 0.dp,
    ) {
        val items = listOf(
            Triple(HomeTab.HOME, Icons.Outlined.Home, "Trang chủ"),
            Triple(HomeTab.DOCUMENTS, Icons.Outlined.LibraryBooks, "Tài liệu"),
            Triple(HomeTab.QUIZ, Icons.Outlined.Quiz, "Quiz AI"),
            Triple(HomeTab.PROFILE, Icons.Outlined.AccountCircle, "Hồ sơ"),
        )

        items.forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontFamily = DmSansFamily,
                        fontSize = 10.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ColorAmber,
                    selectedTextColor = ColorAmber,
                    unselectedIconColor = ColorTextOnDarkSecondary,
                    unselectedTextColor = ColorTextOnDarkSecondary,
                    indicatorColor = ColorInkLighter,
                )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HOME CONTENT (LazyColumn)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    recentDocs: List<RecentDoc>,
    activityItems: List<ActivityItem>,
    topicTags: List<TopicTag>,
    paddingValues: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        // ── Top App Bar ────────────────────────────────────────────────────
        item { HomeTopBar(uiState = uiState) }

        // ── Hero Section ───────────────────────────────────────────────────
        item {
            HeroSection(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
        }

        // ── Quick Tools ────────────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Công cụ nhanh",
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            QuickToolsGrid(modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Recent Documents ───────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Tài liệu gần đây",
                actionText = "Xem tất cả",
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            RecentDocsRow(docs = recentDocs)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── AI Tools ──────────────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Công cụ AI",
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            AiToolsSection(modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Activity ───────────────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Hoạt động gần đây",
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            ActivitySection(
                items = activityItems,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Topic Tags ─────────────────────────────────────────────────────
        item {
            SectionHeader(
                title = "Chủ đề nổi bật",
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            TopicTagsSection(
                tags = topicTags,
                modifier = Modifier.padding(horizontal = 14.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ─── Top App Bar ──────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(uiState: HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorInk)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAmber.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.5f),
                        radius = size.width * 0.55f,
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 0.85f, size.height * 0.5f),
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(colors = listOf(ColorAmber, ColorAmberDark))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "E",
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = ColorInk,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        append("Xin chào, ")
                        withStyle(SpanStyle(color = ColorAmber, fontStyle = FontStyle.Italic)) {
                            append(uiState.userName.split(" ").first())
                        }
                        append(" 👋")
                    },
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ColorTextOnDark,
                )
                Text(
                    text = "🏛️ ${uiState.university}",
                    fontFamily = DmSansFamily,
                    fontSize = 11.sp,
                    color = ColorTextOnDarkSecondary,
                )
            }

            // Search button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ColorInkLight)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Tìm kiếm",
                    tint = ColorTextOnDark,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Notification button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ColorInkLight)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Thông báo",
                    tint = ColorTextOnDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_float")
    val float1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float1"
    )
    val float2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(3200, 800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float2"
    )
    val float3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2800, 1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float3"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing), RepeatMode.Reverse),
        label = "hero_glow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(ColorInk)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAmber.copy(alpha = glowAlpha), Color.Transparent),
                        center = Offset(size.width * 0.75f, size.height * 0.25f),
                        radius = size.width * 0.65f
                    ),
                    radius = size.width * 0.65f,
                    center = Offset(size.width * 0.75f, size.height * 0.25f),
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorForest.copy(alpha = glowAlpha * 0.8f), Color.Transparent),
                        center = Offset(size.width * 0.25f, size.height * 0.85f),
                        radius = size.width * 0.5f
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(size.width * 0.25f, size.height * 0.85f),
                )
            }
            .padding(20.dp)
    ) {
        Column {
            // Label
            Text(
                text = "✦  NỀN TẢNG HỌC TẬP THÔNG MINH",
                fontFamily = JetBrainsMonoFamily,
                fontSize = 9.sp,
                color = ColorAmber,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Title
            Text(
                text = buildAnnotatedString {
                    append("Học ")
                    withStyle(SpanStyle(color = ColorAmber, fontStyle = FontStyle.Italic)) {
                        append("hiệu quả hơn")
                    }
                    append("\nvới AI của bạn")
                },
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 32.sp,
                color = ColorTextOnDark,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Description
            Text(
                text = "Tóm tắt tài liệu, tạo quiz, ghi chú thông minh — tất cả được xây dựng từ chính nguồn học của bạn.",
                fontFamily = DmSansFamily,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = ColorTextOnDarkSecondary,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // Floating mini-cards row
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                HeroFloatCard(
                    emoji = "📝", label = "Ghi chú", subtitle = "Kinh tế vi mô Ch.3",
                    modifier = Modifier.graphicsLayer { translationY = float1 }
                )
                HeroFloatCard(
                    emoji = "🧩", label = "Quiz AI", subtitle = "12/20 câu đúng",
                    modifier = Modifier.graphicsLayer { translationY = float2 }
                )
                HeroFloatCard(
                    emoji = "📊", label = "Tóm tắt", subtitle = "Hoàn thành 85%",
                    modifier = Modifier.graphicsLayer { translationY = float3 }
                )
            }

            // CTA Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorAmber)
                        .clickable {}
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖  Thử StudyAI",
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = ColorInk,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, ColorTextOnDark.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .background(ColorTextOnDark.copy(alpha = 0.08f))
                        .clickable {}
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tải tài liệu",
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = ColorTextOnDark,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroFloatCard(
    emoji: String,
    label: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ColorTextOnDark.copy(alpha = 0.08f))
            .border(1.dp, ColorTextOnDark.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text = emoji,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = label,
                fontFamily = JetBrainsMonoFamily,
                fontSize = 8.sp,
                color = ColorAmber,
                letterSpacing = 0.8.sp,
            )
            Text(
                text = subtitle,
                fontFamily = DmSansFamily,
                fontSize = 10.sp,
                color = ColorTextOnDark,
                lineHeight = 14.sp,
            )
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(
    title: String,
    actionText: String? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ColorTextOnLight,
            letterSpacing = (-0.3).sp,
        )
        if (actionText != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
            ) {
                Text(
                    text = actionText,
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ColorForest,
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = ColorForest,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ─── Quick Tools Grid ─────────────────────────────────────────────────────────

@Composable
private fun QuickToolsGrid(modifier: Modifier = Modifier) {
    val tools = listOf(
        QuickTool("📝", "Ghi chú AI", "Tự động tóm tắt tài liệu thành ghi chú có cấu trúc", Color(0xFF4A5568)),
        QuickTool("🧩", "Tạo Quiz", "Sinh câu hỏi trắc nghiệm từ bất kỳ tài liệu nào", ColorForest),
        QuickTool("🃏", "Flashcard", "Ôn tập nhanh với phương pháp spaced repetition", Color(0xFFC0392B)),
        QuickTool("🔍", "Hỏi AI", "Đặt câu hỏi về bất kỳ chủ đề nào", ColorInk),
    )
    val bgColors = listOf(
        ColorAmber.copy(alpha = 0.12f),
        ColorForest.copy(alpha = 0.10f),
        Color(0xFFC0392B).copy(alpha = 0.09f),
        ColorInk.copy(alpha = 0.07f),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickToolCard(tool = tools[0], bgColor = bgColors[0], modifier = Modifier.weight(1f))
            QuickToolCard(tool = tools[1], bgColor = bgColors[1], modifier = Modifier.weight(1f))
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickToolCard(tool = tools[2], bgColor = bgColors[2], modifier = Modifier.weight(1f))
            QuickToolCard(tool = tools[3], bgColor = bgColors[3], modifier = Modifier.weight(1f))
        }
    }
}

private data class QuickTool(val emoji: String, val name: String, val desc: String, val accentColor: Color)

@Composable
private fun QuickToolCard(tool: QuickTool, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable {},
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp, pressedElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = tool.emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = tool.name,
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ColorTextOnLight,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = tool.desc,
                fontFamily = DmSansFamily,
                fontSize = 11.sp,
                color = ColorTextOnLightSecondary,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ─── Recent Documents Row (horizontal scroll) ─────────────────────────────────

@Composable
private fun RecentDocsRow(docs: List<RecentDoc>) {
    val thumbBgs = listOf(
        listOf(Color(0xFFFEF9F0), Color(0xFFFDF0D5)),
        listOf(Color(0xFFF0F9F4), Color(0xFFD4EDDA)),
        listOf(Color(0xFFF0F4FF), Color(0xFFDDE8FF)),
        listOf(Color(0xFFFFF0F0), Color(0xFFFFDDDD)),
        listOf(Color(0xFFF5F0FF), Color(0xFFEAD5FF)),
        listOf(Color(0xFFF0FCFF), Color(0xFFD5F0FF)),
    )
    val badgeColors = mapOf(
        DocType.NOTE to Pair(ColorAmber.copy(alpha = 0.18f), ColorAmberDark),
        DocType.QUIZ to Pair(ColorForest.copy(alpha = 0.14f), ColorForest),
        DocType.SLIDE to Pair(Color(0xFF4A5568).copy(alpha = 0.12f), Color(0xFF2D3748)),
        DocType.SUMMARY to Pair(Color(0xFFC0392B).copy(alpha = 0.10f), Color(0xFF922B21)),
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        items(docs) { doc ->
            val bg = thumbBgs.getOrElse(doc.bgIndex) { thumbBgs[0] }
            val badge = badgeColors[doc.type] ?: Pair(ColorAmber.copy(alpha = 0.18f), ColorAmberDark)

            DocCard(
                doc = doc,
                thumbGradient = Brush.linearGradient(bg.map { it }),
                badgeBg = badge.first,
                badgeText = badge.second,
            )
        }
    }
}

@Composable
private fun DocCard(
    doc: RecentDoc,
    thumbGradient: Brush,
    badgeBg: Color,
    badgeText: Color,
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable {},
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // Thumb
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(thumbGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(text = doc.emoji, fontSize = 28.sp)
                // Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(badgeBg)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = doc.type.label.uppercase(),
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = badgeText,
                        letterSpacing = 0.5.sp,
                    )
                }
                // Pages
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${doc.pages}tr",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 9.sp,
                        color = ColorTextOnLightSecondary,
                    )
                }
            }

            // Info
            Column(modifier = Modifier.padding(horizontal = 11.dp, vertical = 10.dp)) {
                Text(
                    text = doc.course,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 9.sp,
                    color = ColorTextOnLightSecondary,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = doc.title,
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = ColorTextOnLight,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Text(
                            text = "👁 ${doc.views}",
                            fontFamily = DmSansFamily,
                            fontSize = 10.sp,
                            color = ColorTextOnLightSecondary,
                        )
                        Text(
                            text = "⭐ ${doc.saves}",
                            fontFamily = DmSansFamily,
                            fontSize = 10.sp,
                            color = ColorTextOnLightSecondary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(ColorCream)
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmarks,
                            contentDescription = "Lưu",
                            tint = ColorTextOnLightSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── AI Tools Section ─────────────────────────────────────────────────────────

@Composable
private fun AiToolsSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Card 1: StudyAI
        AiToolCard(
            tag = "✦  AI POWERED",
            title = "StudyAI",
            description = "Hỏi bất kỳ câu hỏi nào về tài liệu của bạn. AI sẽ giải thích theo cách dễ hiểu nhất.",
            emoji = "🤖",
            isAmber = true,
        )
        // Card 2: Quiz Generator
        AiToolCard(
            tag = "✦  QUIZ ENGINE",
            title = "Tạo Quiz tự động",
            description = "Upload tài liệu — Quiz được tạo trong vài giây. Hỗ trợ trắc nghiệm, điền khuyết, đúng/sai.",
            emoji = "🧩",
            isAmber = false,
        )
    }
}

@Composable
private fun AiToolCard(
    tag: String,
    title: String,
    description: String,
    emoji: String,
    isAmber: Boolean,
) {
    val accentGlow = if (isAmber) ColorAmber else ColorForest

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorInk)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accentGlow.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.2f),
                        radius = size.width * 0.6f,
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.9f, size.height * 0.2f),
                )
            }
            .clickable {}
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 9.sp,
                    color = if (isAmber) ColorAmber else ColorForestLight,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(bottom = 7.dp)
                )
                Text(
                    text = title,
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = ColorTextOnDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = description,
                    fontFamily = DmSansFamily,
                    fontSize = 12.sp,
                    color = ColorTextOnDarkSecondary,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorTextOnDark.copy(alpha = 0.10f))
                        .border(1.dp, ColorTextOnDark.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .clickable {}
                        .padding(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Thử ngay →",
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ColorTextOnDark,
                    )
                }
            }
            Text(
                text = emoji,
                fontSize = 48.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .graphicsLayer { alpha = 0.15f }
                    .offset(x = 6.dp, y = 8.dp)
            )
        }
    }
}

// ─── Activity Section ─────────────────────────────────────────────────────────

@Composable
private fun ActivitySection(items: List<ActivityItem>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Gần đây",
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = ColorTextOnLight,
                )
                Text(
                    text = "Xem tất cả →",
                    fontFamily = DmSansFamily,
                    fontSize = 12.sp,
                    color = ColorForest,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
                )
            }

            // Activity rows
            items.forEachIndexed { index, item ->
                ActivityRow(item = item)
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(ColorBorder)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem) {
    val (iconBg, iconEmoji) = when (item.type) {
        ActivityType.NOTE -> Pair(ColorAmber.copy(alpha = 0.12f), "📝")
        ActivityType.QUIZ -> Pair(ColorForest.copy(alpha = 0.12f), "🧩")
        ActivityType.SLIDE -> Pair(Color(0xFF4A5568).copy(alpha = 0.10f), "📊")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = iconEmoji, fontSize = 16.sp)
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = ColorTextOnLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                fontFamily = DmSansFamily,
                fontSize = 11.sp,
                color = ColorTextOnLightSecondary,
            )
            if (item.progress != null) {
                Spacer(modifier = Modifier.height(5.dp))
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .width(70.dp)
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = ColorForestLight,
                    trackColor = ColorBorder,
                    strokeCap = StrokeCap.Round,
                )
            }
        }

        // Time
        Text(
            text = item.time,
            fontFamily = JetBrainsMonoFamily,
            fontSize = 10.sp,
            color = ColorTextOnLightSecondary,
        )
    }
}

// ─── Topic Tags Section ───────────────────────────────────────────────────────

@Composable
private fun TopicTagsSection(tags: List<TopicTag>, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White)
                    .border(1.5.dp, ColorBorder, RoundedCornerShape(100.dp))
                    .clickable {}
                    .padding(horizontal = 13.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = tag.label,
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = ColorTextOnLightSecondary,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(ColorCream)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${tag.count}",
                            fontFamily = JetBrainsMonoFamily,
                            fontSize = 10.sp,
                            color = ColorTextOnLightSecondary,
                        )
                    }
                }
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(name = "HomeScreen — Phone", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HomeScreenPreview() {
    EduVaultTheme(darkTheme = false) {
        val vm = HomeViewModel()
        HomeScaffold(
            uiState = HomeUiState(),
            recentDocs = vm.recentDocs,
            activityItems = vm.activityItems,
            topicTags = vm.topicTags,
            onTabSelected = {},
        )
    }
}
