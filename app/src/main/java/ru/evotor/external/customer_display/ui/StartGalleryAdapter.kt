package ru.evotor.external.customer_display.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import ru.evotor.external.customer_display.R

class StartGalleryAdapter : RecyclerView.Adapter<StartGalleryAdapter.StartGalleryViewHolder>() {
    private var dataset = listOf<String>()


    override fun getItemCount(): Int =
        dataset.size

    fun bindPictures(newPictures: List<String>) {
        dataset = newPictures
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: StartGalleryViewHolder, position: Int) =
        holder.run {
            setData(dataset[position])
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartGalleryViewHolder =
        StartGalleryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.start_gallery_rv_item, parent, false)
        )


    inner class StartGalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val picture by lazy { view.findViewById<ImageView>(R.id.start_gallery_picture) }

        private val multiTransformation = MultiTransformation(
            CenterCrop(),
            RoundedCornersTransformation(8, 0)
        )

        fun setData(data: String) {
            Glide.with(itemView.context)
                .load(data)
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_empty_gallery)
                .fallback(R.drawable.ic_empty_gallery)
                .into(picture)
        }
    }
}