package com.example.eduvault.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eduvault.R
import com.example.eduvault.core.theme.ColorAmber
import com.example.eduvault.core.theme.ColorAmberDark
import com.example.eduvault.core.theme.ColorAmberLight
import com.example.eduvault.core.theme.ColorBorder
import com.example.eduvault.core.theme.ColorCream
import com.example.eduvault.core.theme.ColorError
import com.example.eduvault.core.theme.ColorForest
import com.example.eduvault.core.theme.ColorInk
import com.example.eduvault.core.theme.ColorInkLight
import com.example.eduvault.core.theme.ColorPaper
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
fun LoginScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Điều hướng khi login thành công
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onNavigateToHome()
        }
    }

    // Hiện snackbar khi có lỗi
    LaunchedEffect(uiState.loginError) {
        uiState.loginError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.onDismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ColorInk,
    ) { paddingValues ->
        LoginContent(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
            onLoginClick = viewModel::onLoginClick,
            onForgotPasswordClick = { /* TODO: navigate to forgot password */ },
            onRegisterClick = onNavigateToRegister,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

// ─── Layout Content ───────────────────────────────────────────────────────────

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // ── Left Panel: Dark background với glow effect ────────────────────
        LeftBrandPanel(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.42f)
                .align(Alignment.CenterStart)
        )

        // ── Right Panel: Form đăng nhập ────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.58f)
                .align(Alignment.CenterEnd)
                .clip(
                    RoundedCornerShape(
                        topStart = 28.dp,
                        bottomStart = 28.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(ColorCream)
        ) {
            LoginFormPanel(
                uiState = uiState,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                onLoginClick = onLoginClick,
                onForgotPasswordClick = onForgotPasswordClick,
                onRegisterClick = onRegisterClick,
            )
        }
    }
}

// ─── Left Brand Panel ─────────────────────────────────────────────────────────

@Composable
private fun LeftBrandPanel(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .background(ColorInk)
            .drawBehind {
                // Radial glow màu hổ phách ở giữa-trên
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ColorAmber.copy(alpha = glowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.55f, size.height * 0.28f),
                        radius = size.width * 1.1f
                    ),
                    radius = size.width * 1.1f,
                    center = Offset(size.width * 0.55f, size.height * 0.28f),
                )
                // Secondary glow xanh lá ở góc dưới
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ColorForest.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.2f, size.height * 0.85f),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(size.width * 0.2f, size.height * 0.85f),
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-20).dp, y = (-120).dp)
                .clip(CircleShape)
                .border(1.dp, ColorAmber.copy(alpha = 0.18f), CircleShape)
                .background(Color.Transparent)
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 30.dp, y = (-160).dp)
                .clip(CircleShape)
                .border(1.dp, ColorAmber.copy(alpha = 0.10f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(x = (-40).dp, y = 140.dp)
                .clip(CircleShape)
                .border(1.dp, ColorForest.copy(alpha = 0.22f), CircleShape)
        )

        // Brand content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ColorAmber, ColorAmberDark)
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "E",
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = ColorInk,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App name với JetBrains Mono style tag
            Text(
                text = "EduVault",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = ColorAmber,
                letterSpacing = 3.sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline với Playfair Display (tạo điểm nhấn nghệ thuật)
            Text(
                text = buildAnnotatedString {
                    append("Nơi lưu giữ\n")
                    withStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic,
                            color = ColorAmber,
                            fontFamily = PlayfairDisplayFamily,
                        )
                    ) {
                        append("mọi kiến thức")
                    }
                },
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                color = ColorTextOnDark,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Thư viện học liệu thông minh giúp bạn ôn tập và nắm vững từng khái niệm.",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = ColorTextOnDarkSecondary,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Stats row với JetBrains Mono
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BrandStat(number = "500+", label = "Tài liệu")
                BrandStat(number = "12K+", label = "Sinh viên")
            }
        }
    }
}

@Composable
private fun BrandStat(number: String, label: String) {
    Column {
        Text(
            text = number,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = ColorAmber,
        )
        Text(
            text = label,
            fontFamily = DmSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            color = ColorTextOnDarkSecondary,
        )
    }
}

