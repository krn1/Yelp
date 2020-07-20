package com.yelp.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yelp.databinding.ViewProductListItemBinding
import com.yelp.model.BusinessData
import java.util.*

class BusinessListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val businessList: MutableList<BusinessData> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: ViewProductListItemBinding =
            ViewProductListItemBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val viewHolder: BusinessListAdapter.ItemViewHolder =
            holder as BusinessListAdapter.ItemViewHolder
        viewHolder.onBind(position)
    }

    override fun getItemCount(): Int {
        return businessList.size
    }

    // region Helper Methods
    fun refresh(businessList: List<BusinessData>) {
        clear()
        loadPage(businessList)
    }

    fun loadPage(businessList: List<BusinessData>) {
        this.businessList.addAll(businessList)
        notifyDataSetChanged()
    }

    private fun clear() {
        businessList.clear()
        notifyDataSetChanged()
    }

    // endregion
    // region item View holder
    internal inner class ItemViewHolder(private val binding: ViewProductListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            val businessData: BusinessData = businessList[position]
            val imageUrl: String = businessData.imageUrl
            binding.image.setImageURI(imageUrl)
            binding.name.text = businessData.name
            binding.desc.text = "Category:  ${businessData.description}"
            binding.rating.text = "Ratings: ${businessData.rating}"
            binding.review.text = businessData.getReview()
        }

    } // endregion

}