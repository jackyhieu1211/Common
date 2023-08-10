package vn.app.common_lib.extension

import android.webkit.MimeTypeMap
import java.io.File

fun listMimeTypeNeedShowRecent(): Array<String> {
    return arrayOf(
        "text/csv",
        "text/javascript",
        "text/css",
        "text/comma-separated-values",
        "text/plain",
        "application/vnd.ms-excel",
        "application/vnd.ms-fontobject",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.oasis.opendocument.text",
        "application/vnd.oasis.opendocument.spreadsheet",
        "application/vnd.oasis.opendocument.presentation",
        "application/vnd.ms-powerpoint",
        "application/vnd.visio",
        "application/pdf",
        "application/xml",
        "text/xml",
        "application/atom+xml",
        "application/x-abiword",
        "application/x-freearc",
        "video/mp2t",
        "video/mp4",
        "video/webm",
        "video/quicktime",
        "video/x-ms-wmv",
        "video/x-msvideo",
        "video/3gpp",
        "video/ogg",
        "application/x-mpegURL",
        "video/x-flv",
        "image/avif",
        "image/jpeg",
        "image/tiff",
        "image/webp",
        "image/png",
        "image/gif",
        "image/svg+xml",
        "audio/3gpp",
        "audio/ogg",
        "audio/wav",
        "audio/mpeg",
        "audio/aac",
        "audio/webm",
        "audio/opus",
        "audio/midi",
        "audio/x-midi",
        "application/ogg",
        "application/vnd.android.package-archive",
        "application/vnd.apple.installer+xml",
        "application/java-archive",
        "application/x-tar",
        "application/x-bzip",
        "application/x-bzip2",
        "application/zip",
        "application/rar",
        "application/vnd.rar",
        "application/gzip",
        "application/x-7z-compressed",
        "application/json",
        "application/ld+json",
        "application/vnd.amazon.ebook",
        "application/x-httpd-php",
        "application/x-shockwave-flash",
        "font/ttf",
        "image/vnd.microsoft.icon"
    )
}

fun listMimeTyVideo(): Array<String> {
    return arrayOf(
        "video/x-flv",
        "video/mp4",
        "application/x-mpegURL",
        "video/MP2T",
        "video/3gpp",
        "video/quicktime",
        "video/x-msvideo",
        "video/x-ms-wmv"
    )
}

fun listMimeTyImage2(): Array<String> {
    return arrayOf("image/jpeg", "image/png", "image/webp")
}

fun listMimeTyImage(): Array<String> {
    return arrayOf("image/gif", "image/jpeg", "image/png", "image/webp", "image/svg+xml")
}

fun listMimeTypePdf(): Array<String> {
    return arrayOf("application/pdf")
}


fun listMimeTypeExcel(): Array<String> {
    return arrayOf(
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.oasis.opendocument.spreadsheet",
    )
}

fun listMimeTypePPT(): Array<String> {
    return arrayOf(
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.oasis.opendocument.presentation",
    )
}

fun listMimeType7z(): Array<String> {
    return arrayOf("application/x-7z-compressed")
}

fun listMimeTypeZip(): Array<String> {
    return arrayOf(
        "application/zip",
        "application/gzip",
        "application/x-bzip",
        "application/x-bzip2"
    )
}

fun listMimeTypeArchives(): Array<String> {
    return arrayOf("application/java-archive")
}

fun listMimeTypeRar(): Array<String> {
    return arrayOf("application/rar", "application/vnd.rar")
}

fun listMimeTypeTar(): Array<String> {
    return arrayOf("application/x-tar")
}

fun listMimeTypeWord(): Array<String> {
    return arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/x-abiword"
    )
}

fun listMimeTypeTxt(): Array<String> {
    return arrayOf("text/plain", "application/rtf", "application/vnd.oasis.opendocument.text")
}

fun listMimeTypeHtml(): Array<String> {
    return arrayOf("text/html")
}

fun listMimeTypeSearch(): Array<String> {
    return arrayOf("application/pdf")
}

fun listMimeTypeDocument(): Array<String> {
    return arrayOf(
        "text/csv",
        "text/plain",
        "application/vnd.ms-excel",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.oasis.opendocument.text",
        "application/vnd.oasis.opendocument.spreadsheet",
        "application/vnd.oasis.opendocument.presentation",
        "application/vnd.ms-powerpoint",
        "application/pdf",
        "application/xml",
        "text/xml",
        "application/atom+xml",
        "application/vnd.apple.installer+xml",
        "application/json",
        "application/ld+json",
    )
}

fun getMimeType(url: String?): String {
    val extension = getExtension(url)
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}

fun getExtension(path: String?): String {
    return if (path != null && path.contains(".")) path.substring(path.lastIndexOf(".") + 1)
        .lowercase() else ""
}

fun File.getMimeType(): String {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
}