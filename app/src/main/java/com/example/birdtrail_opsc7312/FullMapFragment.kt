package com.example.birdtrail_opsc7312

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentFullMapBinding
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.lang.ref.WeakReference
import java.util.*


class FullMapFragment : Fragment(R.layout.fragment_full_map) {

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private lateinit var mapView: MapView

    private var _binding: FragmentFullMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFullMapBinding.inflate(inflater, container, false)
        val view = binding.root

        mapView = MapView(requireContext())
        view.addView(mapView) // Add the MapView to your layout

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
        }


        locationPermissionHelper = LocationPermissionHelper(WeakReference(activity))
        locationPermissionHelper.checkPermissions {
            onMapReady()
            addAnnotationsToMap()
        }
        onMapReady()
        return view
    }


    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
            addAnnotationsToMap()
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.imguser_location,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun addAnnotationToMap() {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.imglocation_pin
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
            // Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(Point.fromLngLat(18.62, -33.9))
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(it)
            // Add the resulting pointAnnotation to the map.

            pointAnnotationManager?.addClickListener {
                // Show a Toast message
                Toast.makeText(requireContext(), "Location Name", Toast.LENGTH_SHORT).show()
                false
            }
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun addAnnotationsToMap() {
        //Toast.makeText(requireContext(), checkPermissions().toString(), Toast.LENGTH_SHORT).show()
        if (checkPermissions())
        {
            // Create an instance of the Annotation API and get the PointAnnotationManager.
            bitmapFromDrawableRes(
                requireContext(),
                R.drawable.imglocation_pin
            )?.let { bitmap ->
                val annotationApi = mapView?.annotations
                val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
                // Get the user's current location.
                var userLocation: Location? = null
                var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    userLocation = task.result
                    if (userLocation != null) {
                        // Iterate over each hotspot in the global list
                        for (hotspot in GlobalClass.hotspots) {
                            // Calculate the distance between the user's location and the hotspot.
                            val distanceInKm = calculateDistance(userLocation!!.latitude, userLocation!!.longitude, hotspot.lat!!, hotspot.lng!!)
                            // If the distance is less than or equal to 50km, add an annotation for this hotspot.
                            if (distanceInKm <= 200) {
                                // Set options for the resulting symbol layer.
                                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                                    // Define a geographic coordinate from the hotspot's lat and lng.
                                    .withPoint(Point.fromLngLat(hotspot.lng!!, hotspot.lat!!))
                                    // Specify the bitmap you assigned to the point annotation
                                    // The bitmap will be added to map style automatically.
                                    .withIconImage(bitmap)
                                // Add the resulting pointAnnotation to the map.
                                pointAnnotationManager?.create(pointAnnotationOptions)
                            }
                        }
                        pointAnnotationManager?.addClickListener { pointAnnotation ->
                            // Show a Toast message with the location name of the clicked annotation

                            val clickedHotspotIndex = GlobalClass.hotspots.indexOfFirst { it.lng == pointAnnotation.point.longitude() && it.lat == pointAnnotation.point.latitude() }

                            val mapHotspotView = MapHotspot()
                            val args = Bundle()

                            args.putInt("hotspotIndex", clickedHotspotIndex)

                            mapHotspotView.arguments = args


                            val transaction = parentFragmentManager.beginTransaction()
                            transaction.replace(R.id.flContent, mapHotspotView)
                            transaction.addToBackStack(null)
                            transaction.commit()

                            false
                        }
                    }
                }
            }
        }
    }



    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }



    // Function to calculate distance between two points in km using Haversine formula
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadiusKm * c
    }








    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))


    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


}