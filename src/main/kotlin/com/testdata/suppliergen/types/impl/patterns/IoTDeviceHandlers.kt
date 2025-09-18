package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object DeviceIdHandler : PatternBasedTypeHandler {
    private val devicePatterns = listOf("device", "deviceid", "device_id", "iot", "iotdevice", "iot_device", "sensor", "sensorid", "sensor_id")

    override val priority: Int = 15
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, devicePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val deviceType = when {
            fieldName.lowercase().contains("sensor") -> "SENSOR"
            fieldName.lowercase().contains("camera") -> "CAM"
            fieldName.lowercase().contains("thermostat") -> "THERMO"
            fieldName.lowercase().contains("light") -> "LIGHT"
            else -> "IOT"
        }
        val deviceId = "$deviceType-${generateAlphaNumeric(6)}-${(100000..999999).random()}"
        return "\"$deviceId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object SensorValueHandler : PatternBasedTypeHandler {
    private val sensorPatterns = listOf("temperature", "humidity", "pressure", "brightness", "motion", "proximity", "acceleration", "gyroscope")

    override val priority: Int = 14
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        "java.lang.Integer", "Integer", "int" -> true
        "java.math.BigDecimal", "BigDecimal" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, sensorPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val value = when {
            fieldName.lowercase().contains("temp") -> Random.nextDouble(15.0, 35.0) // Celsius
            fieldName.lowercase().contains("humidity") -> Random.nextDouble(30.0, 80.0) // Percentage
            fieldName.lowercase().contains("pressure") -> Random.nextDouble(950.0, 1050.0) // hPa
            fieldName.lowercase().contains("brightness") -> Random.nextDouble(0.0, 100.0) // Percentage
            fieldName.lowercase().contains("motion") -> Random.nextDouble(0.0, 10.0) // m/s
            fieldName.lowercase().contains("proximity") -> Random.nextDouble(0.0, 200.0) // cm
            fieldName.lowercase().contains("acceleration") -> Random.nextDouble(-9.8, 9.8) // m/s²
            else -> Random.nextDouble(0.0, 100.0)
        }

        val formattedValue = String.format("%.2f", value)
        return when (fqName) {
            "java.lang.String", "String" -> "\"$formattedValue\""
            "java.math.BigDecimal", "BigDecimal" -> "new java.math.BigDecimal(\"$formattedValue\")"
            "java.lang.Double", "Double", "double" -> formattedValue
            "java.lang.Float", "Float", "float" -> "${formattedValue}f"
            "java.lang.Integer", "Integer", "int" -> "${value.toInt()}"
            else -> formattedValue
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = setOf("java.math.BigDecimal")
}

object BatteryLevelHandler : PatternBasedTypeHandler {
    private val batteryPatterns = listOf("battery", "batterylevel", "battery_level", "charge", "chargelevel", "charge_level", "power", "powerlevel", "power_level")

    override val priority: Int = 13
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.lang.Integer", "Integer", "int" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, batteryPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val batteryLevel = (10..100).random() // 10-100% battery

        return when (fqName) {
            "java.lang.String", "String" -> "\"$batteryLevel%\""
            "java.lang.Integer", "Integer", "int" -> "$batteryLevel"
            "java.lang.Double", "Double", "double" -> "$batteryLevel.0"
            "java.lang.Float", "Float", "float" -> "${batteryLevel}.0f"
            else -> "$batteryLevel"
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object MachineLearningModelHandler : PatternBasedTypeHandler {
    private val mlPatterns = listOf("model", "modelid", "model_id", "algorithm", "prediction", "confidence", "accuracy", "score")

    override val priority: Int = 12
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.lang.Double", "Double", "double" -> true
        "java.lang.Float", "Float", "float" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, mlPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        return when {
            fieldName.lowercase().contains("modelid") || fieldName.lowercase().contains("model_id") -> {
                val modelTypes = listOf("CNN", "RNN", "LSTM", "BERT", "GPT", "YOLO", "ResNet")
                val modelId = "${modelTypes.random()}-v${(1..9).random()}.${(0..9).random()}-${generateAlphaNumeric(6)}"
                "\"$modelId\""
            }
            fieldName.lowercase().contains("algorithm") -> {
                val algorithms = listOf("RandomForest", "SVM", "NeuralNetwork", "KMeans", "LogisticRegression", "DecisionTree")
                "\"${algorithms.random()}\""
            }
            fieldName.lowercase().contains("confidence") || fieldName.lowercase().contains("accuracy") || fieldName.lowercase().contains("score") -> {
                val value = Random.nextDouble(0.65, 0.99) // 65-99% confidence/accuracy
                val formattedValue = String.format("%.4f", value)
                when (fqName) {
                    "java.lang.String", "String" -> "\"$formattedValue\""
                    "java.lang.Float", "Float", "float" -> "${formattedValue}f"
                    else -> formattedValue
                }
            }
            else -> {
                val predictions = listOf("cat", "dog", "car", "person", "positive", "negative", "fraud", "normal")
                "\"${predictions.random()}\""
            }
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}