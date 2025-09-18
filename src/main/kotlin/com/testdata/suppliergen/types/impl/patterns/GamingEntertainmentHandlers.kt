package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object GameScoreHandler : PatternBasedTypeHandler {
    private val scorePatterns = listOf("score", "points", "highscore", "high_score", "gamescore", "game_score", "rating", "level")

    override val priority: Int = 15
    override fun supports(fqName: String?, psiType: PsiType?) = when (fqName) {
        "java.lang.String", "String" -> true
        "java.lang.Integer", "Integer", "int" -> true
        "java.lang.Long", "Long", "long" -> true
        "java.lang.Double", "Double", "double" -> true
        else -> false
    }

    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, scorePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val score = when {
            fieldName.lowercase().contains("level") -> (1..100).random()
            fieldName.lowercase().contains("rating") -> (1..5).random()
            fieldName.lowercase().contains("high") -> (50000..999999).random()
            else -> (100..50000).random()
        }

        return when (fqName) {
            "java.lang.String", "String" -> "\"$score\""
            "java.lang.Long", "Long", "long" -> "${score}L"
            "java.lang.Double", "Double", "double" -> "$score.0"
            else -> "$score"
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object PlayerIdHandler : PatternBasedTypeHandler {
    private val playerPatterns = listOf("player", "playerid", "player_id", "gamer", "gamerid", "gamer_id", "user", "userid", "user_id")

    override val priority: Int = 14
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, playerPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val playerId = when {
            fieldName.lowercase().contains("gamer") -> "GAMER-${generateAlphaNumeric(8)}"
            fieldName.lowercase().contains("user") && !fieldName.lowercase().contains("player") -> "USER-${(100000..999999).random()}"
            else -> "PLAYER-${generateAlphaNumeric(6)}-${(1000..9999).random()}"
        }
        return "\"$playerId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val prefixes = listOf("PLAYER", "GAMER", "USER", "CHAMPION", "WARRIOR", "MASTER")
        val playerId = "${prefixes.random()}-${generateAlphaNumeric(8)}"
        return "\"$playerId\""
    }

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object StreamKeyHandler : PatternBasedTypeHandler {
    private val streamPatterns = listOf("stream", "streamkey", "stream_key", "streaming", "broadcast", "live", "livekey", "live_key")

    override val priority: Int = 13
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, streamPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val streamKey = "live_${generateHexString(8)}_${generateHexString(12)}_${generateHexString(8)}"
        return "\"$streamKey\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val prefixes = listOf("live", "stream", "broadcast", "rtmp")
        val streamKey = "${prefixes.random()}_${generateHexString(32)}"
        return "\"$streamKey\""
    }

    private fun generateHexString(length: Int): String {
        val hexChars = "0123456789abcdef"
        return (1..length).map { hexChars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}

object GameAchievementHandler : PatternBasedTypeHandler {
    private val achievementPatterns = listOf("achievement", "achievementid", "achievement_id", "badge", "badgeid", "badge_id", "trophy", "trophyid", "trophy_id", "unlock")

    override val priority: Int = 12
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, achievementPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val achievements = listOf(
            "FIRST_KILL", "LEVEL_UP", "BOSS_DEFEATED", "TREASURE_HUNTER", "SPEED_RUNNER",
            "LEGENDARY_WARRIOR", "MASTER_BUILDER", "DRAGON_SLAYER", "GOLD_COLLECTOR", "STREAK_MASTER"
        )

        return when {
            fieldName.lowercase().contains("badge") -> "\"BADGE_${achievements.random()}\""
            fieldName.lowercase().contains("trophy") -> "\"TROPHY_${achievements.random()}\""
            else -> "\"ACHIEVEMENT_${achievements.random()}\""
        }
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object GameSessionHandler : PatternBasedTypeHandler {
    private val sessionPatterns = listOf("session", "sessionid", "session_id", "gamesession", "game_session", "match", "matchid", "match_id", "room", "roomid", "room_id")

    override val priority: Int = 11
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, sessionPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val sessionId = when {
            fieldName.lowercase().contains("match") -> "MATCH-${java.time.LocalDateTime.now().year}${String.format("%06d", (1..999999).random())}"
            fieldName.lowercase().contains("room") -> "ROOM-${generateAlphaNumeric(4)}-${(1000..9999).random()}"
            else -> "SESSION-${generateHexString(16)}"
        }
        return "\"$sessionId\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    private fun generateHexString(length: Int): String {
        val hexChars = "0123456789abcdef"
        return (1..length).map { hexChars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = setOf("java.time.LocalDateTime")
}