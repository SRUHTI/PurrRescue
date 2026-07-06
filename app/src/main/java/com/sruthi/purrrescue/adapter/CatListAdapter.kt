package com.sruthi.purrrescue.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.databinding.CatListLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils

class CatListAdapter(
    private var cats: List<Cat>,
    private val onItemClick: (Cat) -> Unit
) : RecyclerView.Adapter<CatListAdapter.CatViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatViewHolder {
        val binding = CatListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val data = cats[position]

        holder.binding.tvCatDescription.text = data.description
        holder.binding.tvStatus.text = data.status
        holder.binding.tvLocation.text = data.country + ", " + data.state + ", " + data.city
        holder.binding.root.setOnClickListener {
            onItemClick(data)
        }

        when (data.status) {
            Constants.CAT_REPORTED -> {
                holder.binding.tvDate.text = Utils.formatDate(data.reportedAt)
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))

            }

            Constants.CAT_RESCUED -> {
                holder.binding.tvDate.text = data.rescuedOn?.let { Utils.formatDate(it) } ?: "Recently rescued"
                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))

            }
        }

    }

    override fun getItemCount(): Int = cats.size

    fun updateCatList(newCats: List<Cat>) {
        cats = newCats
        notifyDataSetChanged()
    }

    class CatViewHolder(val binding: CatListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}