package ru.evotor.external.customer_display.repository

import android.net.Uri
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList

class PicturesRepository() {

    private var realm: Realm? = null

    fun createPictureItemFromUri(uri: Uri, fileName: String): PictureItem {
        val pictureItem = PictureItem()
        pictureItem.id = UUID.randomUUID().mostSignificantBits
        pictureItem.uriString = uri.toString()
        pictureItem.fileName = fileName
        return pictureItem
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
}