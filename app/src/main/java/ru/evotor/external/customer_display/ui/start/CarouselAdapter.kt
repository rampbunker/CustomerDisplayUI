package ru.evotor.external.customer_display.ui.start

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem

class CarouselAdapter(private val images: List<PictureItem>) :
    RecyclerView.Adapter<CarouselAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.start_carousel_item, parent, false)
        )
    }

    override fun onBindViewHolder(vh: VH, position: Int) {
        val image = images[position]
        vh.setData(image)
    }

    override fun getItemCount(): Int = images.size

    class VH(val view: View) : RecyclerView.ViewHolder(view) {

        private val multiTransformation = MultiTransformation(
            CenterCrop(),
            RoundedCornersTransformation(8, 0)
        )

        fun setData(picture: PictureItem) {
            Glide.with(itemView.context)
                .load(Uri.parse(picture.uriString))
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_empty_gallery)
                .fallback(R.drawable.ic_empty_gallery)
                .into(view.findViewById(R.id.start_gallery_picture))
        }
    }
}