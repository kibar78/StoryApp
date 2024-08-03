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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.ViewModelFactory
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.adapter.LoadingStateAdapter
import com.example.storyapp.view.adapter.StoriesAdapter
import com.example.storyapp.view.maps.MapsActivity
import com.example.storyapp.view.upload.AddStoryActivity
import com.example.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: StoriesAdapter

    private var token = ""

    private val viewModel by viewModels<MainViewModel> {
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

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        adapter = StoriesAdapter()

        binding.rvStories.adapter =
            adapter.withLoadStateHeaderAndFooter(footer = LoadingStateAdapter {
                adapter.retry()
            }, header = LoadingStateAdapter {
                adapter.retry()
            })

        lifecycleScope.launch {
            viewModel.getSession.collect { token ->
                if (token.isNullOrEmpty()) {
                    showLoading(true)
                    navigateToWelcomeActivity()
                } else {
                    showLoading(false)
                    try {
                        viewModel.getStories(token = token).observe(this@MainActivity) {
                            adapter.submitData(lifecycle, it)
                        }
                    } catch (e: Exception) {
                        showToast(e.message.toString())
                    }
                }
            }
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout")
                        setMessage(getString(R.string.info_logout))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.logout()
                        }
                        setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
                        create()
                        show()
                    }
                    true
                }

                R.id.action_map -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()

    }
}
