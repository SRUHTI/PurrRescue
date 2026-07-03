package com.sruthi.purrrescue.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sruthi.purrrescue.databinding.CatDetailsFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils

class CatDetailsFragment : Fragment() {

    private lateinit var binding: CatDetailsFragmentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = CatDetailsFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CatDetailsFragmentArgs.fromBundle(requireArguments())
        val cat = args.catDetails


        binding.apply {
            tvHeaderCatDetails.text = "Cat Details - ID: ${cat.catId}"
            tvCatDescription.text = cat.description
            tvLocation.text = "${cat.street}, ${cat.city}"
            tvStatus.text = cat.status
            tvReportedByTimeLine.text = Utils.formatDate(cat.reportedAt)
            tvRescuedByHeader.text = cat.rescuedAt?.let { Utils.formatDate(it) } ?: "Not yet rescued"
        }

        binding.tvHeaderCatDetails.setOnClickListener {
            Utils.shareViewAsImage(requireContext(), binding.clCatDetailsLayout)
        }
    }

}