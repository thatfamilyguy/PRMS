package com.example.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onRoleSelectedForLogin: (UserRole) -> Unit,
    onInstantDemoLogin: (UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf<UserRole>(UserRole.NURSE) }

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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header Logo & System Information
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = BentoPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.5.dp, BentoPrimary.copy(alpha = 0.3f))
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

            Text(
                text = "Health Ledger",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BentoTextPrimary,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = "Select your role to access your clinical workspace",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BentoTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selection Cards Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                RoleSelectionCard(
                    role = UserRole.NURSE,
                    title = "Clinical Nurse",
                    subtitle = "Ward Ops • Patient Intake • Vitals Recording",
                    description = "Record real-time patient vitals, handle emergency ICU ward intakes, and view diagnostic history.",
                    icon = Icons.Default.MedicalServices,
                    isSelected = selectedRole == UserRole.NURSE,
                    testTag = "role_card_nurse",
                    onSelect = { selectedRole = UserRole.NURSE }
                )

                RoleSelectionCard(
                    role = UserRole.ADMIN,
                    title = "System Administrator",
                    subtitle = "RBAC Security • User Management • Audit Logs",
                    description = "Manage staff credentials, enforce Separation of Duties security, and inspect tamper-proof system logs.",
                    icon = Icons.Default.AdminPanelSettings,
                    isSelected = selectedRole == UserRole.ADMIN,
                    testTag = "role_card_admin",
                    onSelect = { selectedRole = UserRole.ADMIN }
                )

                RoleSelectionCard(
                    role = UserRole.MANAGER,
                    title = "Hospital Manager",
                    subtitle = "Bed Occupancy • Clinical Analytics • Duty Roster",
                    description = "Monitor hospital capacity, track department analytics, and manage shift schedules.",
                    icon = Icons.Default.Assessment,
                    isSelected = selectedRole == UserRole.MANAGER,
                    testTag = "role_card_manager",
                    onSelect = { selectedRole = UserRole.MANAGER }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Action Buttons
            Button(
                onClick = { onRoleSelectedForLogin(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("continue_to_login_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Proceed to Login Screen (${selectedRole.displayName})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    role: UserRole,
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onSelect: () -> Unit
) {
    val roleColor = Color(role.badgeColorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag(testTag),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BentoSurface else BentoSurface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) roleColor else BentoBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(roleColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = roleColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )

                    if (isSelected) {
                        Surface(
                            color = roleColor,
                            shape = CircleShape,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Role",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = roleColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BentoTextSecondary,
                        fontSize = 12.sp
                    )
                )


            }
        }
    }
}
