package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrmsTopBar(
    currentUsername: String,
    currentRole: UserRole,
    onLogoutClick: () -> Unit
) {
    Surface(
        color = BentoBackground,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(BentoPrimary, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "PRMS Medical Security Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PRMS Medical",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary,
                                fontSize = 17.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "SQLCipher Encryption Active",
                                tint = BentoPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "SQLCipher 256-bit • Bento Grid",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BentoTextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currentUsername,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BentoTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        RoleBadge(role = currentRole)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = onLogoutClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = BentoContainerPurple,
                            contentColor = BentoPrimaryDark
                        ),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Lock / Logout Session",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleBadge(role: UserRole) {
    Surface(
        color = BentoContainerPurple,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoAccentLight)
    ) {
        Text(
            text = role.displayName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = BentoPrimaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        )
    }
}

