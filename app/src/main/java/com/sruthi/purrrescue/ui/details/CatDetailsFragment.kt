package com.sruthi.purrrescue.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.databinding.CatDetailsFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils

class CatDetailsFragment : Fragment() {

    private lateinit var binding: CatDetailsFragmentLayoutBinding
    private val viewModel: CatDetailsViewModel by viewModels()

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


       binding.btnMarkRescued.visibility = if (cat.status == Constants.CAT_REPORTED) View.VISIBLE else View.GONE

        binding.apply {
            tvHeaderCatDetails.text = "Cat Details - ID: ${cat.catId}"
            tvCatDescription.text = cat.description
            tvLocation.text = "${cat.street}, ${cat.city}"
            tvStatus.text = cat.status
            tvReportedByTimeLine.text = Utils.formatDate(cat.reportedAt)
            tvRescuedByHeader.text = cat.rescuedAt?.let { Utils.formatDate(it) } ?: "Not yet rescued"

            Glide.with(requireContext())
                .load(cat.imageUrl)
                .placeholder(R.drawable.image_view)
                .error(R.drawable.image_view)
                .centerCrop()
                .into(ivRecOne)
        }

        binding.tvHeaderCatDetails.setOnClickListener {
            Utils.shareViewAsImage(requireContext(), binding.clCatDetailsLayout)
        }

        binding.btnMarkRescued.setOnClickListener {
            binding.btnMarkRescued.isEnabled = false
            viewModel.markRescued(cat.catId, System.currentTimeMillis())
        }

        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                Utils.showToast(requireContext(), "Cat has been resued")
                val now = System.currentTimeMillis()
                binding.tvStatus.text = Constants.CAT_RESCUED
                binding.tvRescuedByHeader.text = Utils.formatDate(now)
                binding.btnMarkRescued.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
            binding.btnMarkRescued.isEnabled = true
            binding.tvStatus.text = Constants.CAT_REPORTED

        }
    }

}