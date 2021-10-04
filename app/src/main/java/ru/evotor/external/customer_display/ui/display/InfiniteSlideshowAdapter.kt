package ru.evotor.external.customer_display.ui.display

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem
import java.io.InputStream
import java.util.*


class InfiniteSlideshowAdapter(
    itemList: ArrayList<PictureItem>,
    isInfinite: Boolean
) : LoopingPagerAdapter<PictureItem>(itemList, isInfinite) {

    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {
        return LayoutInflater.from(container.context)
            .inflate(R.layout.slideshow_item, container, false)
    }

    override fun bindView(
        convertView: View,
        listPosition: Int,
        viewType: Int
    ) {
        val imageView = convertView.findViewById<ImageView>(R.id.slideshowImage)
        val inputStream: InputStream? =
            convertView.context.contentResolver.openInputStream(Uri.parse(itemList?.get(listPosition)?.uriString))
        val drawable =
            Drawable.createFromStream(inputStream, itemList?.get(listPosition)?.uriString)
        Glide.with(convertView.context)
            .load(drawable)
            .into(imageView)
    }
}