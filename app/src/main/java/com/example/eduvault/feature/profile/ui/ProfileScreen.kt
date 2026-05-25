package com.example.eduvault.feature.profile.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eduvault.core.theme.ColorAmber
import com.example.eduvault.core.theme.ColorAmberDark
import com.example.eduvault.core.theme.ColorAmberLight
import com.example.eduvault.core.theme.ColorBorder
import com.example.eduvault.core.theme.ColorCream
import com.example.eduvault.core.theme.ColorError
import com.example.eduvault.core.theme.ColorForest
import com.example.eduvault.core.theme.ColorForestLight
import com.example.eduvault.core.theme.ColorInk
import com.example.eduvault.core.theme.ColorPaper
import com.example.eduvault.core.theme.ColorTextOnDark
import com.example.eduvault.core.theme.ColorTextOnDarkSecondary
import com.example.eduvault.core.theme.ColorTextOnLight
import com.example.eduvault.core.theme.ColorTextOnLightSecondary
import com.example.eduvault.core.theme.DmSansFamily
import com.example.eduvault.core.theme.JetBrainsMonoFamily
import com.example.eduvault.core.theme.PlayfairDisplayFamily
import java.io.File

@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPaper)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ColorAmber)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(scrollState)
            ) {
                // Header Profile Info Banner
                ProfileHeaderSection(
                    uiState = uiState,
                    paddingValues = paddingValues,
                    onEditClick = viewModel::onStartEditing
                )

                // Message Alerts (Success/Error)
                AnimatedVisibility(visible = uiState.successMessage != null || uiState.errorMessage != null) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        if (uiState.successMessage != null) {
                            AlertBox(
                                message = uiState.successMessage ?: "",
                                isSuccess = true,
                                icon = Icons.Outlined.CheckCircle
                            )
                        } else if (uiState.errorMessage != null) {
                            AlertBox(
                                message = uiState.errorMessage ?: "",
                                isSuccess = false,
                                icon = Icons.Outlined.Info
                            )
                        }
                    }
                }

                if (uiState.isEditing) {
                    // ─── EDIT MODE ───
                    ProfileEditForm(
                        uiState = uiState,
                        viewModel = viewModel,
                        onSaveClick = viewModel::saveProfile,
                        onCancelClick = viewModel::onCancelEditing
                    )
                } else {
                    // ─── VIEW MODE ───
                    ProfileViewDetails(
                        uiState = uiState,
                        onLogoutClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    }

    // Confirmation Logout dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogout)
                    }
                ) {
                    Text(
                        text = "Đăng xuất",
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.Bold,
                        color = ColorError
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "Hủy bỏ",
                        fontFamily = DmSansFamily,
                        color = ColorTextOnLightSecondary
                    )
                }
            },
            title = {
                Text(
                    text = "Xác nhận đăng xuất?",
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ColorInk
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng tài liệu học tập EduVault?",
                    fontFamily = DmSansFamily,
                    fontSize = 14.sp,
                    color = ColorTextOnLightSecondary,
                    lineHeight = 18.sp
                )
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UI COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

// ─── Profile Header Section ───

@Composable
private fun ProfileHeaderSection(
    uiState: ProfileUiState,
    paddingValues: PaddingValues,
    onEditClick: () -> Unit
) {
    val user = uiState.user ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorInk)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ColorAmber.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.85f, size.height * 0.4f),
                        radius = size.width * 0.55f
                    ),
                    radius = size.width * 0.55f,
                    center = Offset(size.width * 0.85f, size.height * 0.4f)
                )
            }
            .padding(top = paddingValues.calculateTopPadding() + 16.dp, bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Profile Avatar Display
            ProfileAvatar(
                avatarUrl = user.avatarUrl,
                fullName = user.fullName,
                modifier = Modifier.size(100.dp),
                borderWidth = 3.5.dp,
                emojiSize = 48.sp,
                initialSize = 44.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // User Name
            Text(
                text = user.fullName.ifEmpty { "Gamer" },
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ColorTextOnDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = ColorTextOnDarkSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = user.email,
                    fontFamily = DmSansFamily,
                    fontSize = 12.5.sp,
                    color = ColorTextOnDarkSecondary
                )
            }

            // University Badge (if present)
            if (user.university.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorTextOnDark.copy(alpha = 0.08f))
                        .border(1.dp, ColorTextOnDark.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        tint = ColorAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = user.university,
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ColorAmber
                    )
                }
            }

            // Edit button when NOT in edit mode
            if (!uiState.isEditing) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorAmber)
                        .clickable { onEditClick() }
                        .padding(horizontal = 18.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = ColorInk,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Chỉnh sửa hồ sơ",
                            fontFamily = DmSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = ColorInk
                        )
                    }
                }
            }
        }
    }
}