// ─── Right Form Panel ─────────────────────────────────────────────────────────

@Composable
private fun LoginFormPanel(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        // Heading
        Text(
            text = "Chào mừng",
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = ColorInk,
            lineHeight = 34.sp,
        )
        Text(
            text = buildAnnotatedString {
                append("trở ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = ColorAmberDark)) {
                    append("lại")
                }
            },
            fontFamily = PlayfairDisplayFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = ColorInk,
            lineHeight = 34.sp,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Đăng nhập để tiếp tục học tập",
            fontFamily = DmSansFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = ColorTextOnLightSecondary,
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Email Field
        LoginTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "email@example.com",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        LoginTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = "Mật khẩu",
            placeholder = "Nhập mật khẩu",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible)
                            Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (uiState.isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onLoginClick()
                }
            ),
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
        )

        // Quên mật khẩu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Quên mật khẩu?",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = ColorAmberDark,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onForgotPasswordClick() }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Nút đăng nhập
        LoginButton(
            isLoading = uiState.isLoading,
            onClick = {
                focusManager.clearFocus()
                onLoginClick()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = ColorBorder,
                thickness = 1.dp
            )
            Text(
                text = "  Hoặc  ",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = ColorTextOnLightSecondary,
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = ColorBorder,
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google button
        GoogleSignInButton(onClick = { /* TODO: Google Sign In */ })

        Spacer(modifier = Modifier.height(24.dp))

        // Link đăng ký
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Chưa có tài khoản? ",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = ColorTextOnLightSecondary,
            )
            Text(
                text = "Đăng ký ngay",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = ColorAmberDark,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onRegisterClick() }
            )
        }
    }
}

// ─── Reusable Components ──────────────────────────────────────────────────────

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column {
        Text(
            text = label,
            fontFamily = DmSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isError) ColorError
            else if (isFocused) ColorAmberDark
            else ColorTextOnLightSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = DmSansFamily,
                    fontSize = 14.sp,
                    color = ColorBorder,
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            interactionSource = interactionSource,
            singleLine = true,
            shape = RoundedCornerShape(11.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAmber,
                unfocusedBorderColor = ColorBorder,
                errorBorderColor = ColorError,
                focusedLeadingIconColor = ColorAmber,
                unfocusedLeadingIconColor = ColorTextOnLightSecondary,
                errorLeadingIconColor = ColorError,
                focusedTrailingIconColor = ColorAmberDark,
                unfocusedTrailingIconColor = ColorTextOnLightSecondary,
                focusedTextColor = ColorTextOnLight,
                unfocusedTextColor = ColorTextOnLight,
                cursorColor = ColorAmber,
                errorTextColor = ColorTextOnLight,
                errorCursorColor = ColorError,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = ColorPaper,
                errorContainerColor = Color.White,
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: "",
                fontFamily = DmSansFamily,
                fontSize = 11.sp,
                color = ColorError,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val buttonScale by animateFloatAsState(
        targetValue = if (isLoading) 0.97f else 1f,
        animationSpec = tween(150),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .graphicsLayer { scaleX = buttonScale; scaleY = buttonScale },
        shape = RoundedCornerShape(11.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorAmber,
            contentColor = ColorInk,
            disabledContainerColor = ColorAmberLight.copy(alpha = 0.7f),
            disabledContentColor = ColorInk.copy(alpha = 0.5f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = ColorInk,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Đăng nhập",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                letterSpacing = 0.3.sp,
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(11.dp))
            .border(1.5.dp, ColorBorder, RoundedCornerShape(11.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            // Google "G" icon bằng text (không cần icon file)
            Text(
                text = "G",
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF4285F4),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Tiếp tục với Google",
                fontFamily = DmSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = ColorTextOnLight,
            )
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 400, heightDp = 820)
@Composable
private fun LoginScreenPreview() {
    EduVaultTheme(darkTheme = false) {
        LoginContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
        )
    }
}
