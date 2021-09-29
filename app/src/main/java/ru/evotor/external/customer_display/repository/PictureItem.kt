package ru.evotor.external.customer_display.repository

import android.net.Uri

data class PictureItem(
    var uri: Uri,
    var fileName: String = "",
    var inRotation: Boolean = false
)
