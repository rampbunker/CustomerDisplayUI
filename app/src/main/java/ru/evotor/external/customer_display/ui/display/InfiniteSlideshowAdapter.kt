package ru.evotor.external.customer_display.ui.display

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItemNew
import java.util.*


class InfiniteSlideshowAdapter(
    itemList: ArrayList<PictureItemNew>,
    isInfinite: Boolean
) : LoopingPagerAdapter<PictureItemNew>(itemList, isInfinite) {

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
        Glide.with(convertView.context)
            .load(itemList?.get(listPosition)?.bitmap)
            .into(imageView)
    }
}