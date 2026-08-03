package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.security.SecurityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PrmsRepository private constructor(
    private val db: AppDatabase,
    private val securityManager: SecurityManager
) {
    val userDao = db.userDao()
    val patientDao = db.patientDao()
    val vitalsDao = db.vitalsDao()
    val auditLogDao = db.auditLogDao()

    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()
    val recentVitals: Flow<List<Vitals>> = vitalsDao.getRecentVitals()
    val auditLogs: Flow<List<AuditLog>> = auditLogDao.getAllAuditLogs()

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (userDao.getUserCount() == 0) {
            // Seed Admin User
            val adminSalt = securityManager.generateSalt()
            val adminHash = securityManager.hashPassword("AdminPassword123!", adminSalt)
            val adminUser = User(
                username = "admin",
                passwordHash = adminHash,
                salt = adminSalt,
                fullName = "Dr. Alexander Wright",
                role = UserRole.ADMIN,
                email = "admin@prms-hospital.org",
                department = "System Security & Admin"
            )
            userDao.insertUser(adminUser)

            // Seed Manager User
            val managerSalt = securityManager.generateSalt()
            val managerHash = securityManager.hashPassword("ManagerPassword123!", managerSalt)
            val managerUser = User(
                username = "manager",
                passwordHash = managerHash,
                salt = managerSalt,
                fullName = "Sarah Jenkins, MHA",
                role = UserRole.MANAGER,
                email = "manager@prms-hospital.org",
                department = "Hospital Operations & Planning"
            )
            userDao.insertUser(managerUser)

            // Seed Nurse User
            val nurseSalt = securityManager.generateSalt()
            val nurseHash = securityManager.hashPassword("NursePassword123!", nurseSalt)
            val nurseUser = User(
                username = "nurse",
                passwordHash = nurseHash,
                salt = nurseSalt,
                fullName = "Nurse Clara Barton, RN",
                role = UserRole.NURSE,
                email = "nurse.clara@prms-hospital.org",
                department = "Emergency & Clinical Care"
            )
            userDao.insertUser(nurseUser)

            // Log System Seeding
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = "SYSTEM",
                    actorRole = UserRole.ADMIN,
                    actionType = "SYSTEM_INIT",
                    details = "SQLCipher encrypted database initialized with seed RBAC accounts & Salted Hashes."
                )
            )
        }

        if (patientDao.getPatientCount() == 0) {
            val samplePatients = listOf(
                Patient(
                    mrn = "MRN-84920",
                    fullName = "Eleanor Vance",
                    dob = "1978-05-14",
                    gender = "Female",
                    bloodType = "O+",
                    roomNumber = "ICU-102",
                    primaryDiagnosis = "Acute Coronary Syndrome",
                    attendingPhysician = "Dr. Harrison",
                    allergies = "Penicillin",
                    status = "ICU",
                    department = "Cardiology"
                ),
                Patient(
                    mrn = "MRN-91024",
                    fullName = "Marcus Aurelius Reed",
                    dob = "1965-11-20",
                    gender = "Male",
                    bloodType = "A-",
                    roomNumber = "304-B",
                    primaryDiagnosis = "Post-Op Knee Replacement",
                    attendingPhysician = "Dr. Miller",
                    allergies = "Sulfa",
                    status = "Admitted",
                    department = "Orthopedics"
                ),
                Patient(
                    mrn = "MRN-77312",
                    fullName = "Sophia Chen",
                    dob = "1992-03-08",
                    gender = "Female",
                    bloodType = "B+",
                    roomNumber = "ER-04",
                    primaryDiagnosis = "Respiratory Distress / Asthma",
                    attendingPhysician = "Dr. Patel",
                    allergies = "None Known",
                    status = "Observation",
                    department = "Emergency"
                ),
                Patient(
                    mrn = "MRN-65899",
                    fullName = "Robert Sterling",
                    dob = "1954-09-12",
                    gender = "Male",
                    bloodType = "AB+",
                    roomNumber = "ICU-108",
                    primaryDiagnosis = "Sepsis / Pneumonia",
                    attendingPhysician = "Dr. Harrison",
                    allergies = "Latex, Aspirin",
                    status = "ICU",
                    department = "Intensive Care"
                ),
                Patient(
                    mrn = "MRN-33109",
                    fullName = "Amara Lawson",
                    dob = "1989-12-01",
                    gender = "Female",
                    bloodType = "O-",
                    roomNumber = "210-A",
                    primaryDiagnosis = "Type 1 Diabetes Ketoacidosis",
                    attendingPhysician = "Dr. Garcia",
                    allergies = "Codeine",
                    status = "Admitted",
                    department = "Endocrinology"
                )
            )

            for (p in samplePatients) {
                val pId = patientDao.insertPatient(p)
                // Add initial vitals
                vitalsDao.insertVitals(
                    Vitals(
                        patientId = pId,
                        heartRate = if (p.status == "ICU") 118 else 75,
                        bloodPressure = if (p.status == "ICU") "145/95" else "120/80",
                        spo2 = if (p.status == "ICU") 92 else 98,
                        temperatureC = if (p.status == "ICU") 38.4 else 36.8,
                        nurseNotes = "Initial baseline vitals recorded upon admission.",
                        recordedBy = "Nurse Clara Barton, RN"
                    )
                )
            }
        }
    }

    suspend fun authenticateUser(username: String, passwordAttempt: String): Result<User> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username.trim())
            ?: return@withContext Result.failure(Exception("Invalid username or password."))

        if (!user.isActive) {
            return@withContext Result.failure(Exception("Account is deactivated. Contact System Admin."))
        }

        val isValid = securityManager.verifyPassword(passwordAttempt, user.salt, user.passwordHash)
        if (isValid) {
            securityManager.saveActiveSession(user.id, user.username, user.role.name)
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = user.username,
                    actorRole = user.role,
                    actionType = "LOGIN_SUCCESS",
                    details = "User logged in with password authentication. RBAC role: ${user.role.displayName}"
                )
            )
            Result.success(user)
        } else {
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = username,
                    actorRole = UserRole.NURSE,
                    actionType = "LOGIN_FAILED",
                    details = "Failed password authentication attempt for username: $username",
                    securityLevel = "WARNING"
                )
            )
            Result.failure(Exception("Invalid username or password."))
        }
    }

    suspend fun authenticateWithBiometrics(username: String): Result<User> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username.trim())
            ?: return@withContext Result.failure(Exception("User account not found for biometric login."))

        securityManager.saveActiveSession(user.id, user.username, user.role.name)
        auditLogDao.insertAuditLog(
            AuditLog(
                actorUsername = user.username,
                actorRole = user.role,
                actionType = "BIOMETRIC_AUTH_SUCCESS",
                details = "User passed AndroidX Biometric verification. Logged into ${user.role.displayName}"
            )
        )
        Result.success(user)
    }

    suspend fun createUser(
        creator: User,
        newUsername: String,
        newFullName: String,
        newRole: UserRole,
        newEmail: String,
        newDept: String,
        newPassword: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (creator.role != UserRole.ADMIN) {
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = creator.username,
                    actorRole = creator.role,
                    actionType = "UNAUTHORIZED_ATTEMPT",
                    details = "Non-Admin user attempted to create a staff account.",
                    securityLevel = "SECURITY_ALERT"
                )
            )
            return@withContext Result.failure(SecurityException("Separation of Duties Policy: Only Admin can create accounts."))
        }

        val existing = userDao.getUserByUsername(newUsername)
        if (existing != null) {
            return@withContext Result.failure(Exception("Username already exists."))
        }

        val salt = securityManager.generateSalt()
        val hash = securityManager.hashPassword(newPassword, salt)
        val newUser = User(
            username = newUsername,
            passwordHash = hash,
            salt = salt,
            fullName = newFullName,
            role = newRole,
            email = newEmail,
            department = newDept
        )

        val id = userDao.insertUser(newUser)
        auditLogDao.insertAuditLog(
            AuditLog(
                actorUsername = creator.username,
                actorRole = creator.role,
                actionType = "USER_CREATED",
                details = "Admin created account: $newUsername (${newRole.displayName}, Dept: $newDept)"
            )
        )
        Result.success(id)
    }

    suspend fun registerPatient(
        actor: User,
        patient: Patient
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (actor.role == UserRole.ADMIN) {
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = actor.username,
                    actorRole = actor.role,
                    actionType = "PRIVILEGE_RESTRICTED",
                    details = "Admin blocked from direct patient intake (Separation of Duties Policy).",
                    securityLevel = "WARNING"
                )
            )
            return@withContext Result.failure(SecurityException("Separation of Duties: Admins cannot alter medical records directly."))
        }

        val id = patientDao.insertPatient(patient)
        auditLogDao.insertAuditLog(
            AuditLog(
                actorUsername = actor.username,
                actorRole = actor.role,
                actionType = "PATIENT_INTAKE",
                details = "Registered patient ${patient.fullName} (${patient.mrn}) in Room ${patient.roomNumber}."
            )
        )
        Result.success(id)
    }

    suspend fun recordVitals(
        actor: User,
        vitals: Vitals
    ): Result<Long> = withContext(Dispatchers.IO) {
        if (actor.role == UserRole.ADMIN) {
            auditLogDao.insertAuditLog(
                AuditLog(
                    actorUsername = actor.username,
                    actorRole = actor.role,
                    actionType = "PRIVILEGE_RESTRICTED",
                    details = "Admin blocked from entering vitals (Separation of Duties Policy).",
                    securityLevel = "WARNING"
                )
            )
            return@withContext Result.failure(SecurityException("Separation of Duties: Admins cannot alter patient vitals."))
        }

        val id = vitalsDao.insertVitals(vitals)
        val status = vitals.getVitalStatus()
        auditLogDao.insertAuditLog(
            AuditLog(
                actorUsername = actor.username,
                actorRole = actor.role,
                actionType = "VITALS_RECORDED",
                details = "Vitals added for Patient ID ${vitals.patientId}. HR: ${vitals.heartRate}, BP: ${vitals.bloodPressure}, SpO2: ${vitals.spo2}%, Status: ${status.label}",
                securityLevel = if (status == VitalStatus.CRITICAL) "SECURITY_ALERT" else "INFO"
            )
        )
        Result.success(id)
    }

    suspend fun toggleUserActiveState(admin: User, targetUser: User) = withContext(Dispatchers.IO) {
        if (admin.role != UserRole.ADMIN) return@withContext
        val updated = targetUser.copy(isActive = !targetUser.isActive)
        userDao.updateUser(updated)
        auditLogDao.insertAuditLog(
            AuditLog(
                actorUsername = admin.username,
                actorRole = admin.role,
                actionType = "USER_STATUS_CHANGE",
                details = "Toggled active state of ${targetUser.username} to ${updated.isActive}"
            )
        )
    }

    fun logout() {
        securityManager.clearSession()
    }

    companion object {
        @Volatile
        private var INSTANCE: PrmsRepository? = null

        fun getInstance(context: Context): PrmsRepository {
            return INSTANCE ?: synchronized(this) {
                val secManager = SecurityManager(context)
                val passphrase = secManager.getOrGenerateDatabasePassphrase()
                val database = AppDatabase.getDatabase(context, passphrase)
                val instance = PrmsRepository(database, secManager)
                INSTANCE = instance
                instance
            }
        }
    }
}
