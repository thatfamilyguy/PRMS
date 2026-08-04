package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    selectedRole: UserRole? = null,
    onBackToRoleSelection: (() -> Unit)? = null,
    onLoginSuccess: (User) -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    var username by remember(selectedRole) { mutableStateOf("") }
    var password by remember(selectedRole) { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Password Rotation Form State
    var currentPasswordAttempt by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var rotationErrorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess((uiState as AuthUiState.Success).user)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = BentoPrimary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, BentoPrimary.copy(alpha = 0.3f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.health_ledger_emblem),
                        contentDescription = "Health Ledger Emblem",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Health Ledger",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BentoTextPrimary,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "Patient Record Management System",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Security Policy & Auto-Lock Instruction Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_instruction_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BentoContainerPurple.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoAccentLight)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Policy",
                        tint = BentoPrimaryDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "REQUIRED SECURITY POLICY",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimaryDark
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Always enter your password when re-opening the app after closing without logging out and whenever your phone locks.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextSecondary,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }



            if (selectedRole != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(selectedRole.badgeColorHex).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(selectedRole.badgeColorHex))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected Role: ${selectedRole.displayName}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(selectedRole.badgeColorHex),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (onBackToRoleSelection != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = onBackToRoleSelection,
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("change_role_button"),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = "Change Role",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BentoPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            } else if (onBackToRoleSelection != null) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onBackToRoleSelection,
                    modifier = Modifier.testTag("change_role_button")
                ) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Role", style = MaterialTheme.typography.labelMedium)
                }
            }



            Spacer(modifier = Modifier.height(20.dp))

            // Login Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "System Access",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState is AuthUiState.Error) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            color = BentoAlert.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BentoAlert)
                        ) {
                            Text(
                                text = (uiState as AuthUiState.Error).message,
                                color = BentoAlert,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Username", tint = BentoPrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary,
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedLabelColor = BentoPrimary,
                            unfocusedLabelColor = BentoTextSecondary,
                            focusedContainerColor = BentoContainerSecondary,
                            unfocusedContainerColor = BentoContainerSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = BentoPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = BentoTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextPrimary,
                            unfocusedTextColor = BentoTextPrimary,
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedLabelColor = BentoPrimary,
                            unfocusedLabelColor = BentoTextSecondary,
                            focusedContainerColor = BentoContainerSecondary,
                            unfocusedContainerColor = BentoContainerSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    Button(
                        onClick = { authViewModel.login(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        enabled = uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }

    // MANDATORY WEEKLY PASSWORD CHANGE DIALOG
    if (uiState is AuthUiState.PasswordExpired) {
        val expiredState = uiState as AuthUiState.PasswordExpired

        AlertDialog(
            onDismissRequest = { authViewModel.resetState() },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = "Password Expired",
                    tint = BentoWarning,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Weekly Password Rotation Required",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = expiredState.reason,
                        style = MaterialTheme.typography.bodyMedium.copy(color = BentoTextSecondary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (rotationErrorMsg != null) {
                        Text(
                            text = rotationErrorMsg!!,
                            style = MaterialTheme.typography.labelSmall.copy(color = BentoAlert),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = currentPasswordAttempt.ifEmpty { password },
                        onValueChange = { currentPasswordAttempt = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = confirmPasswordInput,
                        onValueChange = { confirmPasswordInput = it },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currPass = currentPasswordAttempt.ifEmpty { password }
                        if (newPasswordInput.length < 6) {
                            rotationErrorMsg = "New password must be at least 6 characters."
                            return@Button
                        }
                        if (newPasswordInput != confirmPasswordInput) {
                            rotationErrorMsg = "New passwords do not match."
                            return@Button
                        }
                        rotationErrorMsg = null
                        authViewModel.updatePassword(expiredState.user.username, currPass, newPasswordInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Update & Login")
                }
            },
            dismissButton = {
                TextButton(onClick = { authViewModel.resetState() }) {
                    Text("Cancel", color = BentoTextSecondary)
                }
            },
            containerColor = BentoSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}


