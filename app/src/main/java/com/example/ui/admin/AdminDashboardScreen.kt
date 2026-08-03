package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.PrmsTopBar
import com.example.ui.components.RoleBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentUser: User,
    viewModel: AdminDashboardViewModel,
    onLogout: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PrmsTopBar(
                currentUsername = currentUser.fullName,
                currentRole = currentUser.role,
                onLogoutClick = onLogout
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddUserDialog = true },
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add Staff Account") },
                    text = { Text("Add User") },
                    containerColor = BentoPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("add_user_fab")
                )
            }
        },
        containerColor = BentoBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Separation of Duties Security Policy Banner
            Surface(
                color = BentoAlert.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoAlert),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Separation of Duties Policy",
                        tint = BentoAlert,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SEPARATION OF DUTIES POLICY (ACTIVE)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoAlert,
                                fontSize = 11.sp
                            )
                        )
                        Text(
                            text = "Admin role manages system security, users & audit logs. Direct medical record modification is restricted.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BentoTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BentoSurface,
                contentColor = BentoPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BentoPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("User Management (${users.size})", color = if (selectedTab == 0) BentoPrimary else BentoTextSecondary) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Audit Logs (${auditLogs.size})", color = if (selectedTab == 1) BentoPrimary else BentoTextSecondary) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Security Config", color = if (selectedTab == 2) BentoPrimary else BentoTextSecondary) }
                )
            }

            when (selectedTab) {
                0 -> UserManagementTab(users = users, currentUser = currentUser, onToggleStatus = { target ->
                    viewModel.toggleUserActiveState(currentUser, target)
                })
                1 -> AuditLogsTab(auditLogs = auditLogs)
                2 -> SecurityConfigTab()
            }
        }
    }

    if (showAddUserDialog) {
        AddUserDialog(
            currentUser = currentUser,
            onDismiss = { showAddUserDialog = false },
            onUserCreated = { username, name, role, email, dept, pass ->
                viewModel.createUser(currentUser, username, name, role, email, dept, pass) { res ->
                    if (res.isSuccess) showAddUserDialog = false
                }
            }
        )
    }
}

@Composable
fun UserManagementTab(
    users: List<User>,
    currentUser: User,
    onToggleStatus: (User) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(users) { user ->
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(BentoContainerPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (user.role) {
                                    UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    UserRole.MANAGER -> Icons.Default.Analytics
                                    UserRole.NURSE -> Icons.Default.MedicalServices
                                },
                                contentDescription = null,
                                tint = BentoPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                RoleBadge(role = user.role)
                            }
                            Text(
                                text = "@${user.username} • Dept: ${user.department}",
                                style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary)
                            )
                        }
                    }

                    if (user.id != currentUser.id) {
                        IconButton(onClick = { onToggleStatus(user) }) {
                            Icon(
                                imageVector = if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = "Toggle Active Status",
                                tint = if (user.isActive) BentoSuccess else BentoAlert
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogsTab(auditLogs: List<AuditLog>) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(auditLogs) { log ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when (log.securityLevel) {
                        "SECURITY_ALERT" -> BentoAlert.copy(alpha = 0.08f)
                        "WARNING" -> BentoWarning.copy(alpha = 0.08f)
                        else -> BentoSurface
                    }
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (log.securityLevel) {
                                    "SECURITY_ALERT" -> Icons.Default.Warning
                                    "WARNING" -> Icons.Default.ErrorOutline
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when (log.securityLevel) {
                                    "SECURITY_ALERT" -> BentoAlert
                                    "WARNING" -> BentoWarning
                                    else -> BentoPrimary
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log.actionType,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            )
                        }
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = log.details,
                        style = MaterialTheme.typography.bodySmall.copy(color = BentoTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Actor: ${log.actorUsername} (${log.actorRole.displayName})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BentoTextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityConfigTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = BentoPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Database Encryption Strategy", style = MaterialTheme.typography.titleMedium.copy(color = BentoTextPrimary, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Engine: SQLCipher (net.zetetic:android-database-sqlcipher)\n• Cipher: 256-bit AES GCM\n• Master Key Storage: Android Keystore + EncryptedSharedPreferences\n• Status: ENCRYPTED & ACTIVE", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BentoSurface),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Password, contentDescription = null, tint = BentoSuccess)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Credential Security Policy", style = MaterialTheme.typography.titleMedium.copy(color = BentoTextPrimary, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Hashing Algorithm: PBKDF2WithHmacSHA256\n• Salt Generation: 16-byte Cryptographic Secure Random Salt\n• Iteration Count: 10,000 Rounds\n• Plaintext Passwords: NEVER STORED", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onUserCreated: (String, String, UserRole, String, String, String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dept by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.NURSE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Staff Account", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        containerColor = BentoSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = dept,
                    onValueChange = { dept = it },
                    label = { Text("Department") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Initial Password") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                Text("Assigned Role:", color = BentoTextSecondary, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UserRole.values().forEach { r ->
                        FilterChip(
                            selected = selectedRole == r,
                            onClick = { selectedRole = r },
                            label = { Text(r.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUserCreated(username, name, selectedRole, email, dept, password) },
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = BentoTextSecondary) }
        }
    )
}

