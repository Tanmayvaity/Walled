package com.example.walled.feature.feature_feed.presentation.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.walled.core.domain.model.Image
import com.example.walled.databinding.LocalImageItemBinding

class LocalImagesAdapter(
    private val context: Context,
    private var imageList: List<Image>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LocalImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = LocalImageItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        Glide.with(context)
            .load(image.uri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.pbLocalImageLoading.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.pbLocalImageLoading.visibility = View.GONE
                    return false
                }
            })
            .into(holder.binding.ivLocalImage)

        holder.binding.root.setOnClickListener {
            listener.onItemClick(image)
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun updateList(newList: List<Image>) {
        imageList = newList
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(image: Image)
    }

    inner class ViewHolder(val binding: LocalImageItemBinding) : RecyclerView.ViewHolder(binding.root)
}
