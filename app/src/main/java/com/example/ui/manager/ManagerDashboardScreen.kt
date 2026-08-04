package com.example.ui.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.ComposableMpBarChart
import com.example.ui.components.ComposableMpPieChart
import com.example.ui.components.PrmsTopBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerDashboardScreen(
    currentUser: User,
    viewModel: ManagerDashboardViewModel,
    onLogout: () -> Unit
) {
    val patients by viewModel.patients.collectAsState()
    val vitals by viewModel.recentVitals.collectAsState()

    val icuCount = remember(patients) { patients.count { it.status == "ICU" } }
    val totalPatients = remember(patients) { patients.size }
    val warningVitalsCount = remember(vitals) {
        vitals.count { it.getVitalStatus() != com.example.data.model.VitalStatus.NORMAL }
    }

    Scaffold(
        topBar = {
            PrmsTopBar(
                currentUsername = currentUser.fullName,
                currentRole = currentUser.role,
                onLogoutClick = onLogout
            )
        },
        containerColor = BentoBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bento Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Executive Operations Analytics",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                    Text(
                        text = "Hospital Planning, Patient Trends & Resource Metrics",
                        style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                    )
                }

                Surface(
                    color = BentoContainerPurple,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoAccentLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Audited Data", style = MaterialTheme.typography.labelSmall.copy(color = BentoPrimaryDark, fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Bento Stat Cards Grid (Scrollable horizontally)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ManagerStatCard(
                    title = "Active Census",
                    value = "$totalPatients Patients",
                    icon = Icons.Default.People,
                    bgColor = BentoContainerPurple,
                    tintColor = BentoPrimary,
                    modifier = Modifier.width(180.dp)
                )
                ManagerStatCard(
                    title = "ICU Occupancy",
                    value = "$icuCount Beds",
                    icon = Icons.Default.AirlineSeatFlat,
                    bgColor = BentoAlert.copy(alpha = 0.1f),
                    tintColor = BentoAlert,
                    modifier = Modifier.width(180.dp)
                )
                ManagerStatCard(
                    title = "Vitals Alerts",
                    value = "$warningVitalsCount Alerts",
                    icon = Icons.Default.Warning,
                    bgColor = BentoWarning.copy(alpha = 0.1f),
                    tintColor = BentoWarning,
                    modifier = Modifier.width(180.dp)
                )
            }

            // MPAndroidChart Bento Tile 1: Intake Trends
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BentoContainerPurple, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Weekly Patient Admissions Trend",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            )
                        }
                        Text("MPAndroidChart", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ComposableMpBarChart(
                        labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                        values = listOf(14f, 22f, 18f, 29f, 25f, 31f, 19f),
                        chartTitle = "Admissions per Day"
                    )
                }
            }

            // MPAndroidChart Bento Tile 2: Department Allocation
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BentoContainerPurple, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Department Resource Allocation",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            )
                        }
                        Text("Live Share %", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    ComposableMpPieChart(
                        entries = listOf(
                            "Cardiology" to 35f,
                            "ICU" to 25f,
                            "Emergency" to 20f,
                            "Orthopedics" to 12f,
                            "Endocrinology" to 8f
                        ),
                        colorsHex = listOf("#6750A4", "#B91C1C", "#B45309", "#006A6A", "#9333EA")
                    )
                }
            }

            // Hospital Planning Action Bento Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoContainerSecondary),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Manager Resource Planning Insights",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• ICU bed occupancy stands at 80% threshold. Recommend allocating 2 additional ventilator units.", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Cardiology patient volume increased by +18% over previous 7-day period.", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Clinical staffing ratio: 1 Nurse per 4 Patients across active wards.", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary))
                }
            }
        }
    }
}

@Composable
fun ManagerStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tintColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = BentoTextPrimary))
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary, fontSize = 11.sp))
        }
    }
}

