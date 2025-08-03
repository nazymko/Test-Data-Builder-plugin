package com.testdata.suppliergen.factory

import com.intellij.psi.PsiType

object FqNameTypeResolver {

    fun canonicalClassNameToFqName(psiType: PsiType): String {
        val canonicalFqName = when (psiType.canonicalText) {
            "String" -> "java.lang.String"
            "Integer" -> "java.lang.Integer"
            "Boolean" -> "java.lang.Boolean"
            "Double" -> "java.lang.Double"
            "Long" -> "java.lang.Long"
            "Short" -> "java.lang.Short"
            "Byte" -> "java.lang.Byte"
            "Character" -> "java.lang.Character"
            "Float" -> "java.lang.Float"
            "Object" -> "java.lang.Object"
            "Void" -> "java.lang.Void"

            "List" -> "java.util.List"
            "ArrayList" -> "java.util.ArrayList"
            "LinkedList" -> "java.util.LinkedList"
            "Set" -> "java.util.Set"
            "HashSet" -> "java.util.HashSet"
            "LinkedHashSet" -> "java.util.LinkedHashSet"
            "TreeSet" -> "java.util.TreeSet"
            "Map" -> "java.util.Map"
            "HashMap" -> "java.util.HashMap"
            "LinkedHashMap" -> "java.util.LinkedHashMap"
            "TreeMap" -> "java.util.TreeMap"
            "ConcurrentHashMap" -> "java.util.concurrent.ConcurrentHashMap"
            "EnumMap" -> "java.util.EnumMap"
            "WeakHashMap" -> "java.util.WeakHashMap"
            "IdentityHashMap" -> "java.util.IdentityHashMap"

            "Optional" -> "java.util.Optional"
            "UUID" -> "java.util.UUID"
            "Date" -> "java.util.Date"
            "Calendar" -> "java.util.Calendar"
            "Locale" -> "java.util.Locale"
            "TimeZone" -> "java.util.TimeZone"

            "Instant" -> "java.time.Instant"
            "LocalDate" -> "java.time.LocalDate"
            "LocalDateTime" -> "java.time.LocalDateTime"
            "OffsetDateTime" -> "java.time.OffsetDateTime"
            "ZonedDateTime" -> "java.time.ZonedDateTime"
            "ZoneId" -> "java.time.ZoneId"
            "Duration" -> "java.time.Duration"
            "Period" -> "java.time.Period"

            "BigDecimal" -> "java.math.BigDecimal"
            "BigInteger" -> "java.math.BigInteger"

            "URI" -> "java.net.URI"
            "URL" -> "java.net.URL"

            "CopyOnWriteArrayList" -> "java.util.concurrent.CopyOnWriteArrayList"
            "ConcurrentSkipListMap" -> "java.util.concurrent.ConcurrentSkipListMap"

            "Function" -> "java.util.function.Function"
            "Predicate" -> "java.util.function.Predicate"
            "Consumer" -> "java.util.function.Consumer"

            "File" -> "java.io.File"
            "Serializable" -> "java.io.Serializable"

            "Path" -> "java.nio.file.Path"

            else -> psiType.canonicalText // as is
        }
        return canonicalFqName
    }
}