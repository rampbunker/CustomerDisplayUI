package ru.evotor.external.customer_display.ui.settings

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.settings_pictures_rv_item.view.*
import ru.evotor.external.customer_display.R
import ru.evotor.external.customer_display.repository.PictureItem


class SettingsPicturesAdapter(private val clickListener: (PictureItem) -> Unit) :
    RecyclerView.Adapter<SettingsPicturesAdapter.SettingsPicturesViewHolder>() {
    private var dataset = ArrayList<PictureItem>()


    override fun getItemCount(): Int =
        dataset.size

    fun bindPictures(newPictures: ArrayList<PictureItem>) {
        dataset = newPictures
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SettingsPicturesViewHolder, position: Int) =
        holder.run {
            setData(dataset[position])
            holder.itemView.settings_gallery_icon_remove.setOnClickListener {
                clickListener(dataset[position])
                dataset.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsPicturesViewHolder =
        SettingsPicturesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.settings_pictures_rv_item, parent, false)
        )

    inner class SettingsPicturesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val picture by lazy { view.findViewById<ImageView>(R.id.settings_gallery_picture) }
        private val fileName by lazy { view.findViewById<TextView>(R.id.settingsGalleryFileName) }

        fun setData(pictureItem: PictureItem) {
            fileName.text = pictureItem.fileName
            Glide.with(itemView.context)
                .load(Uri.parse(pictureItem.uriString))
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_empty_gallery)
                .fallback(R.drawable.ic_empty_gallery)
                .into(picture)
        }
    }
}