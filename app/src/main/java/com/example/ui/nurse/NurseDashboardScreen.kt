package com.example.ui.nurse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Patient
import com.example.data.model.User
import com.example.data.model.Vitals
import com.example.data.model.VitalStatus
import com.example.ui.components.PrmsTopBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseDashboardScreen(
    currentUser: User,
    viewModel: NurseDashboardViewModel,
    onLogout: () -> Unit
) {
    val patients by viewModel.patients.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showIntakeDialog by remember { mutableStateOf(false) }
    var selectedPatientForVitals by remember { mutableStateOf<Patient?>(null) }
    var selectedPatientForHistory by remember { mutableStateOf<Patient?>(null) }

    Scaffold(
        topBar = {
            PrmsTopBar(
                currentUsername = currentUser.fullName,
                currentRole = currentUser.role,
                onLogoutClick = onLogout
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showIntakeDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Patient Intake") },
                text = { Text("Patient Intake") },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("patient_intake_fab")
            )
        },
        containerColor = BentoBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Clinical Ward Operations",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    )
                    Text(
                        text = "Patient Intake, Vitals Monitoring & Immediate Needs",
                        style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                    )
                }
            }

            // Real-time Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search by Patient Name, MRN, or Room #...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BentoPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = BentoTextSecondary)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("patient_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BentoTextPrimary,
                    unfocusedTextColor = BentoTextPrimary,
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder,
                    focusedContainerColor = BentoSurface,
                    unfocusedContainerColor = BentoSurface
                ),
                shape = RoundedCornerShape(20.dp)
            )

            // Patient List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(patients) { patient ->
                    PatientClinicalCard(
                        patient = patient,
                        onAddVitalsClick = { selectedPatientForVitals = patient },
                        onViewHistoryClick = { selectedPatientForHistory = patient }
                    )
                }
            }
        }
    }

    if (showIntakeDialog) {
        RegisterPatientDialog(
            currentUser = currentUser,
            onDismiss = { showIntakeDialog = false },
            onSave = { newPatient ->
                viewModel.registerPatient(currentUser, newPatient) { res ->
                    if (res.isSuccess) showIntakeDialog = false
                }
            }
        )
    }

    if (selectedPatientForVitals != null) {
        AddVitalsDialog(
            patient = selectedPatientForVitals!!,
            currentUser = currentUser,
            onDismiss = { selectedPatientForVitals = null },
            onSaveVitals = { vitals ->
                viewModel.recordVitals(currentUser, vitals) { res ->
                    if (res.isSuccess) selectedPatientForVitals = null
                }
            }
        )
    }

    if (selectedPatientForHistory != null) {
        PatientHistoryDialog(
            patient = selectedPatientForHistory!!,
            viewModel = viewModel,
            onDismiss = { selectedPatientForHistory = null }
        )
    }
}

