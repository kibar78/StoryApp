package com.example.storyapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.datastore.UserPreferences
import com.example.storyapp.data.local.datastore.dataStore
import com.example.storyapp.data.remote.ApiConfig
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.StoriesResponse
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val pref = UserPreferences.getInstance(mContext.dataStore)
    private val user = runBlocking { pref.getSession().first() }

    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {
        TODO("Not yet implemented")
    }

    override fun onDataSetChanged() {
        fetchStoryImages()
    }

    private fun fetchStoryImages() {
        val client = ApiConfig.getApiService().getWidgetStories("$user")
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>, response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    handleApiResponse(response.body()?.listStory as List<ListStoryItem>)
                } else {
                    Log.d("widget", response.message().toString())
                }
            }
            override fun onFailure(
                call: Call<StoriesResponse>, t: Throwable
            ) {
                Log.d("widget", t.message.toString())
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun handleApiResponse(storyImages: List<ListStoryItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mWidgetItems.clear()

                for (storyImage in storyImages) {
                    val imageUrl = storyImage.photoUrl

                    val bitmap = Glide.with(mContext).asBitmap().load(imageUrl).submit().get()
                    mWidgetItems.add(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[p0])

        val extras = bundleOf(
            StoryWidget.EXTRA_ITEM to p0
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}