// ─── Custom Avatar Loader Component ───

@Composable
fun ProfileAvatar(
    avatarUrl: String,
    fullName: String,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 3.dp,
    emojiSize: TextUnit = 32.sp,
    initialSize: TextUnit = 32.sp
) {
    val bitmap = remember(avatarUrl) {
        if (avatarUrl.isNotEmpty() && !avatarUrl.startsWith("emoji:")) {
            val file = File(avatarUrl)
            if (file.exists()) {
                try {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            } else null
        } else null
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(ColorForestLight, ColorAmber)
                )
            )
            .border(borderWidth, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Ảnh đại diện thiết bị",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val emoji = if (avatarUrl.startsWith("emoji:")) {
                avatarUrl.removePrefix("emoji:")
            } else {
                ""
            }

            if (emoji.isNotEmpty()) {
                Text(
                    text = emoji,
                    fontSize = emojiSize
                )
            } else {
                val initial = if (fullName.isNotEmpty()) fullName.first().uppercase() else "E"
                Text(
                    text = initial,
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = initialSize,
                    color = Color.White
                )
            }
        }
    }
}

// ─── View Profile Details Screen ───

@Composable
private fun ProfileViewDetails(
    uiState: ProfileUiState,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Study Stats
        Text(
            text = "Thành tích tích lũy",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ColorInk,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(emoji = "🧩", value = "18", label = "Quiz đã luyện", modifier = Modifier.weight(1f))
            StatCard(emoji = "📝", value = "42 tr", label = "Lưu trữ tài liệu", modifier = Modifier.weight(1f))
            StatCard(emoji = "⭐", value = "8.5", label = "Điểm thi đua", modifier = Modifier.weight(1f))
        }

        // Settings list
        Text(
            text = "Cài đặt & Tiện ích",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ColorInk,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                MenuSettingsItem(icon = Icons.Outlined.Settings, label = "Cấu hình tài khoản học tập")
                MenuSettingsItem(icon = Icons.Outlined.Notifications, label = "Nhắc nhở ôn thi & Luyện tập")
                MenuSettingsItem(icon = Icons.Outlined.HelpOutline, label = "Trợ giúp & Đóng góp phản hồi")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Red Logout Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, ColorError, RoundedCornerShape(12.dp))
                .clickable { onLogoutClick() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExitToApp,
                    contentDescription = null,
                    tint = ColorError,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Đăng xuất tài khoản",
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = ColorError
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = ColorInk
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = DmSansFamily,
                fontSize = 10.sp,
                color = ColorTextOnLightSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MenuSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ColorInk,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            fontFamily = DmSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = ColorTextOnLight
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ColorBorder)
    )
}

// ─── Edit Profile Form Screen ───

