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

//    fun createPictureItemFromUri(uri: Uri): PictureItem {
//        val pictureItem = PictureItem()
//        pictureItem.id = UUID.randomUUID().mostSignificantBits
//        pictureItem.uriString = uri.toString()
//        pictureItem.fileName = getFileNameFromUri(uri)
//        return pictureItem
//    }

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

    fun getAllPicturesFromFile(): ArrayList<PictureItemNew> {
        val pictureItemsNewArray: ArrayList<PictureItemNew> = ArrayList()
        val directoryPath = File(
            appContext.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), SettingsFragment.ALBUM_DIRECTORY_NAME
        )
        if (directoryPath.exists()) {
            if (directoryPath.list()!!.isNotEmpty()) {
                directoryPath.list()!!.forEach { i ->
                    val pictureFilePath = File(directoryPath, i)
//                    pictureFilePathArray.add("$directoryPath/$i")
                    val bmOptions = BitmapFactory.Options()
                    var bitmap = BitmapFactory.decodeFile(pictureFilePath.absolutePath, bmOptions)
                    bitmap = Bitmap.createBitmap(bitmap!!)
                    pictureItemsNewArray.add(PictureItemNew(pictureFilePath.name, bitmap))
                }
            }
        }
        return pictureItemsNewArray
    }

//    private fun openRealm(): Realm? {
//        if (realm == null) {
//            realm = Realm.getDefaultInstance()
//        }
//        return realm
//    }

//    fun savePictureToRealm(pictureItem: PictureItem) {
//        val realm = openRealm()!!
//        realm.beginTransaction()
//        realm.copyToRealmOrUpdate(pictureItem)
//        realm.commitTransaction()
//    }

//    fun loadPicturesFromRealm(): ArrayList<PictureItem> {
//        val pictureItemsResults = openRealm()!!.where(PictureItem::class.java).findAll()
//        val pictureItems = ArrayList<PictureItem>()
//        for (pictureItem in pictureItemsResults) {
//            pictureItems.add((pictureItem))
//        }
//        return pictureItems
//    }

//    fun isInRotationEmpty(): Boolean {
//        return openRealm()!!.where(PictureItem::class.java)
//            .equalTo("inRotation", true)
//            .findAll().isEmpty()
//    }

//    fun isRealmEmpty(): Boolean {
//        return openRealm()!!.where(PictureItem::class.java).findAll().isEmpty()
//    }

//    fun deleteFromRealm(pictureItem: PictureItem) {
//        val realm = openRealm()!!
//        val result: RealmResults<PictureItem> =
//            realm.where(PictureItem::class.java).equalTo("id", pictureItem.id).findAll()
//        realm.beginTransaction()
//        result.deleteAllFromRealm()
//        realm.commitTransaction()
//    }

//    fun createPictureItemFromFile(uri: Uri): PictureItem {
//        val pictureItem = PictureItem()
//        pictureItem.id = UUID.randomUUID().mostSignificantBits
//        pictureItem.uriString = uri.toString()
//        pictureItem.fileName = getFileNameFromUri(uri)
//        return pictureItem
//    }
}