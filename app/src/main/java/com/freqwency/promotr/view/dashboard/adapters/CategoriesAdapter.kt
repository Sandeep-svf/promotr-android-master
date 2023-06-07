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
import com.freqwency.promotr.databinding.CategoryItemBinding
import com.freqwency.promotr.view.dashboard.viewmodel.category.CategoryResponse
import com.freqwency.promotr.view.search.ui.SearchResultActivity

class CategoriesAdapter(private val activity: Context,
                        private val actions: ArrayList<CategoryResponse.Categorys>?
) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: CategoryItemBinding = CategoryItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.tvName.text = actions!![position].name

        val url = actions[position].full_image_url
        Glide
            .with(activity)
            .load(url)
            .placeholder(R.drawable.ic_promo)
            .into(holder.ivIcon)

        holder.itemViewLayout.setOnClickListener {
            val ids = actions!![position].id
            val name = actions!![position].name
            val intent = Intent(activity, SearchResultActivity::class.java)
            intent.putExtra("categoryId", ids)
            intent.putExtra("catName", name)
            intent.putExtra("type", "category")
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return actions?.size ?: 0
    }

    class CategoryViewHolder(view: CategoryItemBinding) : RecyclerView.ViewHolder(view.root) {
        val itemViewLayout: LinearLayout = view.girdItem
        val ivIcon: ImageView = view.ivIcon
        val tvName: TextView = view.tvTitle
    }
}
