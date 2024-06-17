package com.example.storyapp.view.Detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.response.Story
import com.example.storyapp.databinding.ActivityDetailStoriesBinding
import kotlinx.coroutines.launch

class DetailStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoriesBinding

    private val viewModel by viewModels<DetailStoriesViewModel>(){
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idUser = intent.getStringExtra(EXTRA_ID)

        lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.getSession.collect { token ->
                            if (!token.isNullOrEmpty()) {
                                if (idUser != null) {
                                    viewModel.getDetailStories(idUser)
                                }
                            }
                        }
                    }
                }
            }

            viewModel.detailStories.observe(this){result->
                when(result){
                    is ResultState.Loading->{
                        showLoading(true)
                    }
                    is ResultState.Success->{
                        setStories(result.data)
                        showLoading(false)
                    }
                    is ResultState.Error->{
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    private fun setStories(detailStories: Story?){
        Glide.with(this)
            .load(detailStories?.photoUrl)
            .into(binding.imgView)
        binding.tvName.text = detailStories?.name.toString()
        binding.tvDesc.text = detailStories?.description.toString()
    }
    private fun showLoading(isLoading: Boolean){
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object{
        const val EXTRA_ID = "user.id"
    }
}