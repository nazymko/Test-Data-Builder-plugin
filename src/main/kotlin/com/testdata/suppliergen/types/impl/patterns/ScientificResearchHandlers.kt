package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object DOIHandler : PatternBasedTypeHandler {
    private val doiPatterns = listOf("doi", "digitalobjectidentifier", "digital_object_identifier", "publication", "paper", "research", "journal")

    override val priority: Int = 20
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, doiPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val registrant = listOf("1000", "1001", "1038", "1109", "1145", "1155", "1371", "3390").random()
        val suffix = (100..999999).random()
        val doi = "10.$registrant/$suffix"
        return "\"$doi\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object ResearchIdHandler : PatternBasedTypeHandler {
    private val researchPatterns = listOf("researchid", "research_id", "studyid", "study_id", "experimentid", "experiment_id", "projectid", "project_id", "grantid", "grant_id")

    override val priority: Int = 19
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, researchPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val currentYear = java.time.LocalDate.now().year
        val researchId = when {
            fieldName.lowercase().contains("study") -> "STUDY-$currentYear-${String.format("%06d", (1..999999).random())}"
            fieldName.lowercase().contains("experiment") -> "EXP-${generateAlphaNumeric(4)}-$currentYear-${(100..999).random()}"
            fieldName.lowercase().contains("project") -> "PROJ-${generateAlphaNumeric(6)}-${(1000..9999).random()}"
            fieldName.lowercase().contains("grant") -> "GRANT-${listOf("NIH", "NSF", "DOE", "NASA", "DARPA").random()}-$currentYear-${(100000..999999).random()}"
            else -> "RESEARCH-$currentYear-${generateAlphaNumeric(8)}"
        }
        return "\"$researchId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object LabSampleHandler : PatternBasedTypeHandler {
    private val samplePatterns = listOf("sample", "sampleid", "sample_id", "specimen", "specimenid", "specimen_id", "lab", "labid", "lab_id", "batch", "batchid", "batch_id")

    override val priority: Int = 18
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, samplePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val currentDate = java.time.LocalDate.now()
        val sampleId = when {
            fieldName.lowercase().contains("specimen") -> "SPEC-${currentDate.year}${String.format("%02d%02d", currentDate.monthValue, currentDate.dayOfMonth)}-${generateAlphaNumeric(6)}"
            fieldName.lowercase().contains("batch") -> "BATCH-${currentDate.year}-${('A'..'Z').random()}${(100..999).random()}"
            fieldName.lowercase().contains("lab") -> "LAB-${currentDate.year}-SAMPLE-${String.format("%06d", (1..999999).random())}"
            else -> "SAMPLE-${generateAlphaNumeric(4)}-${currentDate.year}-${String.format("%04d", (1..9999).random())}"
        }
        return "\"$sampleId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDate")
}

object ChemicalFormulaHandler : PatternBasedTypeHandler {
    private val chemicalPatterns = listOf("formula", "chemical", "compound", "molecule", "reagent", "catalyst", "solution")

    override val priority: Int = 17
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, chemicalPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val commonFormulas = listOf(
            "H2O", "NaCl", "CO2", "H2SO4", "HCl", "NaOH", "CH4", "C2H6O", "C6H12O6", "CaCO3",
            "NH3", "HNO3", "H2O2", "C2H4", "CH3COOH", "C6H6", "C8H18", "CaO", "KCl", "MgSO4"
        )

        val formula = when {
            fieldName.lowercase().contains("acid") -> listOf("H2SO4", "HCl", "HNO3", "CH3COOH").random()
            fieldName.lowercase().contains("base") || fieldName.lowercase().contains("alkali") -> listOf("NaOH", "KOH", "Ca(OH)2", "NH3").random()
            fieldName.lowercase().contains("salt") -> listOf("NaCl", "KCl", "CaCO3", "MgSO4").random()
            fieldName.lowercase().contains("organic") -> listOf("CH4", "C2H6O", "C6H12O6", "C6H6", "C8H18").random()
            else -> commonFormulas.random()
        }
        return "\"$formula\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object GenomeSequenceHandler : PatternBasedTypeHandler {
    private val genomePatterns = listOf("genome", "dna", "rna", "sequence", "gene", "geneid", "gene_id", "chromosome", "nucleotide", "basepair", "base_pair")

    override val priority: Int = 16
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, genomePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val sequence = when {
            fieldName.lowercase().contains("gene") && (fieldName.lowercase().contains("id") || fieldName.lowercase().contains("_id")) -> {
                // Gene ID format
                "ENSG${String.format("%011d", (1..99999999999L).random())}"
            }
            fieldName.lowercase().contains("rna") -> {
                // RNA sequence (A, U, G, C)
                generateSequence(listOf("A", "U", "G", "C"), (20..100).random())
            }
            fieldName.lowercase().contains("chromosome") -> {
                // Chromosome notation
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y").random()
            }
            else -> {
                // DNA sequence (A, T, G, C)
                generateSequence(listOf("A", "T", "G", "C"), (20..100).random())
            }
        }
        return "\"$sequence\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateSequence(bases: List<String>, length: Int): String {
        return (1..length).map { bases.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}