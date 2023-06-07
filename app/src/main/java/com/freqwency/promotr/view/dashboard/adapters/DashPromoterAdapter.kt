package com.freqwency.promotr.view.dashboard.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freqwency.promotr.R
import com.freqwency.promotr.databinding.DashPromoterItemBinding
import com.freqwency.promotr.view.dashboard.viewmodel.promotersList.PromotersListResponse
import com.freqwency.promotr.view.promoterprofile.ui.PromoterProfileActivity

class DashPromoterAdapter(
    private val activity: Context, private val actions: ArrayList<PromotersListResponse.Promoters>?
) :
    RecyclerView.Adapter<DashPromoterAdapter.ViewHolder>() {
    class ViewHolder(view: DashPromoterItemBinding) : RecyclerView.ViewHolder(view.root) {
        val itemViewLayout: LinearLayout = view.itemView
        val ivIcon: ImageView = view.ivIcon
        val tvPromoter: TextView = view.tvPromoter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: DashPromoterItemBinding =
            DashPromoterItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actions!![position].user

        val url = actions[position].full_image_url
        Glide
            .with(activity)
            .load(url)
            .placeholder(R.drawable.ic_promo)
            .into(holder.ivIcon)

        holder.tvPromoter.text = item!!.first_name + " " + item!!.last_name

        holder.itemViewLayout.setOnClickListener {
            val items = actions[position]
            val intent = Intent(activity, PromoterProfileActivity::class.java)
            intent.putExtra("promoterId", items.id)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return actions?.size ?: 0
    }
}