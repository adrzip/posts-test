package com.sampleprojects.poststest

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sampleprojects.poststest.model.Post
import kotlinx.android.synthetic.main.item_post.view.*
import java.sql.Date

class PostsAdapter : PagedListAdapter<Post, PostsAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var current = getItem(position)
        holder?.tvTitle?.text = current?.title
        holder?.tvAuthor?.text = current?.author
        if(current?.date != null) {
            var date = Date(current?.date)
            val dateFormat = android.text.format.DateFormat.getTimeFormat(holder.tvDate.context)
            holder?.tvDate?.text = dateFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
    }

    // Viewholder
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.text_title
        val tvAuthor = view.text_author
        val tvDate= view.text_date
    }

    // Paging library component
    object DIFF_CALLBACK: DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post?, newItem: Post?): Boolean {
            return oldItem?.id == newItem?.id
        }

        override fun areContentsTheSame(oldItem: Post?, newItem: Post?): Boolean {
            return oldItem == newItem
        }

    }
}