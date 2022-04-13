package com.kotlin.pdfextractor.viewModels

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.pdfextractor.models.FileItem
import com.kotlin.pdfextractor.utils.ConstantUtils.Companion.TAG
import com.kotlin.pdfextractor.utils.GeneralUtils.getDate
import com.kotlin.pdfextractor.utils.GeneralUtils.getFileSize
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File

class HomeViewModel(private val application: Application) : ViewModel() {

    private var fileList = ArrayList<FileItem>()
    private val fileListMutableLiveData: MutableLiveData<ArrayList<FileItem>> = MutableLiveData<ArrayList<FileItem>>()

    private var exceptionHandler = CoroutineExceptionHandler { contextCoroutine, throwable ->
        Log.d(TAG, "Exception: HomeViewModel: (Context: $contextCoroutine): $throwable")
    }

    fun fetchPdfFiles() {
        CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_ADDED,
                    MediaStore.Files.FileColumns.DATE_MODIFIED,
                    MediaStore.Files.FileColumns.SIZE
                )

                val mimeTypePdf = "application/pdf"
                val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeTypePdf + "')"
                val orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
                val cursor: Cursor? = application.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    projection,
                    whereClause,
                    null,
                    orderBy
                )

                cursor?.let {
                    //val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val dataCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    val addedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                    val modifiedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                    val sizeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                    if (it.moveToFirst()) {
                        do {
                            //val fileUri: Uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), it.getString(idCol))
                            val data = it.getString(dataCol)
                            val dateAdded = it.getLong(addedCol)
                            val dateModified = it.getLong(modifiedCol)
                            val size = it.getLong(sizeCol)
                            val file = File(data)
                            val fileItem = FileItem(file, file.name, getDate(dateAdded * 1000), getDate(dateModified * 1000), getFileSize(size), dateAdded, dateModified, size, false)
                            fileList.add(fileItem)
                        } while (it.moveToNext())
                    }
                    it.close()
                }
            }.join()
            fileListMutableLiveData.value = fileList
        }
    }

    fun fetchPdfFilesWithFlow(): Flow<FileItem> {
        return flow {
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.SIZE
            )

            val mimeTypePdf = "application/pdf"
            val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeTypePdf + "')"
            val orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
            val cursor: Cursor? = application.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                whereClause,
                null,
                orderBy
            )

            cursor?.let {
                //val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val dataCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val addedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val modifiedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                val sizeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                if (it.moveToFirst()) {
                    do {
                        //val fileUri: Uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), it.getString(idCol))
                        val data = it.getString(dataCol)
                        val dateAdded = it.getLong(addedCol)
                        val dateModified = it.getLong(modifiedCol)
                        val size = it.getLong(sizeCol)
                        val file = File(data)
                        val fileItem = FileItem(file, file.name, getDate(dateAdded * 1000), getDate(dateModified * 1000), getFileSize(size), dateAdded, dateModified, size, false)
                        CoroutineScope(IO).launch {
                            fileList.add(fileItem)
                        }.join()
                        emit(fileItem)
                    } while (it.moveToNext())
                    //fileListMutableLiveData.value = fileList
                }
                it.close()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class HomeViewModelProviderFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(application) as T
        }
    }
}

