package com.example.eduvault.feature.library.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eduvault.core.theme.ColorAmber
import com.example.eduvault.core.theme.ColorAmberDark
import com.example.eduvault.core.theme.ColorAmberLight
import com.example.eduvault.core.theme.ColorBorder
import com.example.eduvault.core.theme.ColorCream
import com.example.eduvault.core.theme.ColorForest
import com.example.eduvault.core.theme.ColorInk
import com.example.eduvault.core.theme.ColorPaper
import com.example.eduvault.core.theme.ColorTextOnDark
import com.example.eduvault.core.theme.ColorTextOnDarkSecondary
import com.example.eduvault.core.theme.ColorTextOnLight
import com.example.eduvault.core.theme.ColorTextOnLightSecondary
import com.example.eduvault.core.theme.DmSansFamily
import com.example.eduvault.core.theme.EduVaultTheme
import com.example.eduvault.core.theme.JetBrainsMonoFamily
import com.example.eduvault.core.theme.PlayfairDisplayFamily
import com.example.eduvault.domain.model.DocReaderState
import com.example.eduvault.domain.model.DocViewerTabsContent

@Composable
fun LibraryContent(
    paddingValues: PaddingValues,
    onUploadClick: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showBlockDialog by remember { mutableStateOf(false) }
    var selectedViewDoc by remember { mutableStateOf<LibraryDoc?>(null) }

    // Reload credits when entering the tab
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadUserCredits()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPaper)
            .padding(paddingValues)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 1. Search Bar & Pill selectors & Actions
        SearchBarAndActions(
            searchQuery = uiState.searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            selectedSort = uiState.selectedSort,
            onSortSelected = viewModel::onSortSelected,
            selectedSchool = uiState.selectedSchool,
            onSchoolSelected = viewModel::onSchoolSelected,
            isGridView = uiState.isGridView,
            onToggleViewMode = viewModel::onToggleViewMode,
            documentCredits = uiState.documentCredits,
            onUploadClick = onUploadClick
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Banner: "Tài liệu của bạn"
        DocsBanner()

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Course Chips List
        CourseChipsRow(
            selectedSubject = uiState.selectedSubject,
            onSubjectSelected = viewModel::onSubjectSelected
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Document Type Tabs
        DocTypeTabsRow(
            selectedDocType = uiState.selectedDocType,
            onDocTypeSelected = viewModel::onDocTypeSelected,
            totalCount = uiState.totalCount
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Document List/Grid showing count and quick pills
        DocSortHeaderRow(
            totalCount = uiState.totalCount,
            selectedSort = uiState.selectedSort,
            onSortSelected = viewModel::onSortSelected
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 6. Documents list grid or list column
        if (uiState.documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không tìm thấy tài liệu phù hợp",
                    fontFamily = DmSansFamily,
                    color = ColorTextOnLightSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            if (uiState.isGridView) {
                // Since verticalScroll is active on outer Column, we construct a responsive flow list
                // rather than standard LazyVerticalGrid to avoid nested scroll issues
                val chunkedList = uiState.documents.chunked(2)
                chunkedList.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { doc ->
                            LibraryDocCard(
                                doc = doc,
                                onToggleSaved = viewModel::onToggleSaved,
                                onDocClick = {
                                    viewModel.onOpenDocument(
                                        docId = doc.id,
                                        onAllowed = { selectedViewDoc = doc },
                                        onBlocked = { showBlockDialog = true }
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                uiState.documents.forEach { doc ->
                    LibraryDocListItem(
                        doc = doc,
                        onToggleSaved = viewModel::onToggleSaved,
                        onDocClick = {
                            viewModel.onOpenDocument(
                                docId = doc.id,
                                onAllowed = { selectedViewDoc = doc },
                                onBlocked = { showBlockDialog = true }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Pagination
        PaginationRow(
            currentPage = uiState.currentPage,
            totalPages = uiState.totalPages,
            onPageSelected = viewModel::onPageSelected
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Dialogs overlay
    if (showBlockDialog) {
        CreditBlockDialog(
            onDismiss = { showBlockDialog = false },
            onUploadClick = onUploadClick
        )
    }

    selectedViewDoc?.let { doc ->
        DocViewerDialog(
            doc = doc,
            onDismiss = { selectedViewDoc = null }
        )
    }
}

// ─── Component 1: Search Bar & Actions ─────────────────────────────────────────
@Composable
private fun SearchBarAndActions(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSort: SortType,
    onSortSelected: (SortType) -> Unit,
    selectedSchool: String,
    onSchoolSelected: (String) -> Unit,
    isGridView: Boolean,
    onToggleViewMode: () -> Unit,
    documentCredits: Int,
    onUploadClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var schoolMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Search Text Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Tìm tài liệu, môn học...",
                        fontFamily = DmSansFamily,
                        fontSize = 13.sp,
                        color = ColorTextOnLightSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = ColorTextOnLightSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ColorAmber,
                    unfocusedBorderColor = ColorBorder
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Credit Badge displaying actual credits
            Row(
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorAmber.copy(alpha = 0.12f))
                    .border(1.5.dp, ColorAmber, RoundedCornerShape(10.dp))
                    .clickable { onUploadClick() }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🧩 $documentCredits lượt",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    color = ColorAmberDark
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pill Dropdowns
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // School Select
                Box {
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                            .clickable { schoolMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSchool,
                            fontFamily = DmSansFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextOnLight
                        )
                    }

                    DropdownMenu(
                        expanded = schoolMenuExpanded,
                        onDismissRequest = { schoolMenuExpanded = false }
                    ) {
                        listOf("Tất cả trường", "ĐH Kinh tế TP.HCM", "ĐH Bách Khoa", "ĐH Quốc Gia").forEach { school ->
                            DropdownMenuItem(
                                text = { Text(school, fontFamily = DmSansFamily, fontSize = 12.sp) },
                                onClick = {
                                    onSchoolSelected(school)
                                    schoolMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Sort Dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                            .clickable { sortMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSort.label,
                            fontFamily = DmSansFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextOnLight
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        SortType.values().forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort.label, fontFamily = DmSansFamily, fontSize = 12.sp) },
                                onClick = {
                                    onSortSelected(sort)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Grid / List Layout Toggle
            IconButton(
                onClick = onToggleViewMode,
                modifier = Modifier
                    .size(34.dp)
                    .background(ColorCream, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Filled.List else Icons.Filled.GridOn,
                    contentDescription = "Toggle view",
                    tint = ColorTextOnLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ─── Component 2: Banner "Tài liệu của bạn" ────────────────────────────────────
@Composable
private fun DocsBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ColorInk)
            .drawBehind {
                // Background radial gradients matching warm editorial tech look
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAmber.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.1f),
                        radius = size.width * 0.5f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorForest.copy(alpha = 0.14f), Color.Transparent),
                        center = Offset(size.width * 0.4f, size.height * 0.9f),
                        radius = size.width * 0.4f
                    )
                )
            }
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "✦ KHO HỌC LIỆU",
                fontFamily = JetBrainsMonoFamily,
                fontSize = 9.sp,
                color = ColorAmber,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = buildAnnotatedString {
                    append("Tài liệu ")
                    withStyle(style = SpanStyle(color = ColorAmber, fontStyle = FontStyle.Italic)) {
                        append("của bạn")
                    }
                },
                fontFamily = PlayfairDisplayFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tìm kiếm, lọc và quản lý toàn bộ ghi chú, quiz, slide từ cộng đồng và thư viện cá nhân.",
                fontFamily = DmSansFamily,
                fontSize = 11.sp,
                color = ColorTextOnDarkSecondary,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Stat 1
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "2.4k",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorAmber
                    )
                    Text(
                        text = "TÀI LIỆU",
                        fontFamily = DmSansFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextOnDarkSecondary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Stat 2
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "18",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorAmber
                    )
                    Text(
                        text = "MÔN HỌC",
                        fontFamily = DmSansFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextOnDarkSecondary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Stat 3
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "12",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorAmber
                    )
                    Text(
                        text = "ĐÃ LƯU",
                        fontFamily = DmSansFamily,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextOnDarkSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

// ─── Component 3: Course Chips Row ─────────────────────────────────────────────
@Composable
private fun CourseChipsRow(
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit
) {
    val subjects = listOf("Tất cả", "Kinh tế vi mô", "Marketing", "Thống kê", "Quản trị", "Kế toán", "Luật", "Lập trình")
    val colors = listOf(ColorForest, ColorAmber, Color(0xFFC0392B), ColorInk, Color(0xFF8E44AD), Color(0xFF2980B9))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(subjects) { subject ->
            val isSelected = selectedSubject == subject
            val color = if (subject == "Tất cả") ColorForest else {
                val index = (subject.hashCode() % colors.size).let { if (it < 0) it + colors.size else it }
                colors[index]
            }

            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) color else Color.White)
                    .border(1.dp, if (isSelected) color else ColorBorder, CircleShape)
                    .clickable { onSubjectSelected(subject) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (isSelected) Color.White else color, CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = subject,
                    fontFamily = DmSansFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else ColorTextOnLight
                )
            }
        }
    }
}

// ─── Component 4: Document Type Tabs ───────────────────────────────────────────
@Composable
private fun DocTypeTabsRow(
    selectedDocType: DocTypeFilter,
    onDocTypeSelected: (DocTypeFilter) -> Unit,
    totalCount: Int
) {
    val tabs = DocTypeFilter.values()
    val counts = mapOf(
        DocTypeFilter.ALL to totalCount,
        DocTypeFilter.NOTE to 94,
        DocTypeFilter.QUIZ to 62,
        DocTypeFilter.SLIDE to 51,
        DocTypeFilter.SUMMARY to 28,
        DocTypeFilter.FLASHCARD to 15
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tabs) { tab ->
            val isSelected = selectedDocType == tab
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ColorInk else Color.White)
                    .border(1.dp, if (isSelected) ColorInk else ColorBorder, RoundedCornerShape(8.dp))
                    .clickable { onDocTypeSelected(tab) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon representation
                val icon = when (tab) {
                    DocTypeFilter.ALL -> "📁"
                    DocTypeFilter.NOTE -> "📝"
                    DocTypeFilter.QUIZ -> "🧩"
                    DocTypeFilter.SLIDE -> "📊"
                    DocTypeFilter.SUMMARY -> "📌"
                    DocTypeFilter.FLASHCARD -> "🃏"
                }

                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tab.label,
                    fontFamily = DmSansFamily,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else ColorTextOnLight
                )

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.15f) else ColorCream)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${counts[tab] ?: 0}",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 9.sp,
                        color = if (isSelected) ColorTextOnDarkSecondary else ColorTextOnLightSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ─── Component 5: Doc Sort Header Row ──────────────────────────────────────────
@Composable
private fun DocSortHeaderRow(
    totalCount: Int,
    selectedSort: SortType,
    onSortSelected: (SortType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append("Hiển thị ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = ColorInk)) {
                    append("$totalCount")
                }
                append(" tài liệu")
            },
            fontFamily = DmSansFamily,
            fontSize = 12.sp,
            color = ColorTextOnLightSecondary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SortType.values().forEach { sort ->
                val isSelected = selectedSort == sort
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) ColorInk else Color.Transparent)
                        .border(1.dp, if (isSelected) ColorInk else ColorBorder, CircleShape)
                        .clickable { onSortSelected(sort) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = sort.label,
                        fontFamily = DmSansFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else ColorTextOnLightSecondary
                    )
                }
            }
        }
    }
}

// ─── Component 6: Document Grid Card ───────────────────────────────────────────
@Composable
private fun LibraryDocCard(
    doc: LibraryDoc,
    onToggleSaved: (String) -> Unit,
    onDocClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Curated soft background list
    val bgGradients = listOf(
        Brush.linearGradient(listOf(Color(0xFFFEF9F0), Color(0xFFFDF0D5))), // Note
        Brush.linearGradient(listOf(Color(0xFFF0F9F4), Color(0xFFD4EDDA))), // Quiz
        Brush.linearGradient(listOf(Color(0xFFF0F4FF), Color(0xFFDDE8FF))), // Slide
        Brush.linearGradient(listOf(Color(0xFFFFF0F0), Color(0xFFFDD8D8))), // Summary
        Brush.linearGradient(listOf(Color(0xFFF5F0FF), Color(0xFFEAD5FF))),
        Brush.linearGradient(listOf(Color(0xFFF0FCFF), Color(0xFFD5F0FF)))
    )
    val gradient = bgGradients[doc.bgIndex % bgGradients.size]

    val emoji = when (doc.type) {
        LibraryDocType.NOTE -> "📝"
        LibraryDocType.QUIZ -> "🧩"
        LibraryDocType.SLIDE -> "📊"
        LibraryDocType.SUMMARY -> "📌"
    }

    val badgeBg = when (doc.type) {
        LibraryDocType.NOTE -> ColorAmber.copy(alpha = 0.15f)
        LibraryDocType.QUIZ -> ColorForest.copy(alpha = 0.12f)
        LibraryDocType.SLIDE -> ColorTextOnLightSecondary.copy(alpha = 0.12f)
        LibraryDocType.SUMMARY -> Color(0xFFC0392B).copy(alpha = 0.1f)
    }

    val badgeTextColor = when (doc.type) {
        LibraryDocType.NOTE -> ColorAmberDark
        LibraryDocType.QUIZ -> ColorForest
        LibraryDocType.SLIDE -> ColorTextOnLight
        LibraryDocType.SUMMARY -> Color(0xFFC0392B)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onDocClick() }
    ) {
        // Thumbnail section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(86.dp)
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            // Type badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(badgeBg, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = doc.type.label.uppercase(),
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor
                )
            }

            // Quantity Pages tag
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = doc.quantityLabel,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 8.sp,
                    color = ColorTextOnLightSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Large emoji center
            Text(text = emoji, fontSize = 28.sp)
        }

        // Info Section
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = doc.courseCode,
                fontFamily = JetBrainsMonoFamily,
                fontSize = 9.sp,
                color = ColorTextOnLightSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = doc.title,
                fontFamily = DmSansFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorInk,
                maxLines = 2,
                minLines = 2,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Rating",
                        tint = ColorAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${doc.rating}",
                        fontFamily = DmSansFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextOnLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Outlined.RemoveRedEye,
                        contentDescription = "Views",
                        tint = ColorTextOnLightSecondary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = doc.views,
                        fontFamily = DmSansFamily,
                        fontSize = 10.sp,
                        color = ColorTextOnLightSecondary
                    )
                }

                IconButton(
                    onClick = { onToggleSaved(doc.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (doc.isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save document",
                        tint = if (doc.isSaved) ColorAmber else ColorTextOnLightSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// ─── Component 6b: Document List Item ──────────────────────────────────────────
@Composable
private fun LibraryDocListItem(
    doc: LibraryDoc,
    onToggleSaved: (String) -> Unit,
    onDocClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgGradients = listOf(
        Brush.linearGradient(listOf(Color(0xFFFEF9F0), Color(0xFFFDF0D5))),
        Brush.linearGradient(listOf(Color(0xFFF0F9F4), Color(0xFFD4EDDA))),
        Brush.linearGradient(listOf(Color(0xFFF0F4FF), Color(0xFFDDE8FF))),
        Brush.linearGradient(listOf(Color(0xFFFFF0F0), Color(0xFFFDD8D8))),
        Brush.linearGradient(listOf(Color(0xFFF5F0FF), Color(0xFFEAD5FF))),
        Brush.linearGradient(listOf(Color(0xFFF0FCFF), Color(0xFFD5F0FF)))
    )
    val gradient = bgGradients[doc.bgIndex % bgGradients.size]

    val emoji = when (doc.type) {
        LibraryDocType.NOTE -> "📝"
        LibraryDocType.QUIZ -> "🧩"
        LibraryDocType.SLIDE -> "📊"
        LibraryDocType.SUMMARY -> "📌"
    }

    val badgeBg = when (doc.type) {
        LibraryDocType.NOTE -> ColorAmber.copy(alpha = 0.15f)
        LibraryDocType.QUIZ -> ColorForest.copy(alpha = 0.12f)
        LibraryDocType.SLIDE -> ColorTextOnLightSecondary.copy(alpha = 0.12f)
        LibraryDocType.SUMMARY -> Color(0xFFC0392B).copy(alpha = 0.1f)
    }

    val badgeTextColor = when (doc.type) {
        LibraryDocType.NOTE -> ColorAmberDark
        LibraryDocType.QUIZ -> ColorForest
        LibraryDocType.SLIDE -> ColorTextOnLight
        LibraryDocType.SUMMARY -> Color(0xFFC0392B)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onDocClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Thumbnail square
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(gradient),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center Info
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = doc.courseCode,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 9.sp,
                    color = ColorTextOnLightSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = doc.type.label.uppercase(),
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = doc.quantityLabel,
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 8.sp,
                    color = ColorTextOnLightSecondary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = doc.title,
                fontFamily = DmSansFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ColorInk,
                maxLines = 1,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Rating",
                    tint = ColorAmber,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${doc.rating}",
                    fontFamily = DmSansFamily,
                    fontSize = 10.sp,
                    color = ColorTextOnLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.RemoveRedEye,
                    contentDescription = "Views",
                    tint = ColorTextOnLightSecondary,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = doc.views,
                    fontFamily = DmSansFamily,
                    fontSize = 10.sp,
                    color = ColorTextOnLightSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right Save Bookmark action
        IconButton(
            onClick = { onToggleSaved(doc.id) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (doc.isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = "Save document",
                tint = if (doc.isSaved) ColorAmber else ColorTextOnLightSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Component 7: Pagination ───────────────────────────────────────────────────
@Composable
private fun PaginationRow(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev button
        IconButton(
            onClick = { if (currentPage > 1) onPageSelected(currentPage - 1) },
            enabled = currentPage > 1,
            modifier = Modifier
                .size(34.dp)
                .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Trang trước",
                tint = if (currentPage > 1) ColorTextOnLight else ColorTextOnLightSecondary,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // First 3 Pages or current pages
        val pagesToDraw = remember(currentPage, totalPages) {
            val list = mutableListOf<Int>()
            if (totalPages <= 5) {
                for (i in 1..totalPages) list.add(i)
            } else {
                list.add(1)
                if (currentPage > 3) {
                    list.add(-1) // -1 is dot indicator
                }
                val start = maxOf(2, currentPage - 1)
                val end = minOf(totalPages - 1, currentPage + 1)
                for (i in start..end) {
                    if (!list.contains(i)) list.add(i)
                }
                if (currentPage < totalPages - 2) {
                    list.add(-2) // -2 is dot indicator
                }
                if (!list.contains(totalPages)) list.add(totalPages)
            }
            list
        }

        pagesToDraw.forEach { page ->
            if (page < 0) {
                Text(
                    text = "...",
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 12.sp,
                    color = ColorTextOnLightSecondary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            } else {
                val isSelected = currentPage == page
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ColorInk else Color.White)
                        .border(1.dp, if (isSelected) ColorInk else ColorBorder, RoundedCornerShape(8.dp))
                        .clickable { onPageSelected(page) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$page",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else ColorTextOnLight
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Next button
        IconButton(
            onClick = { if (currentPage < totalPages) onPageSelected(currentPage + 1) },
            enabled = currentPage < totalPages,
            modifier = Modifier
                .size(34.dp)
                .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Trang sau",
                tint = if (currentPage < totalPages) ColorTextOnLight else ColorTextOnLightSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

// ─── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LibraryScreenPreview() {
    EduVaultTheme {
        LibraryContent(
            paddingValues = PaddingValues(0.dp),
            onUploadClick = {}
        )
    }
}

// ─── Credit Block Dialog (Give-and-Take Rules) ──────────────────────────────────
@Composable
fun CreditBlockDialog(
    onDismiss: () -> Unit,
    onUploadClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* chặn click trượt */ }
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ColorAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧩", fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Hết lượt sử dụng tài liệu!",
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColorInk,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "EduVault áp dụng Quy tắc Đóng góp - Sử dụng công bằng (Give-and-Take).\n\nBạn cần đóng góp đăng tải 1 tài liệu học tập mới của mình để nhận ngay +1 lượt sử dụng tài liệu chất lượng khác từ cộng đồng.",
                        fontFamily = DmSansFamily,
                        fontSize = 12.5.sp,
                        color = ColorTextOnLightSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                                .background(ColorCream)
                                .clickable { onDismiss() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bỏ qua",
                                fontFamily = DmSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = ColorTextOnLightSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ColorAmber)
                                .clickable {
                                    onDismiss()
                                    onUploadClick()
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Đăng tài liệu ngay 📥",
                                fontFamily = DmSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = ColorInk
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Custom Document Viewer Dialog ──────────────────────────────────────────────
@Composable
fun DocViewerDialog(
    doc: LibraryDoc,
    readerState: DocReaderState = DocReaderState.Unlocked,
    activeContent: DocViewerTabsContent? = null,
    isLoadingContent: Boolean = false,
    contentError: String? = null,
    onRetryLoadContent: () -> Unit = {},
    onDismiss: () -> Unit,
    onReportClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onUnlockWithCredit: () -> Unit = {},
    onGenerateSummary: (suspend () -> Result<String>)? = null,
    onNoteClick: ((LibraryDoc) -> Unit)? = null
) {
    val state = readerState
    // Determine which content to show
    val displayContent = when {
        state is DocReaderState.GuestLocked -> "Bạn cần đăng nhập để xem tài liệu này."
        state is DocReaderState.NoCredits -> "Bạn đã hết credit. Hãy upload tài liệu để nhận thêm credit."
        state is DocReaderState.HasCredits -> "Sử dụng ${state.count} credit để mở khóa tài liệu này."
        isLoadingContent -> "Đang tải nội dung..."
        contentError != null -> "Lỗi: $contentError"
        activeContent != null -> activeContent.content
        else -> """
            KHOA HỌC LIỆU ĐẠI HỌC - EDUVAULT
            ----------------------------------------
            Tài liệu: ${doc.title}
            Mã môn học: ${doc.courseCode}
            Phân loại: ${doc.type.label}
            Lượt xem tích lũy: ${doc.views}
            Đánh giá: ${doc.rating} ★
            
            PHẦN 1: NỘI DUNG TỔNG QUAN
            Trong học phần này, chúng ta sẽ đi sâu tìm hiểu các khái niệm nền tảng quan trọng nhất.
            
            PHẦN 2: CÁC KHÁI NIỆM TRỌNG TÂM
            1. Định nghĩa và Bản chất
            2. Cơ cấu phân tích
            3. Case study thực tế
            
            PHẦN 3: LUYỆN TẬP & CỦNG CỐ
            Cuối mỗi chương đều đi kèm bộ câu hỏi trắc nghiệm tự luyện.
        """.trimIndent()
    }

    val isLocked = state is DocReaderState.GuestLocked || 
                   state is DocReaderState.NoCredits || 
                   state is DocReaderState.HasCredits

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* Chặn click trượt */ }
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = doc.courseCode,
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ColorAmberDark
                        )
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = "Đóng",
                            tint = ColorTextOnLightSecondary,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onDismiss() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = doc.title,
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ColorInk,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Document Content area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorCream)
                            .border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (isLoadingContent) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    color = ColorForest,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            Text(
                                text = displayContent,
                                fontFamily = DmSansFamily,
                                fontSize = 12.sp,
                                color = ColorInk,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Error + Retry
                    if (contentError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ColorAmberLight)
                                .clickable { onRetryLoadContent() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Thử lại",
                                fontFamily = DmSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ColorInk
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Locked states: show appropriate actions
                    if (isLocked) {
                        when (state) {
                            is DocReaderState.GuestLocked -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ColorForest)
                                            .clickable { onNavigateToLogin() }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Đăng nhập", fontFamily = DmSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, ColorForest, RoundedCornerShape(10.dp))
                                            .clickable { onNavigateToRegister() }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Đăng ký", fontFamily = DmSansFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ColorForest)
                                    }
                                }
                            }
                            is DocReaderState.HasCredits -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ColorForest)
                                        .clickable { onUnlockWithCredit() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Mở khóa (${state.count} credit)",
                                        fontFamily = DmSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                            is DocReaderState.NoCredits -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ColorAmber)
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Hết credit – Upload tài liệu để nhận thêm",
                                        fontFamily = DmSansFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = ColorInk
                                    )
                                }
                            }
                            else -> { /* Unlocked – nothing to show */ }
                        }
                    } else {
                        // Unlocked: show "Đã đọc xong" button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(ColorForest)
                                .clickable { onDismiss() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Đã đọc xong",
                                fontFamily = DmSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