@Composable
private fun ProfileEditForm(
    uiState: ProfileUiState,
    viewModel: ProfileViewModel,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val context = LocalContext.current
    
    // System Image Picker launcher resultcontract
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleCustomAvatarPicked(context, uri)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Thay đổi ảnh đại diện",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ColorInk,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Custom device Picker Button
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ColorBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { launcher.launch("image/*") }
                .padding(bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = ColorAmberDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tải ảnh đại diện từ thiết bị",
                    fontFamily = DmSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ColorAmberDark
                )
            }
        }

        // Quick presets Grid
        Text(
            text = "Hoặc chọn nhanh từ bộ sưu tập nhanh:",
            fontFamily = DmSansFamily,
            fontSize = 11.5.sp,
            color = ColorTextOnLightSecondary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        val quickAvatars = listOf(
            "emoji:👨‍🎓", "emoji:👩‍🎓", "emoji:🧑‍💻", "emoji:👩‍💻",
            "emoji:🦊", "emoji:🦁", "emoji:🦄", "emoji:🐼"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            quickAvatars.chunked(4).forEach { colItems ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    colItems.forEach { avatar ->
                        val isSelected = uiState.editAvatarUrl == avatar
                        val emoji = avatar.removePrefix("emoji:")

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) ColorAmber.copy(alpha = 0.15f) else ColorCream
                                )
                                .border(
                                    2.dp,
                                    if (isSelected) ColorAmber else ColorBorder,
                                    CircleShape
                                )
                                .clickable { viewModel.onAvatarSelected(avatar) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                }
            }
        }

        // Info input Form
        Text(
            text = "Thông tin cá nhân",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ColorInk,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Email field (READ ONLY)
        OutlinedTextField(
            value = uiState.user?.email ?: "",
            onValueChange = {},
            enabled = false,
            label = { Text("Đăng nhập email (Không thể sửa)", fontFamily = DmSansFamily) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(11.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = ColorTextOnLightSecondary,
                disabledBorderColor = ColorBorder,
                disabledContainerColor = ColorCream
            )
        )

        // Full name field
        OutlinedTextField(
            value = uiState.editFullName,
            onValueChange = viewModel::onFullNameChange,
            label = { Text("Họ và tên người dùng", fontFamily = DmSansFamily) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(11.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAmber,
                unfocusedBorderColor = ColorBorder,
                focusedLabelColor = ColorAmberDark,
                unfocusedLabelColor = ColorTextOnLightSecondary
            )
        )

        // University field
        OutlinedTextField(
            value = uiState.editUniversity,
            onValueChange = viewModel::onUniversityChange,
            label = { Text("Trường đại học học tập", fontFamily = DmSansFamily) },
            leadingIcon = { Icon(imageVector = Icons.Outlined.School, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(11.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAmber,
                unfocusedBorderColor = ColorBorder,
                focusedLabelColor = ColorAmberDark,
                unfocusedLabelColor = ColorTextOnLightSecondary
            )
        )

        // Save & Cancel Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Cancel button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, ColorBorder, RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable(enabled = !uiState.isSaving) { onCancelClick() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = null,
                        tint = ColorTextOnLightSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Hủy bỏ",
                        fontFamily = DmSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        color = ColorTextOnLightSecondary
                    )
                }
            }

            // Save button
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (uiState.isSaving) ColorAmber.copy(alpha = 0.5f) else ColorAmber)
                    .clickable(enabled = !uiState.isSaving) { onSaveClick() }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = ColorInk, modifier = Modifier.size(16.dp))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null,
                            tint = ColorInk,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Lưu thay đổi",
                            fontFamily = DmSansFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = ColorInk
                        )
                    }
                }
            }
        }
    }
}

// ─── Styled Alert Box Composable ───

@Composable
private fun AlertBox(
    message: String,
    isSuccess: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val bg = if (isSuccess) Color(0xFFF0FAF5) else Color(0xFFFFF0F0)
    val border = if (isSuccess) Color(0xFFD4EDDA) else Color(0xFFFFDDDD)
    val color = if (isSuccess) ColorForest else ColorError

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, border, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                fontFamily = DmSansFamily,
                fontSize = 12.5.sp,
                color = color,
                lineHeight = 16.sp
            )
        }
    }
}
