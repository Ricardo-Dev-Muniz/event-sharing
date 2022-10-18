package com.app.co.event_sharing.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.app.co.event_sharing.data.Event
import com.app.co.event_sharing.util.launchImage
import com.app.co.event_sharing.listener.OnclickListener
import com.app.co.event_sharing.databinding.ItemEventsBinding

class EventsAdapter(
    private val context: Context,
    private val mutableList: List<Event?>?,
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private lateinit var listener: OnclickListener

    fun onClickItem(events: OnclickListener) {
        listener = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemEventsBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return ViewHolder(itemBinding, listener, context)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mutableList?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = mutableList?.size!!

    override fun getItemViewType(position: Int): Int = mutableList?.size!!

    class ViewHolder(
        private val binding: ItemEventsBinding,
        private val listenerItem: OnclickListener,
        private val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Event) {
            val fade: Animation = AlphaAnimation(0f, 1f).apply {
                interpolator = DecelerateInterpolator()
                duration = 1000
            }

            val animation = AnimationSet(false)
            animation.addAnimation(fade)

            binding.root.setOnClickListener {
                data.id?.let { listenerItem.flowId(it) }
            }

            launchImage(data.image, size = 100, context = context) {
                binding.ivEvent.setImageBitmap(it)
                binding.ivEvent.animation = animation
            }

            binding.tvTitle.text = data.title
            binding.tvDescription.text = data.description
        }
    }
}