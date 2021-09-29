package ru.evotor.external.customer_display.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import io.realm.Realm
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class PicturesRepository @Inject constructor(private val appContext: Context) {

    private var realm: Realm? = null

    fun createPictureItemFromUri(uri: Uri): PictureItem {
        val pictureItem = PictureItem()
        pictureItem.id = UUID.randomUUID().mostSignificantBits
        pictureItem.uriString = uri.toString()
        pictureItem.fileName = getFileNameFromUri(uri)
        return pictureItem
    }

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

    private fun openRealm(): Realm? {
        if (realm == null) {
            realm = Realm.getDefaultInstance()
        }
        return realm
    }

    fun savePictureToRealm(pictureItem: PictureItem) {
        val realm = openRealm()!!
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(pictureItem)
        realm.commitTransaction()
    }

    fun loadPicturesFromRealm(): ArrayList<PictureItem> {
        val pictureItemsResults = openRealm()!!.where(PictureItem::class.java).findAll()
        val pictureItems = ArrayList<PictureItem>()
        for (pictureItem in pictureItemsResults) {
            pictureItems.add((pictureItem))
        }
        return pictureItems
    }

    fun isInRotationEmpty(): Boolean {
        return openRealm()!!.where(PictureItem::class.java)
            .equalTo("inRotation", true)
            .findAll().isEmpty()
    }

    fun isRealmEmpty(): Boolean {
        return openRealm()!!.where(PictureItem::class.java).findAll().isEmpty()
    }
}