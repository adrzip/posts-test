package com.sampleprojects.poststest

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import com.sampleprojects.poststest.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.layout_empty_posts.*
import android.view.MenuItem
import android.app.ActivityManager
import android.content.Context
import android.support.design.widget.Snackbar
import com.sampleprojects.poststest.services.AutoPopulateDBService

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var postsAdapter: PostsAdapter
    private lateinit var viewModel: MainViewModel
    private var snackbar: Snackbar? = null

    private var lastKnownCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        frame_empty.visibility = View.VISIBLE
        swipe_layout.visibility = View.GONE

        // load viewmodel
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java!!)

        button_reload.setOnClickListener {
            Log.d(TAG, "reload button click")
            viewModel.initPosts(this)
        }

        postsAdapter = PostsAdapter()
        loadPosts()
        checkService()
        swipe_layout.setOnRefreshListener { loadPosts() }
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = postsAdapter
        // Fix for recyclerview in a swipelayout
        recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val topRowVerticalPosition = if (recyclerView == null || recyclerView.childCount === 0) 0 else recyclerView.getChildAt(0).top
                swipe_layout.isEnabled = topRowVerticalPosition >= 0
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.option_clear -> {
                Log.d(TAG, "option clear selected")
                viewModel.clearPosts(this)
                true
            }
            R.id.option_start_autoadd -> {
                Log.d(TAG, "option start auto-ad selected")
                checkService()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadPosts() {
        swipe_layout.isRefreshing = true
        viewModel.getPosts(this)?.observe(this, Observer {
            swipe_layout.isRefreshing = false
            if(it != null && it.count() > 0) {
                val count = it.count()
                Log.d(TAG, "posts update - $count")

                postsAdapter.submitList(it)
                frame_empty.visibility = View.GONE
                swipe_layout.visibility = View.VISIBLE
                var diff = count - lastKnownCount
                if(lastKnownCount == 0) {
                    lastKnownCount = count
                } else {
                    if(snackbar == null) {
                        snackbar = Snackbar.make(main_content, "$diff new Posts", Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.view, {
                                    recycler_view.smoothScrollToPosition(0)
                                    lastKnownCount = count
                                })
                    } else {
                        snackbar?.setText("$diff new Posts")
                    }
                    snackbar?.show()
                }
                postsAdapter.notifyDataSetChanged()
            } else {
                frame_empty.visibility = View.VISIBLE
                swipe_layout.visibility = View.GONE
            }
        })
    }


    private fun checkService() {
        if (!isMyServiceRunning(AutoPopulateDBService::class.java)) {
            StartServiceDialogFragment().show(fragmentManager, "dialog_start_service")
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
