package com.kotlin.pdfextractor.comparators

import com.kotlin.pdfextractor.models.FileItem

class FileComparatorSortSize : Comparator<FileItem> {
    override fun compare(p0: FileItem?, p1: FileItem?): Int {
        return (p0!!.exactSize).compareTo(p1!!.exactSize)
    }
}