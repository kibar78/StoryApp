package com.example.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ItemStoriesBinding
import com.example.storyapp.view.Detail.DetailStoriesActivity

class StoriesAdapter: ListAdapter<ListStoryItem, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(binding: ItemStoriesBinding): RecyclerView.ViewHolder(binding.root) {

        private val image = binding.storyImage
        private val name = binding.tvName
        private val desc = binding.tvDesc

        fun bind(listStories: ListStoryItem){
            name.text = listStories.name
            desc.text = listStories.description
            Glide.with(itemView.context)
                .load(listStories.photoUrl)
                .into(image)

            itemView.setOnClickListener {
                val goDetail = Intent(itemView.context, DetailStoriesActivity::class.java)
                goDetail.putExtra(DetailStoriesActivity.EXTRA_ID , listStories.id)
                val optionCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(image,"image"),
                        Pair(name, "name"),
                        Pair(desc,"description"),
                    )
                itemView.context.startActivity(goDetail, optionCompat.toBundle())
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listStories = getItem(position)
        holder.bind(listStories)
    }

}