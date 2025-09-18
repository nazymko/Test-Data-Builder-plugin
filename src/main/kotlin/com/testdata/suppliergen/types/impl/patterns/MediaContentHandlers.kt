package com.testdata.suppliergen.types.impl.patterns

import com.testdata.suppliergen.types.contract.PatternBasedTypeHandler
import com.intellij.psi.PsiType
import kotlin.random.Random

object ISBN13Handler : PatternBasedTypeHandler {
    private val isbnPatterns = listOf("isbn", "isbn13", "isbn_13", "bookid", "book_id", "publication", "publicationid", "publication_id")

    override val priority: Int = 19
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, isbnPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        // Generate ISBN-13 format: 978-0-123456-47-2
        val prefix = "978" // Standard ISBN-13 prefix
        val group = (0..9).random() // Language/country group
        val publisher = String.format("%06d", (100000..999999).random())
        val title = String.format("%02d", (10..99).random())
        val checkDigit = (0..9).random()

        val isbn = "$prefix-$group-$publisher-$title-$checkDigit"
        return "\"$isbn\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object MimeTypeHandler : PatternBasedTypeHandler {
    private val mimePatterns = listOf("mime", "mimetype", "mime_type", "contenttype", "content_type", "mediatype", "media_type", "format")

    override val priority: Int = 18
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, mimePatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val mimeTypes = mapOf(
            "text" to listOf("text/plain", "text/html", "text/css", "text/javascript", "text/csv", "text/xml"),
            "image" to listOf("image/jpeg", "image/png", "image/gif", "image/svg+xml", "image/webp", "image/bmp"),
            "video" to listOf("video/mp4", "video/avi", "video/mov", "video/webm", "video/mkv", "video/wmv"),
            "audio" to listOf("audio/mp3", "audio/wav", "audio/ogg", "audio/aac", "audio/flac", "audio/m4a"),
            "application" to listOf("application/json", "application/xml", "application/pdf", "application/zip", "application/octet-stream", "application/javascript")
        )

        val mimeType = when {
            fieldName.lowercase().contains("image") -> mimeTypes["image"]!!.random()
            fieldName.lowercase().contains("video") -> mimeTypes["video"]!!.random()
            fieldName.lowercase().contains("audio") -> mimeTypes["audio"]!!.random()
            fieldName.lowercase().contains("text") -> mimeTypes["text"]!!.random()
            else -> mimeTypes.values.flatten().random()
        }

        return "\"$mimeType\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object FileExtensionHandler : PatternBasedTypeHandler {
    private val extensionPatterns = listOf("extension", "ext", "filetype", "file_type", "fileext", "file_ext", "suffix")

    override val priority: Int = 17
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, extensionPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val extensions = mapOf(
            "document" to listOf("pdf", "doc", "docx", "txt", "rtf", "odt"),
            "image" to listOf("jpg", "jpeg", "png", "gif", "svg", "webp", "bmp"),
            "video" to listOf("mp4", "avi", "mov", "webm", "mkv", "wmv", "flv"),
            "audio" to listOf("mp3", "wav", "ogg", "aac", "flac", "m4a", "wma"),
            "archive" to listOf("zip", "rar", "7z", "tar", "gz", "bz2"),
            "code" to listOf("java", "kt", "js", "py", "cpp", "cs", "go", "rs"),
            "data" to listOf("json", "xml", "csv", "yaml", "sql", "db")
        )

        val extension = when {
            fieldName.lowercase().contains("image") -> extensions["image"]!!.random()
            fieldName.lowercase().contains("video") -> extensions["video"]!!.random()
            fieldName.lowercase().contains("audio") -> extensions["audio"]!!.random()
            fieldName.lowercase().contains("document") -> extensions["document"]!!.random()
            fieldName.lowercase().contains("archive") -> extensions["archive"]!!.random()
            fieldName.lowercase().contains("code") -> extensions["code"]!!.random()
            fieldName.lowercase().contains("data") -> extensions["data"]!!.random()
            else -> extensions.values.flatten().random()
        }

        return "\"$extension\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object ColorCodeHandler : PatternBasedTypeHandler {
    private val colorPatterns = listOf("color", "colour", "hex", "hexcolor", "hex_color", "rgb", "rgba", "theme", "background", "foreground")

    override val priority: Int = 16
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, colorPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val colorCode = when {
            fieldName.lowercase().contains("rgb") && !fieldName.lowercase().contains("rgba") -> {
                // RGB format: rgb(255, 128, 64)
                val r = (0..255).random()
                val g = (0..255).random()
                val b = (0..255).random()
                "rgb($r, $g, $b)"
            }
            fieldName.lowercase().contains("rgba") -> {
                // RGBA format: rgba(255, 128, 64, 0.8)
                val r = (0..255).random()
                val g = (0..255).random()
                val b = (0..255).random()
                val a = String.format("%.1f", Random.nextDouble(0.1, 1.0))
                "rgba($r, $g, $b, $a)"
            }
            fieldName.lowercase().contains("background") -> {
                // Common background colors
                val bgColors = listOf("#FFFFFF", "#F8F9FA", "#E9ECEF", "#DEE2E6", "#212529", "#343A40")
                bgColors.random()
            }
            fieldName.lowercase().contains("foreground") || fieldName.lowercase().contains("text") -> {
                // Common text colors
                val textColors = listOf("#000000", "#212529", "#495057", "#6C757D", "#FFFFFF", "#F8F9FA")
                textColors.random()
            }
            else -> {
                // HEX format: #FF5733
                val hex = String.format("#%06X", (0..16777215).random())
                hex
            }
        }

        return "\"$colorCode\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)
    override val staticExtraImports: Set<String> = emptySet()
}

object MediaUrlHandler : PatternBasedTypeHandler {
    private val urlPatterns = listOf("url", "link", "href", "src", "source", "thumbnail", "avatar", "image", "video", "audio")

    override val priority: Int = 15
    override fun supports(fqName: String?, psiType: PsiType?) = fqName == "java.lang.String" || fqName == "String"
    override fun supportsFieldName(fieldName: String, fqName: String?, psiType: PsiType?) = supports(fqName, psiType) && matchesPattern(fieldName, urlPatterns)

    override fun defaultValue(fieldName: String, fqName: String?, psiType: PsiType?): String {
        val domains = listOf("example.com", "test-media.org", "demo-assets.net", "sample-content.io")
        val domain = domains.random()

        val url = when {
            fieldName.lowercase().contains("image") || fieldName.lowercase().contains("thumbnail") || fieldName.lowercase().contains("avatar") -> {
                val imageId = (1000..9999).random()
                val extension = listOf("jpg", "png", "webp").random()
                "https://$domain/images/img_$imageId.$extension"
            }
            fieldName.lowercase().contains("video") -> {
                val videoId = generateAlphaNumeric(8)
                "https://$domain/videos/video_$videoId.mp4"
            }
            fieldName.lowercase().contains("audio") -> {
                val audioId = generateAlphaNumeric(8)
                "https://$domain/audio/track_$audioId.mp3"
            }
            else -> {
                val resourceId = generateAlphaNumeric(10)
                "https://$domain/content/$resourceId"
            }
        }

        return "\"$url\""
    }

    override fun randomizedValue(fieldName: String, fqName: String?, psiType: PsiType?) = defaultValue(fieldName, fqName, psiType)

    private fun generateAlphaNumeric(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }

    override val staticExtraImports: Set<String> = emptySet()
}