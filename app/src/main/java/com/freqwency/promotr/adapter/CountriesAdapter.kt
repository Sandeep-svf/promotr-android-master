package com.freqwency.promotr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.freqwency.promotr.R
import com.freqwency.promotr.extensions.toFlagEmoji
import com.freqwency.promotr.model.CountryModel

class CountriesAdapter(
    val isArabic: Boolean
) : Filterable,
    RecyclerView.Adapter<CountriesAdapter.CountryCodesViewHolder>() {
    private var countries: List<CountryModel> = ArrayList()
    private var filteredCountryCodes: List<CountryModel> = ArrayList()
    var countryClick: ((CountryModel) -> Unit)? = null
    var isEmpty: ((Boolean) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryCodesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_country_phone_code, parent, false)
        return CountryCodesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryCodesViewHolder, position: Int) {
        val country = filteredCountryCodes[position]

        if (isArabic) {
            holder.countryName.text = country.nameAR
        } else {
            holder.countryName.text = country.name
        }
        holder.countryFlag.text = country.iso!!.toFlagEmoji()
        holder.countryCode.text = country.phoneCode

        holder.itemView.setOnClickListener {
            countryClick?.invoke(country)
        }
    }

    override fun getItemCount(): Int {
        return filteredCountryCodes.size
    }

    class CountryCodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryName: TextView = itemView.findViewById(R.id.countryName)
        val countryCode: TextView = itemView.findViewById(R.id.countryCode)
        val countryFlag: TextView = itemView.findViewById(R.id.countryFlag)

    }

    fun addData(countryCodes: List<CountryModel>) {
        this.countries = ArrayList()
        this.countries = countryCodes
        this.filteredCountryCodes = countryCodes
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_Cast")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()!!.lowercase()
                filteredCountryCodes = if (charString.isEmpty()) countries else {
                    val filteredList = ArrayList<CountryModel>()
                    countries.filter {
                        (it.name!!.lowercase().contains(charString) || it.nameAR!!.lowercase()
                            .contains(charString))
                    }.forEach { filteredList.add(it) }
                    filteredList
                }
                return FilterResults().apply { values = filteredCountryCodes }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredCountryCodes = if (results?.values == null)
                    ArrayList()
                else {
                    results.values as ArrayList<CountryModel>
                }

                isEmpty?.invoke(filteredCountryCodes.isNotEmpty())

                notifyDataSetChanged()
            }
        }
    }

}
