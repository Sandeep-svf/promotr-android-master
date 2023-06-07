package com.freqwency.promotr.view.promoterprofile.adapters

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.application.PromotrApp
import com.freqwency.promotr.databinding.PromoterPromoItemBinding
import com.freqwency.promotr.view.promocodedetails.ui.PromoCodeDetailActivity
import com.freqwency.promotr.view.promocodedetails.ui.PromoCodeDetailPromoterActivity
import com.freqwency.promotr.view.promoterprofile.viewmodel.promoterListById.PromoterListByIdResponse

class PromoterPromoCodeAdapter(private val activity: Activity,private val promoterPromoList: ArrayList<PromoterListByIdResponse.Promocodes>
) : RecyclerView.Adapter<PromoterPromoCodeAdapter.ViewHolder>() {
    class ViewHolder(view: PromoterPromoItemBinding) : RecyclerView.ViewHolder(view.root) {
        val itemViewLayout: LinearLayout = view.itemView
        val promoTitleTxt: TextView = view.promoTitleTxt
        val promoCodetxt: TextView = view.promoCodetxt
        val copyBtn: TextView = view.copyBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PromoterPromoItemBinding = PromoterPromoItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = promoterPromoList!![position]

        holder.promoTitleTxt.text = item.title
        holder.promoCodetxt.text = item.code_id

        holder.copyBtn.setOnClickListener {
            copyTextToClipboard(holder)
        }

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

    private fun copyTextToClipboard(holder: ViewHolder) {
        val textToCopy = holder.promoCodetxt.text
        val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(activity, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    override fun getItemCount(): Int {
        return promoterPromoList?.size ?: 0
    }
}