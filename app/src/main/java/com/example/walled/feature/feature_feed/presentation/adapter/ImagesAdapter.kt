package com.example.walled.feature.feature_feed.presentation.adapter

import android.R.attr.text
import android.R.attr.visibility
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.walled.feature.feature_feed.domain.model.Media
import com.example.walled.R
import com.example.walled.databinding.ImageItemBinding
import com.google.android.material.imageview.ShapeableImageView

class ImagesAdapter(
    val context : Context,
    val imageList : List<Media>
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagesAdapter.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val imageItemBinding = ImageItemBinding.inflate(inflater,parent,false)
        return ViewHolder(imageItemBinding)
    }

    override fun onBindViewHolder(holder: ImagesAdapter.ViewHolder, position: Int) {
        val image = imageList[position]
        Glide.with(context).load(image.urls.regular)
            .listener(object : RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    //
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.pbImageLoadingIndicator.visibility = View.GONE
                    return false
                }

            })
            .into(holder.binding.ivFetchedImage)
        holder.binding.tvUser.text = "Made by ${image.user.name}"
    }

    override fun getItemCount(): Int  = imageList.size

    inner class ViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)

}