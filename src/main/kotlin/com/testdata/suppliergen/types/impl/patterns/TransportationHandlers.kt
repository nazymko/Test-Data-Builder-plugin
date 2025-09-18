package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType

object LicensePlateTypeHandler : PatternBasedTypeHandler {
    private val platePatterns = listOf("license", "plate", "licenseplate", "license_plate", "registration", "plateNumber")
    override val priority: Int = 15
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, platePatterns) && !fieldName.lowercase().contains("driver")
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${('A'..'Z').random()}${('A'..'Z').random()}${('A'..'Z').random()}-${(1000..9999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${(1000..9999).random()}${('A'..'Z').random()}${('A'..'Z').random()}${('A'..'Z').random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}

object VinTypeHandler : PatternBasedTypeHandler {
    private val vinPatterns = listOf("vin", "vehicleid", "vehicle_id", "chassis", "vehiclenumber", "vehicle_number")
    override val priority: Int = 14
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, vinPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"1HGBH41JXMN${(100000..999999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${(1..9).random()}${('A'..'Z').filter { it !in "IOQ" }.random()}${('A'..'Z').random()}${('A'..'Z').random()}${('A'..'Z').random()}${(10..99).random()}${('A'..'Z').random()}${(100000..999999).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}

object FlightNumberTypeHandler : PatternBasedTypeHandler {
    private val flightPatterns = listOf("flight", "flightnumber", "flight_number", "flightcode", "flight_code")
    private val airlines = listOf("AA", "UA", "DL", "SW", "BA", "LH", "AF", "KL", "VS", "JB")
    override val priority: Int = 13
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, flightPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${airlines.random()}${(100..9999).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"${airlines.random()}${(100..9999).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}

object TrackingNumberTypeHandler : PatternBasedTypeHandler {
    private val trackingPatterns = listOf("tracking", "trackingnumber", "tracking_number", "shipment", "packageid", "package_id")
    override val priority: Int = 12
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, trackingPatterns)
    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"1Z999AA${(1000000000L..9999999999L).random()}\""
    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = "\"TRK${(100000000L..999999999L).random()}\""
    override val staticExtraImports: Set<String> = emptySet()
}