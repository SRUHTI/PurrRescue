package com.sruthi.purrrescue.ui.reports

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.sruthi.purrrescue.data.model.Cat
import com.sruthi.purrrescue.databinding.ReportCatsFragmentLayoutBinding
import com.sruthi.purrrescue.utils.Utils
import java.util.Locale
import java.util.UUID

class ReportCatFragment: Fragment(), OnMapReadyCallback {

    private lateinit var binding: ReportCatsFragmentLayoutBinding
    private val viewModel: ReportCatViewModel by viewModels()
    private var photoUri: Uri? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var googleMap: GoogleMap? = null

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


        binding.tvUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }


        val toolTip = Balloon.Builder(requireContext())
            .setWidthRatio(0.5f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("Help us rescue this cat by: uploading photo, capturing location and describing their condition or behavior.")
            .setTextSize(13f)
            .setArrowSize(10)
            .setTextColor(resources.getColor(R.color.black))
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPosition(0.5f)
            .setWidthRatio(0.85f)
            .setPadding(12)
            .setCornerRadius(8f)
            .setBackgroundColorResource(R.color.white)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()


        binding.tvHeader.setOnClickListener {
            toolTip.showAlignTop(it)
        }


        binding.btnSubmit.setOnClickListener {
            val cat = Cat(
                catId = "1",
                description = binding.etDescription.text.toString(),
                country = binding.etCountry.text.toString(),
                state = binding.etState.text.toString(),
                city = binding.etCity.text.toString(),
                street = binding.etStreet.text.toString(),
                latitude = currentLat,
                longitude = currentLng,
                status = "Reported",
                reportedBy = "User123",
                reportedAt = System.currentTimeMillis()
            )

            if (photoUri != null) {
                viewModel.reportCat(cat, photoUri!!)
            } else {
                Utils.showToast(requireContext(), "Please upload a photo first")
            }
        }


        binding.btnCaptureLocation.setOnClickListener {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }



        viewModel.success.observe(viewLifecycleOwner) {
            if (it) Utils.showToast(requireContext(), "Cat reported successfully!")
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            Utils.showToast(requireContext(), message)
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

                    val catLocation = LatLng(currentLat, currentLng)
                    googleMap?.clear()
                    googleMap?.addMarker(MarkerOptions().position(catLocation).title("Cat Location"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(catLocation, 15f))

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(currentLat, currentLng, 1)
                    addresses?.firstOrNull()?.let { addr ->
                        binding.etCountry.setText(addr.countryName ?: "")
                        binding.etState.setText(addr.adminArea ?: "")
                        binding.etCity.setText(addr.locality ?: "")
                        binding.etStreet.setText(addr.thoroughfare ?: "")
                    }

                    Utils.showToast(requireContext(), "Location captured!")
                }
            }
        }
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        val catLocation = LatLng(currentLat, currentLng)
        googleMap?.addMarker(MarkerOptions().position(catLocation).title("Reported Cat"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(catLocation, 15f))

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }
    }


}