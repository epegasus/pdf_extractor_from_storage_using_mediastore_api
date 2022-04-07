package com.kotlin.pdfextractor.models

import java.io.File

data class FileItem(
    var pdfFilePath: File,
    var fileName: String,
    var dateCreated: String,
    var dateModified: String,
    var size: String,
    var exactDateCreated: Long,
    var exactDateModified: Long,
    var exactSize: Long,
    var isFav: Boolean = false
)