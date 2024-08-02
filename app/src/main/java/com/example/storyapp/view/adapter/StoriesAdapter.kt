package com.example.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.room.StoryEntity
import com.example.storyapp.databinding.ItemStoriesBinding
import com.example.storyapp.view.detail.DetailStoriesActivity

class StoriesAdapter:
    PagingDataAdapter<StoryEntity, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(binding: ItemStoriesBinding):
        RecyclerView.ViewHolder(binding.root) {
        private val image = binding.storyImage
        private val name = binding.tvName
        private val desc = binding.tvDesc

        fun bind(listStories: StoryEntity){
            name.text = listStories.name
            desc.text = listStories.desc
            Glide.with(itemView.context)
                .load(listStories.photoUrl)
                .into(image)
            itemView.setOnClickListener {
                val goDetail = Intent(itemView.context, DetailStoriesActivity::class.java)
                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(image,"image"),
                        Pair(name, "name"),
                        Pair(desc,"description"),
                    )
                goDetail.putExtra(DetailStoriesActivity.EXTRA_ID , listStories.id)
                itemView.context.startActivity(goDetail, optionCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listStories = getItem(position)
        if (listStories != null) {
            holder.bind(listStories)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}