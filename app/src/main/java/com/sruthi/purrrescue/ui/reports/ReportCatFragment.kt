package com.sruthi.purrrescue.ui.reports

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.databinding.ReportCatsFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils
import java.util.Locale
import java.util.UUID
import com.sruthi.purrrescue.R
import com.sruthi.purrrescue.base.BaseFragment

class ReportCatFragment: BaseFragment() {

    private lateinit var binding: ReportCatsFragmentLayoutBinding
    private val viewModel: ReportCatViewModel by viewModels()
    private var photoUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                photoUri = it
                binding.ivCatImage.setImageURI(it)
                binding.ivCatImage.visibility = View.VISIBLE
                binding.tvUploadImage.visibility = View.GONE
            }
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                captureLocation()
            } else {
                Utils.showToast(requireContext(), "Location permission denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReportCatsFragmentLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.apply {
            textWatcher(tilCountry, etCountry)
            textWatcher(tilState, etState)
            textWatcher(tilCity, etCity)
            textWatcher(tilStreet, etStreet)
            textWatcher(tilDescription, etDescription)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) showLoading() else hideLoading()
        }

        binding.tvUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack(R.id.home, false)
        }

        val toolTip = Balloon.Builder(requireContext())
            .setWidthRatio(0.5f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("Help us rescue this cat by: uploading photo, capturing location and describing their condition or behavior.")
            .setTextSize(13f)
            .setArrowSize(10)
            .setTextColor(resources.getColor(R.color.orange))
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPosition(0.5f)
            .setWidthRatio(0.85f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.white)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        binding.ivInfo.setOnClickListener {
            toolTip.showAlignTop(it)
        }

        binding.btnSubmit.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            val cat = Cat(
                catId = UUID.randomUUID().toString(),
                description = binding.etDescription.text.toString().trim(),
                country = binding.etCountry.text.toString().trim(),
                state = binding.etState.text.toString().trim(),
                city = binding.etCity.text.toString().trim(),
                street = binding.etStreet.text.toString().trim(),
                latitude = currentLat,
                longitude = currentLng,
                status = "Reported",
                reportedBy = FirebaseAuth.getInstance().currentUser?.displayName
                    ?: FirebaseAuth.getInstance().currentUser?.email
                    ?: "unknown",
                reportedAt = System.currentTimeMillis()
            )

            binding.btnSubmit.isEnabled = false
            viewModel.reportCat(cat, photoUri!!, requireContext())
        }

        binding.btnCaptureLocation.setOnClickListener {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                Utils.showToast(requireContext(), "Cat reported successfully!")
                findNavController().popBackStack(R.id.home, false)
            } else {
                binding.btnSubmit.isEnabled = true
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
            binding.btnSubmit.isEnabled = true
        }
    }

    private fun captureLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLat = it.latitude
                    currentLng = it.longitude

                    try {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses = geocoder.getFromLocation(currentLat, currentLng, 1)
                        addresses?.firstOrNull()?.let { addr ->
                            binding.etCountry.setText(addr.countryName ?: "")
                            binding.etState.setText(addr.adminArea ?: "")
                            binding.etCity.setText(addr.locality ?: addr.subAdminArea ?: "")
                            binding.etStreet.setText(
                                addr.thoroughfare ?: addr.subLocality ?: addr.featureName ?: ""
                            )
                        } ?: Utils.showToast(requireContext(), "Couldn't resolve address for this location")
                    } catch (e: Exception) {
                        Utils.showToast(requireContext(), "Address lookup failed, please enter manually")
                    }

                    Utils.showToast(requireContext(), "Location captured!")
                } ?: Utils.showToast(requireContext(), "Couldn't get current location, please try again")
            }
        }
    }

    private fun validateForm(): Boolean {

        if (photoUri == null) {
            Utils.showToast(requireContext(), "Please upload a photo first")
            return false
        }

        if (currentLat == 0.0 && currentLng == 0.0) {
            Utils.showToast(requireContext(), "Please capture the cat's location")
            return false
        }

        if (binding.etCountry.text.toString().trim().isEmpty()) {
            binding.tilCountry.error = "Country is required"
            return false
        }

        if (binding.etState.text.toString().trim().isEmpty()) {
            binding.tilState.error = "State is required"
            return false
        }

        if (binding.etCity.text.toString().trim().isEmpty()) {
            binding.tilCity.error = "City is required"
            return false
        }

        if (binding.etStreet.text.toString().trim().isEmpty()) {
            binding.tilStreet.error = "Street is required"
            return false
        }

        if (binding.etDescription.text.toString().trim().isEmpty()) {
            binding.tilDescription.error = "Please describe the cat's condition or behavior"
            return false
        }

        return true
    }

    private fun textWatcher(til: TextInputLayout, et: TextInputEditText) {
        et.doOnTextChanged { text, start, before, count ->
            til.error = null
        }
    }
}