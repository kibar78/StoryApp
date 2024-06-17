package com.example.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.ResultState
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.adapter.StoriesAdapter
import com.example.storyapp.view.upload.AddStoryActivity
import com.example.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.getSession.collect{token->
                        if (token.isNullOrEmpty()) {
                            navigateToWelcomeActivity()
                        } else {
                            viewModel.getStories()
                        }
                    }
                }
            }
        }

        viewModel.listStories.observe(this){result->
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

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        val storiesDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(storiesDecoration)

        binding.toolbar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.action_logout ->{
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout")
                        setMessage(getString(R.string.info_logout))
                        setPositiveButton(getString(R.string.yes)){_,_ ->
                            viewModel.logout()
                        }
                        setNegativeButton(getString(R.string.no)){dialog,_ -> dialog.cancel() }
                        create()
                        show()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setStories(dataStories: List<ListStoryItem?>){
        val adapter = StoriesAdapter()
        adapter.submitList(dataStories)
        binding.rvStories.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean){
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }
}