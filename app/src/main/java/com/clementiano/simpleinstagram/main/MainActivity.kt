package com.clementiano.simpleinstagram.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clementiano.simpleinstagram.R
import com.clementiano.simpleinstagram.auth.AuthenticationActivity
import com.clementiano.simpleinstagram.data.PreferenceStore
import com.clementiano.simpleinstagram.databinding.ActivityMainBinding
import com.clementiano.simpleinstagram.network.RetrofitClient
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var prefStore: PreferenceStore
    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding

    private val graphApi = RetrofitClient.getGraphApiInterface()
    private val plainApi = RetrofitClient.getPlainApiInterface()

    private val adapter by lazy {
        PicturesAdapter(
            viewModel.mediaItems,
            graphApi,
            prefStore
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        prefStore = PreferenceStore(this)

        val factory = MainFactory(prefStore)
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)

        binding.apply {
            viewmodel = this@MainActivity.viewModel
            lifecycleOwner = this@MainActivity
            toolbar.title = prefStore.username
            setSupportActionBar(toolbar)
        }

        viewModel.loadProfileData()

        setupRecyclerView()
        observeChanges()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_logout)
            logout()
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            adapter = this@MainActivity.adapter
            layoutManager = GridLayoutManager(this@MainActivity, 3, RecyclerView.VERTICAL, false)
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadProfileData()
        }
    }

    private fun observeChanges() {
        viewModel.username.observe(this, Observer {
            binding.toolbar.title = it
        })

        viewModel.mediaFetched.subscribe {
            adapter.notifyDataSetChanged()
        }

        viewModel.profilePicUrl.observe(this, Observer {
            Picasso.get()
                .load(it)
                .resize(binding.circularImageView.width, binding.circularImageView.height)
                .centerCrop()
                .into(binding.circularImageView)
            if (swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isRefreshing = false
        })
    }


    private fun logout() {
        prefStore.logout()
        startActivity(Intent(this, AuthenticationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}
