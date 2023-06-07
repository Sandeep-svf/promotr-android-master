package com.freqwency.promotr.view.notification.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.databinding.NotificationItemBinding
import com.freqwency.promotr.view.notification.viewmodel.notificationlist.NotificationListResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class NotificationsAdapter(
    private val activity: Activity,
    private val actions: ArrayList<NotificationListResponse.Notifications>?
) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    var is_read: Boolean = false

    class ViewHolder(view: NotificationItemBinding) : RecyclerView.ViewHolder(view.root) {

        val itemViewLayout: LinearLayout = view.itemView
        val tvName: TextView = view.tvNotif
        val tvDate: TextView = view.tvDate
        val checkbox: ImageView = view.deleteImg
        val unreadIcon: ImageView = view.unreadIcon

        fun bind(
            get: NotificationListResponse.Notifications,
            holder: ViewHolder,
            activity: Activity
        ) {
            itemViewLayout.setOnClickListener(View.OnClickListener {
                get.isSelect = !get.isSelect
                checkbox.setVisibility(if (get.isSelect) View.VISIBLE else View.VISIBLE)
                if (get.isSelect) {
                    Log.e("Closet_Item", "     true123")
                    checkbox.isVisible = true
                    //Glide.with(activity).load(R.drawable.circle_gray).into(holder.checkbox)
                } else {
                    checkbox.isVisible = false
                    Log.e("Closet_Item", "     true")
                    //Glide.with(activity).load(R.drawable.delete).into(holder.checkbox)
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: NotificationItemBinding = NotificationItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actions!![position]
        holder.bind(actions[position], holder, activity)
        holder.tvName.text = item.body
        val create_date = item.created_at

        is_read = item.is_read!!

        holder.unreadIcon.isVisible = !is_read

        val inputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        val outputFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
        val date: LocalDate = LocalDate.parse(create_date, inputFormatter)
        val formattedDate: String = outputFormatter.format(date)
        holder.tvDate.text = formattedDate
    }

    override fun getItemCount(): Int {
        return actions!!.size
    }

    fun getSelected(): List<NotificationListResponse.Notifications>? {
        val selected: MutableList<NotificationListResponse.Notifications> =
            ArrayList<NotificationListResponse.Notifications>()
        for (i in actions!!.indices) {
            if (actions.get(i).isSelect) {
                selected.add(actions.get(i))
            }
        }
        return selected
    }

}
