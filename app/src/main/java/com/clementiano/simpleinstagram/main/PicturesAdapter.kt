package com.clementiano.simpleinstagram.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.clementiano.simpleinstagram.R
import com.clementiano.simpleinstagram.data.MediaData
import com.clementiano.simpleinstagram.data.PreferenceStore
import com.clementiano.simpleinstagram.network.ApiInterface
import com.squareup.picasso.Picasso
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PicturesAdapter(
    private val items: MutableList<MediaData>,
    private val graphApi: ApiInterface,
    private val preferenceStore: PreferenceStore) : RecyclerView.Adapter<PicturesAdapter.PictureHolder>() {

    val TAG: String = PicturesAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        return PictureHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pic, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        if (preferenceStore.auth == null) return
        graphApi.getMediaDetail(items[position].id, preferenceStore.auth!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ item ->
                holder.bindViews(item.media_url)
            }, {
                it.printStackTrace()
            })
    }

    inner class PictureHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindViews(url: String) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            Picasso.get().load(url).into(imageView)
        }
    }
}