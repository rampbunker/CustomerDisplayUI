package ru.evotor.external.customer_display.repository

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PictureItem : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    lateinit var uriString: String
    var fileName: String = ""
    var inRotation: Boolean = true
}
