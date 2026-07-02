package com.sruthi.purrrescue.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.databinding.CatListLayoutBinding

class CatListAdapter(private val cats: List<Cat>): RecyclerView.Adapter<CatListAdapter.CatViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatViewHolder {
        val binding = CatListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CatViewHolder,
        position: Int
    ) {

        val data = cats[position]

        holder.binding.tvCatDescription.text = data.description
        holder.binding.tvLocation.text = data.country + ", " + data.state + ", " + data.city
        holder.binding.tvStatus.text = data.status


    }

    override fun getItemCount(): Int {
        return cats.size
    }

    class CatViewHolder(val binding: CatListLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    }

    fun updateCatList(newCats: List<Cat>) {

    }


}