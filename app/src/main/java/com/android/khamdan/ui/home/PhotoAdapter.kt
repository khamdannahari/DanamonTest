package com.android.khamdan.ui.home
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.khamdan.R
import com.android.khamdan.data.photo.Photo
import com.bumptech.glide.Glide

class PhotoAdapter(private val photos: MutableList<Photo>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)
        val textId: TextView = itemView.findViewById(R.id.textId)
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textUrl: TextView = itemView.findViewById(R.id.textUrl)
    }

    fun addItems(newPhotos: List<Photo>) {
        val startPos = photos.size
        photos.addAll(newPhotos)
        notifyItemRangeInserted(startPos, newPhotos.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        holder.textId.text = "ID: ${photo.id}"
        holder.textTitle.text = "Title: ${photo.title}"
        holder.textUrl.text = "URL: ${photo.url}"
        Glide.with(holder.itemView.context)
            .load(photo.thumbnailUrl)
            .into(holder.imageThumbnail)

        holder.textUrl.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(photo.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }
}