@Composable
fun PatientClinicalCard(
    patient: Patient,
    onAddVitalsClick: () -> Unit,
    onViewHistoryClick: () -> Unit
) {
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
                            .size(42.dp)
                            .background(
                                if (patient.status == "ICU") BentoAlert.copy(alpha = 0.15f) else BentoContainerPurple,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (patient.status == "ICU") Icons.Default.Warning else Icons.Default.Person,
                            contentDescription = null,
                            tint = if (patient.status == "ICU") BentoAlert else BentoPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        )
                        Text(
                            text = "${patient.mrn} • ${patient.gender}, ${patient.dob}",
                            style = MaterialTheme.typography.bodySmall.copy(color = BentoTextSecondary)
                        )
                    }
                }

                Surface(
                    color = if (patient.status == "ICU") BentoAlert.copy(alpha = 0.15f) else BentoContainerPurple,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (patient.status == "ICU") BentoAlert else BentoAccentLight)
                ) {
                    Text(
                        text = patient.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (patient.status == "ICU") BentoAlert else BentoPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BentoBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Room / Location", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                    Text(patient.roomNumber, style = MaterialTheme.typography.bodyMedium.copy(color = BentoTextPrimary, fontWeight = FontWeight.SemiBold))
                }
                Column {
                    Text("Blood Type", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                    Text(patient.bloodType, style = MaterialTheme.typography.bodyMedium.copy(color = BentoPrimary, fontWeight = FontWeight.Bold))
                }
                Column {
                    Text("Attending Doctor", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                    Text(patient.attendingPhysician, style = MaterialTheme.typography.bodyMedium.copy(color = BentoTextPrimary))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Diagnosis: ${patient.primaryDiagnosis}", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextPrimary))
            Text("Allergies: ${patient.allergies}", style = MaterialTheme.typography.bodySmall.copy(color = BentoAlert))

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddVitalsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Record Vitals", style = MaterialTheme.typography.labelMedium)
                }

                OutlinedButton(
                    onClick = onViewHistoryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BentoPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vitals History", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPatientDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSave: (Patient) -> Unit
) {
    var mrn by remember { mutableStateOf("MRN-${(10000..99999).random()}") }
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("1985-06-15") }
    var gender by remember { mutableStateOf("Female") }
    var bloodType by remember { mutableStateOf("O+") }
    var room by remember { mutableStateOf("204-A") }
    var diagnosis by remember { mutableStateOf("") }
    var physician by remember { mutableStateOf("Dr. Harrison") }
    var allergies by remember { mutableStateOf("None Known") }
    var status by remember { mutableStateOf("Admitted") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Patient Intake Form", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        containerColor = BentoSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = mrn, onValueChange = { mrn = it }, label = { Text("MRN") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Patient Name") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
                OutlinedTextField(value = room, onValueChange = { room = it }, label = { Text("Room / Bed #") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
                OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it }, label = { Text("Primary Medical Diagnosis") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
                OutlinedTextField(value = allergies, onValueChange = { allergies = it }, label = { Text("Known Allergies") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
                OutlinedTextField(value = physician, onValueChange = { physician = it }, label = { Text("Attending Physician") }, shape = RoundedCornerShape(14.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onSave(
                            Patient(
                                mrn = mrn,
                                fullName = fullName,
                                dob = dob,
                                gender = gender,
                                bloodType = bloodType,
                                roomNumber = room,
                                primaryDiagnosis = diagnosis,
                                attendingPhysician = physician,
                                allergies = allergies,
                                status = status
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Complete Intake")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = BentoTextSecondary) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVitalsDialog(
    patient: Patient,
    currentUser: User,
    onDismiss: () -> Unit,
    onSaveVitals: (Vitals) -> Unit
) {
    var hr by remember { mutableStateOf("76") }
    var bp by remember { mutableStateOf("120/80") }
    var spo2 by remember { mutableStateOf("98") }
    var temp by remember { mutableStateOf("36.8") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Vitals for ${patient.fullName}", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        containerColor = BentoSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = hr,
                    onValueChange = { hr = it },
                    label = { Text("Heart Rate (BPM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = bp,
                    onValueChange = { bp = it },
                    label = { Text("Blood Pressure (e.g. 120/80)") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = spo2,
                    onValueChange = { spo2 = it },
                    label = { Text("SpO2 % Oxygen Saturation") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Nurse Observation Notes") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = BentoTextPrimary, unfocusedTextColor = BentoTextPrimary, focusedContainerColor = BentoContainerSecondary, unfocusedContainerColor = BentoContainerSecondary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val vital = Vitals(
                        patientId = patient.id,
                        heartRate = hr.toIntOrNull() ?: 75,
                        bloodPressure = if (bp.isBlank()) "120/80" else bp,
                        spo2 = spo2.toIntOrNull() ?: 98,
                        temperatureC = temp.toDoubleOrNull() ?: 36.8,
                        nurseNotes = notes,
                        recordedBy = currentUser.fullName
                    )
                    onSaveVitals(vital)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Vitals")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = BentoTextSecondary) }
        }
    )
}

@Composable
fun PatientHistoryDialog(
    patient: Patient,
    viewModel: NurseDashboardViewModel,
    onDismiss: () -> Unit
) {
    val vitalsHistory by viewModel.getVitalsForPatient(patient.id).collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Vitals History - ${patient.fullName}", color = BentoTextPrimary, fontWeight = FontWeight.Bold) },
        containerColor = BentoSurface,
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(vitalsHistory) { v ->
                    val status = v.getVitalStatus()
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoContainerSecondary),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(dateFormat.format(Date(v.timestamp)), style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                                Surface(
                                    color = Color(status.colorHex).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(status.label, color = Color(status.colorHex), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("HR: ${v.heartRate} bpm • BP: ${v.bloodPressure} • SpO2: ${v.spo2}% • Temp: ${v.temperatureC}°C", style = MaterialTheme.typography.bodySmall.copy(color = BentoTextPrimary))
                            if (v.nurseNotes.isNotBlank()) {
                                Text("Notes: ${v.nurseNotes}", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                            }
                            Text("By: ${v.recordedBy}", style = MaterialTheme.typography.labelSmall.copy(color = BentoTextSecondary))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = BentoPrimary) }
        }
    )
}

