package com.testdata.suppliergen.types.impl.patterns

import java.time.LocalDate
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * Generates realistic, related data that makes logical sense together.
 * Ensures temporal consistency and logical relationships between fields.
 */
object RelationalDataGenerator {

    /**
     * Generate consistent employee data with logical relationships.
     */
    fun generateEmployeeData(): EmployeeData {
        val birthDate = generateBirthDate()
        val age = calculateAge(birthDate)

        // Experience can't exceed age - 16 (minimum working age)
        val maxExperience = maxOf(0, age - 16)
        val experience = minOf(maxExperience, (0..35).random())

        // Hire date must be after turning 16 and within experience range
        val earliestHireAge = maxOf(16, age - experience)
        val hireDate = birthDate.plusYears(earliestHireAge.toLong()).plusMonths((0..11).random().toLong())

        // Salary based on experience and education
        val hasAdvancedDegree = Random.nextDouble() < 0.3 // 30% have advanced degrees
        val baseSalary = when {
            hasAdvancedDegree -> 65000
            experience >= 10 -> 55000
            experience >= 5 -> 45000
            else -> 35000
        }
        val salaryVariation = (baseSalary * 0.8).toInt()..(baseSalary * 1.4).toInt()
        val salary = salaryVariation.random()

        // Last promotion should be realistic
        val monthsSinceHire = java.time.Period.between(hireDate, LocalDate.now()).toTotalMonths()
        val lastPromotion = if (monthsSinceHire > 12) {
            hireDate.plusMonths((6..monthsSinceHire.coerceAtMost(60)).random())
        } else {
            null
        }

        return EmployeeData(
            birthDate = birthDate,
            age = age,
            experience = experience,
            hireDate = hireDate,
            salary = salary,
            lastPromotion = lastPromotion,
            hasAdvancedDegree = hasAdvancedDegree
        )
    }

    /**
     * Generate consistent financial account data.
     */
    fun generateAccountData(): AccountData {
        val accountType = listOf("CHECKING", "SAVINGS", "CREDIT", "INVESTMENT").random()
        val openDate = LocalDate.now().minusYears((1..20).random().toLong()).minusDays((1..365).random().toLong())

        val balance = when (accountType) {
            "CHECKING" -> Random.nextDouble(100.0, 15000.0)
            "SAVINGS" -> Random.nextDouble(500.0, 50000.0)
            "CREDIT" -> -Random.nextDouble(0.0, 5000.0) // Negative for credit card debt
            "INVESTMENT" -> Random.nextDouble(1000.0, 100000.0)
            else -> Random.nextDouble(100.0, 10000.0)
        }

        val creditLimit = if (accountType == "CREDIT") {
            Random.nextDouble(balance.absoluteValue * 2, balance.absoluteValue * 5)
        } else null

        val interestRate = when (accountType) {
            "CHECKING" -> Random.nextDouble(0.01, 0.5)
            "SAVINGS" -> Random.nextDouble(0.5, 3.0)
            "CREDIT" -> Random.nextDouble(18.0, 29.99)
            "INVESTMENT" -> Random.nextDouble(4.0, 12.0)
            else -> Random.nextDouble(0.1, 2.0)
        }

        return AccountData(
            accountType = accountType,
            openDate = openDate,
            balance = balance,
            creditLimit = creditLimit,
            interestRate = interestRate
        )
    }

    /**
     * Generate consistent medical record data.
     */
    fun generateMedicalData(): MedicalData {
        val birthDate = generateBirthDate()
        val age = calculateAge(birthDate)

        // Blood pressure varies with age
        val systolic = when {
            age < 30 -> (110..130).random()
            age < 50 -> (115..140).random()
            age < 70 -> (120..150).random()
            else -> (125..160).random()
        }
        val diastolic = (systolic * 0.6).toInt()..(systolic * 0.8).toInt()

        // Weight and height correlation
        val heightCm = (150..200).random()
        val idealWeight = (heightCm - 100) * 0.9 // Rough BMI calculation
        val weightVariation = Random.nextDouble(0.8, 1.3)
        val weight = idealWeight * weightVariation

        // Medical conditions more likely with age
        val hasConditions = when {
            age < 30 -> Random.nextDouble() < 0.1
            age < 50 -> Random.nextDouble() < 0.3
            age < 70 -> Random.nextDouble() < 0.6
            else -> Random.nextDouble() < 0.8
        }

        val lastVisit = LocalDate.now().minusDays((1..730).random().toLong()) // Within 2 years

        return MedicalData(
            birthDate = birthDate,
            age = age,
            height = heightCm,
            weight = weight,
            systolicBP = systolic,
            diastolicBP = diastolic.random(),
            hasConditions = hasConditions,
            lastVisit = lastVisit
        )
    }

