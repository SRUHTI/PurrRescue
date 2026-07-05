package com.sruthi.purrrescue.ui.details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.base.BaseFragment
import com.sruthi.purrrescue.databinding.CatDetailsFragmentLayoutBinding
import com.sruthi.purrrescue.databinding.DialogCatImageLayoutBinding
import com.sruthi.purrrescue.utils.Constants
import com.sruthi.purrrescue.utils.Utils

class CatDetailsFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var args: CatDetailsFragmentArgs
    private lateinit var binding: CatDetailsFragmentLayoutBinding
    private val viewModel: CatDetailsViewModel by viewModels()
    private var googleMap: GoogleMap? = null

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

        args = CatDetailsFragmentArgs.fromBundle(requireArguments())
        val cat = args.catDetails

        if (cat.status == Constants.CAT_REPORTED) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

        if (cat.status == Constants.CAT_RESCUED) {
            binding.trCatImage.visibility = View.VISIBLE
            binding.btnMarkRescued.visibility = View.GONE
            binding.trMap.visibility = View.GONE
            binding.btnShowCatImage.visibility = View.GONE
        } else {
            binding.trCatImage.visibility = View.GONE
            binding.btnMarkRescued.visibility = View.VISIBLE
            binding.trMap.visibility = View.VISIBLE
            binding.btnShowCatImage.visibility = View.VISIBLE
        }

        binding.btnShowCatImage.setOnClickListener {
            showImageDialog(cat.imageUrl)
        }

        binding.apply {
            tvHeaderCatDetails.text = "Cat ID: ${cat.catId}"
            tvCatDescription.text = cat.description
            tvLocation.text = "${cat.street}, ${cat.city}"
            tvStatus.text = cat.status
            tvReportedBy.text = cat.reportedBy
            tvReportedOnTimeLine.text = Utils.formatDate(cat.reportedAt)
            tvRescuedBy.text = cat.rescuedBy ?: "Not yet rescued"
            tvRescuedOn.text = cat.rescuedOn?.let { Utils.formatDate(it) } ?: "Not yet rescued"

            Glide.with(requireContext())
                .load(cat.imageUrl)
                .placeholder(R.drawable.paws)
                .error(R.drawable.paws)
                .centerCrop()
                .into(ivCatImage)
        }

        binding.ivShare.setOnClickListener {
            Utils.shareViewAsImage(requireContext(), binding.clCatDetailsLayout)
        }

        binding.btnMarkRescued.setOnClickListener {
            alertDialog()
        }

        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnMarkRescued.visibility = View.GONE
                binding.clDetailLayout.visibility = View.GONE
                binding.successScreen.visibility = View.VISIBLE

                binding.btnDone.setOnClickListener {
                    findNavController().navigate(
                        R.id.home, null,
                        NavOptions.Builder().setPopUpTo(R.id.home, inclusive = false).build()
                    )
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
            binding.btnMarkRescued.isEnabled = true
            binding.tvStatus.text = Constants.CAT_REPORTED

        }
    }

    private fun showImageDialog(imageUrl: String) {
        if (imageUrl.isBlank()) {
            Utils.showToast(requireContext(), "No image available for this cat")
            return
        }

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val dialogBinding = DialogCatImageLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        Glide.with(this)
            .load(imageUrl)
            .into(dialogBinding.ivFullImage)

        dialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun alertDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.RescueDialogTheme)
            .setTitle("Mark as Rescued?")

            .setMessage("Are you sure this cat has been rescued? This action can't be undone.")
            .setIcon(R.drawable.crying_cat) // optional cat icon
            .setCancelable(false)
            .setPositiveButton("Yes, Rescued") { dialog, _ ->
                binding.btnMarkRescued.isEnabled = false
                viewModel.markRescued(args.catDetails.catId, System.currentTimeMillis(),
                    FirebaseAuth.getInstance().currentUser?.displayName
                        ?: FirebaseAuth.getInstance().currentUser?.email
                        ?: "unknown",)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val cat = args.catDetails
        val catLocation = LatLng(13.1322213,
            80.2434581)

        googleMap?.addMarker(
            MarkerOptions()
                .position(catLocation)
                .title("Cat reported here")
        )
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(catLocation, 15f))


        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = false
            setAllGesturesEnabled(false)
        }
    }


}