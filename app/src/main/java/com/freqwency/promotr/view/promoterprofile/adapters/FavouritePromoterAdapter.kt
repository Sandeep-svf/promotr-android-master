package com.freqwency.promotr.view.promoterprofile.adapters

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
import com.freqwency.promotr.databinding.FavouritePromoterItemBinding
import com.freqwency.promotr.view.promoterprofile.ui.PromoterProfileActivity
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterShowFav.PromoterShowFavResponse

class FavouritePromoterAdapter(private val activity: Context,
                               private val actions: ArrayList<PromoterShowFavResponse.FavoritePromoters>
) :
    RecyclerView.Adapter<FavouritePromoterAdapter.ViewHolder>() {
    class ViewHolder(view: FavouritePromoterItemBinding) : RecyclerView.ViewHolder(view.root) {
        val itemViewLayout: LinearLayout = view.itemView
        val ivIcon: ImageView = view.ivIcon
        val tvPromoter: TextView = view.tvPromoter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FavouritePromoterItemBinding =
            FavouritePromoterItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actions[position].nickname

        val url = actions[position].full_image_url
        Glide.with(activity)
            .load(url)
            .placeholder(R.drawable.ic_promo)
            .into(holder.ivIcon)

       //holder.tvPromoter.text = item!!.first_name + " " + item!!.last_name
        holder.tvPromoter.text = item

        holder.itemViewLayout.setOnClickListener {
            val items = actions[position]
            val intent = Intent(activity, PromoterProfileActivity::class.java)
            intent.putExtra("promoterId", items.id)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return actions.size
    }
}