    /**
     * Generate consistent transaction data.
     */
    fun generateTransactionData(): TransactionData {
        val transactionType = listOf("PURCHASE", "REFUND", "TRANSFER", "DEPOSIT", "WITHDRAWAL").random()
        val amount = when (transactionType) {
            "PURCHASE" -> Random.nextDouble(5.0, 500.0)
            "REFUND" -> -Random.nextDouble(5.0, 200.0) // Negative for refunds
            "TRANSFER" -> Random.nextDouble(50.0, 2000.0)
            "DEPOSIT" -> Random.nextDouble(100.0, 5000.0)
            "WITHDRAWAL" -> Random.nextDouble(20.0, 1000.0)
            else -> Random.nextDouble(10.0, 500.0)
        }

        val transactionDate = LocalDate.now().minusDays((0..90).random().toLong())

        // Processing time varies by type
        val processingDays = when (transactionType) {
            "PURCHASE" -> 0..2
            "REFUND" -> 3..7
            "TRANSFER" -> 1..3
            "DEPOSIT" -> 0..1
            "WITHDRAWAL" -> 0..1
            else -> 0..2
        }.random()

        val processedDate = transactionDate.plusDays(processingDays.toLong())

        return TransactionData(
            transactionType = transactionType,
            amount = amount,
            transactionDate = transactionDate,
            processedDate = processedDate,
            processingDays = processingDays
        )
    }

    private fun generateBirthDate(): LocalDate {
        val currentYear = LocalDate.now().year
        val birthYear = (currentYear - 75)..(currentYear - 18)
        val month = (1..12).random()
        val day = (1..28).random() // Safe day range for all months
        return LocalDate.of(birthYear.random(), month, day)
    }

    private fun calculateAge(birthDate: LocalDate): Int {
        return java.time.Period.between(birthDate, LocalDate.now()).years
    }
}

/**
 * Data classes for related data generation
 */
data class EmployeeData(
    val birthDate: LocalDate,
    val age: Int,
    val experience: Int,
    val hireDate: LocalDate,
    val salary: Int,
    val lastPromotion: LocalDate?,
    val hasAdvancedDegree: Boolean
)

data class AccountData(
    val accountType: String,
    val openDate: LocalDate,
    val balance: Double,
    val creditLimit: Double?,
    val interestRate: Double
)

data class MedicalData(
    val birthDate: LocalDate,
    val age: Int,
    val height: Int, // cm
    val weight: Double, // kg
    val systolicBP: Int,
    val diastolicBP: Int,
    val hasConditions: Boolean,
    val lastVisit: LocalDate
)

data class TransactionData(
    val transactionType: String,
    val amount: Double,
    val transactionDate: LocalDate,
    val processedDate: LocalDate,
    val processingDays: Int
)

/**
 * Temporal consistency validator ensures dates make logical sense.
 */
object TemporalConsistencyValidator {

    fun validateEmployeeData(data: EmployeeData): List<String> {
        val errors = mutableListOf<String>()

        if (data.hireDate.isBefore(data.birthDate.plusYears(16))) {
            errors.add("Hire date cannot be before employee turns 16")
        }

        val experienceYears = java.time.Period.between(data.hireDate, LocalDate.now()).years
        if (data.experience > experienceYears + 1) { // Allow 1 year buffer
            errors.add("Experience cannot exceed years since hire date")
        }

        data.lastPromotion?.let { promotion ->
            if (promotion.isBefore(data.hireDate)) {
                errors.add("Last promotion cannot be before hire date")
            }
        }

        return errors
    }

    fun validateMedicalData(data: MedicalData): List<String> {
        val errors = mutableListOf<String>()

        if (data.lastVisit.isBefore(data.birthDate)) {
            errors.add("Last visit cannot be before birth date")
        }

        // BMI validation
        val heightM = data.height / 100.0
        val bmi = data.weight / (heightM * heightM)
        if (bmi < 10 || bmi > 60) {
            errors.add("BMI (${"%.1f".format(bmi)}) is outside realistic range")
        }

        return errors
    }

    fun validateTransactionData(data: TransactionData): List<String> {
        val errors = mutableListOf<String>()

        if (data.processedDate.isBefore(data.transactionDate)) {
            errors.add("Processed date cannot be before transaction date")
        }

        val actualProcessingDays = java.time.Period.between(data.transactionDate, data.processedDate).days
        if (actualProcessingDays != data.processingDays) {
            errors.add("Processing days mismatch: expected ${data.processingDays}, actual $actualProcessingDays")
        }

        return errors
    }
}