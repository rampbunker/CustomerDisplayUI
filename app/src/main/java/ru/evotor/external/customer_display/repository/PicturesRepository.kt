package ru.evotor.external.customer_display.repository

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import ru.evotor.external.customer_display.ui.settings.SettingsFragment
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PicturesRepository @Inject constructor(private val appContext: Context) {

    fun getFileNameFromUri(uri: Uri): String {
        var result = ""
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = appContext.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        if (result == "") {
            result = uri.path.toString()
            val cut = result.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun isPicturesDirectoryEmpty(): Boolean {
        val directoryPath = File(
            appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), SettingsFragment.ALBUM_DIRECTORY_NAME
        )
        return !(directoryPath.exists() && directoryPath.list()!!.isNotEmpty())
    }

    fun loadAllPicturesFromFile(): ArrayList<PictureItem> {
        val pictureItemsArray: ArrayList<PictureItem> = ArrayList()
        val directoryPath = File(
            appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), SettingsFragment.ALBUM_DIRECTORY_NAME
        )
        if (directoryPath.exists()) {
            if (directoryPath.list()!!.isNotEmpty()) {
                directoryPath.list()!!.forEach { i ->
                    val pictureFilePath = File(directoryPath, i)
                    val bmOptions = BitmapFactory.Options()
                    var bitmap = BitmapFactory.decodeFile(pictureFilePath.absolutePath, bmOptions)
                    bitmap = Bitmap.createBitmap(bitmap!!)
                    pictureItemsArray.add(PictureItem(pictureFilePath.name, bitmap))
                }
            }
        }
        return pictureItemsArray
    }
}