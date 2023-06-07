package com.freqwency.promotr.view.favourite

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
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.PromoItemBinding
import com.freqwency.promotr.view.favourite.viewmodel.PromocodeShowFavResponse
import com.freqwency.promotr.view.promocodedetails.ui.PromoCodeDetailActivity
import com.freqwency.promotr.view.promocodedetails.ui.PromoCodeDetailPromoterActivity

class FavouriteAdapter(private val activity: Context,
                       private val actions: ArrayList<PromocodeShowFavResponse.FavoritePromoCodes>
) :
    RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    class ViewHolder(view: PromoItemBinding) : RecyclerView.ViewHolder(view.root) {
        val itemViewLayout: LinearLayout = view.itemView
        val ivIcon: ImageView = view.ivIcon
        val tvPromo: TextView = view.tvPromo
        val tvPromoDescription: TextView = view.tvPromoDescription
        val tvPromoType: TextView = view.tvPromoType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PromoItemBinding = PromoItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actions!![position]

        val url = actions[position].full_image_url
        Glide
            .with(activity)
            .load(url)
            .placeholder(R.drawable.ic_promo)
            .into(holder.ivIcon)

        holder.tvPromo.text = item.code_id
        holder.tvPromoDescription.text = item.description
        holder.tvPromoType.text = item.title
        holder.tvPromoType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shop, 0, 0, 0);

        holder.itemViewLayout.setOnClickListener {
            val promoterIdApi = item.promoter_id.toString()
            val promoterId = PromotrApp.encryptedPrefs.promoterId

            if (promoterIdApi.equals(promoterId)) {
                val intent = Intent(activity, PromoCodeDetailPromoterActivity::class.java)
                intent.putExtra("categoryId", item.id)
                activity.startActivity(intent)
            } else {
                val intent = Intent(activity, PromoCodeDetailActivity::class.java)
                intent.putExtra("categoryId", item.id)
                activity.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return actions?.size ?: 0
    }
}