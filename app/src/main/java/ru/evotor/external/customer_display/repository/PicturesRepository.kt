package ru.evotor.external.customer_display.repository

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import ru.evotor.external.customer_display.ui.settings.SettingsFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PicturesRepository @Inject constructor(private val appContext: Context) {

    private fun getFileNameFromUri(uri: Uri): String {
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

    fun createPictureItemFromUri(uri: Uri): PictureItem {
        val source = ImageDecoder.createSource(
            appContext.contentResolver,
            uri
        )
        val bitmap = ImageDecoder.decodeBitmap(source)
        val fileName = getFileNameFromUri(uri)
        return PictureItem(fileName, bitmap)
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

    fun savePictureToFile(pictureItem: PictureItem) {
        //путь к папке с картинками
        val directoryPath = File(
            appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), SettingsFragment.ALBUM_DIRECTORY_NAME
        )
        //создать папку, если еще не создана
        if (!directoryPath.exists()) {
            directoryPath.mkdirs()
        }
        //путь к конкретной картинке
        val pictureFilePath = File(directoryPath, pictureItem.filename)
        var fileOutputStream: FileOutputStream? = null
        try {
            //сохраняем картинку
            fileOutputStream = FileOutputStream(pictureFilePath)
            pictureItem.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(appContext, "Failed: " + e.message, Toast.LENGTH_LONG).show()
        } finally {
            if (fileOutputStream != null) {
                try {
                    Toast.makeText(
                        appContext,
                        "Write to <" + pictureFilePath.absolutePath + "> successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(appContext, "Failed to write!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deletePictureFromFile(pictureItem: PictureItem) {
        val directoryPath = File(
            appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), SettingsFragment.ALBUM_DIRECTORY_NAME
        )
        if (directoryPath.exists()) {
            if (directoryPath.list()!!.isNotEmpty()) {
                for (i in directoryPath.list()!!) {
                    val pictureFilePath = File(directoryPath, i)
                    if (pictureFilePath.name.equals(pictureItem.filename)) {
                        pictureFilePath.delete()
                        Toast.makeText(appContext, "Picture deleted", Toast.LENGTH_LONG)
                            .show()
                        return
                    }
                }
            }
        }
    }
}