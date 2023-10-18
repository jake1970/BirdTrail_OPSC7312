package com.example.birdtrail_opsc7312

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.text.Transliterator.Position
import android.location.Location
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.birdtrail_opsc7312.databinding.FragmentFullMapBinding
import com.google.android.gms.location.LocationServices
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
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
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

    //private val onMoveListener = object : OnMoveListener {
    var onMoveListener = object : OnMoveListener {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFullMapBinding.inflate(inflater, container, false)
        val view = binding.root



        mapView = MapView(requireContext())
        view.addView(mapView) // Add the MapView to your layout


        //---------------------------------------------------------
        //Hide the compass
        //---------------------------------------------------------
        mapView.compass.visibility = false
        //---------------------------------------------------------


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





    @RequiresApi(Build.VERSION_CODES.O)
     fun onMapReady() {
/*
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )

 */
        //---------------------------------------------------------------------------

        if (centerOnHotspot == true)
        {

            val hotspotLong = arguments?.getDouble("hotspotLong")
            val hotspotLat = arguments?.getDouble("hotspotLat")


/*
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Androidly Alert")
                builder.setMessage(hotspotLong.toString() + " " + hotspotLat.toString())
                builder.show()

 */

            val hotspotPoint = hotspotLong?.let { hotspotLat?.let { it1 -> Point.fromLngLat(it, it1) } }

            val cameraPosition = CameraOptions.Builder()
                .zoom(14.0)
                .center(hotspotPoint)
                .build()
            // set camera position
            mapView.getMapboxMap().setCamera(cameraPosition)


                        /*
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(hotspotLong?.let {
                        hotspotLat?.let { it1 ->
                            Point.fromLngLat(
                                it, it1
                            )
                        }
                    }
                    )
                    .zoom(14.0)
                    .build()
            )

                         */
        }
        else
        {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .zoom(14.0)
                    .build()
            )
        }


        //---------------------------------------------------------------------------

        loadMap()

        // Accuracy ring is only shown when zoom is greater than or equal to 18




//        mapView.getMapboxMap().loadStyleUri(
//            Style.MAPBOX_STREETS
//        ) {
//            initLocationComponent()
//            setupGesturesListener()
//            addAnnotationsToMap(60)
//        }
    }



    //8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

    fun removeAllAnnotations() {
        val annotationApi = mapView?.annotations
        val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
        pointAnnotationManager?.let { manager ->
            val annotations = manager.annotations
            manager.delete(annotations)
        }
    }

    //8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

    @RequiresApi(Build.VERSION_CODES.O)
    public fun loadMap()
    {
        //distance = newDistance
        mapView.invalidate()
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {

            addSightingAnnotationsToMap()

            initLocationComponent()
            setupGesturesListener()
            addAnnotationsToMap()
        }
    }



    //----------------------------------------------------------------------------------------------------------------------------
    var openInFullView = false
    var centerOnHotspot = false
    var filterDistance = 50.0
    var filterTimeFrame = 1
    var filterSearchBirdName = ""
    //----------------------------------------------------------------------------------------------------------------------------


    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)

        //----------------------------------------------------------------------------------------------------------------------------
        mapView.gestures.addOnMapClickListener{ point ->


            if (openInFullView)
            {
                openInFullView = false
                //create local fragment controller
                val fragmentControl = FragmentHandler()
                fragmentControl.replaceFragment(UserFullMapView(), R.id.flContent, parentFragmentManager)
            }

            true
        }
        //----------------------------------------------------------------------------------------------------------------------------
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
        if (arguments?.getBoolean("hotspotCamera") != true)
        {
            locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)

        }
    }

    //private fun onCameraTrackingDismissed() {
     fun onCameraTrackingDismissed() {
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

    fun getUserLocation() : Location?
    {



        var userLocation: Location? = null
        if (checkPermissions())
        {

          //  var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

            var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                userLocation = task.result
            }



                //  (requireActivity()) { task ->
               // userLocation = mFusedLocationClient.lastLocation.result//task.result
                //}
        }
        return userLocation
    }



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission", "SetTextI18n")
    public fun addAnnotationsToMap() {

        var counterMap = 0 //88888888888888888888888888888888888888888888

        removeAllAnnotations()
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
                            val distanceInKm = calculateDistance(
                                userLocation!!.latitude,
                                userLocation!!.longitude,
                                hotspot.lat!!,
                                hotspot.lng!!
                            )

                            // Define the date format pattern for your input string
                            val inputPattern = "yyyy-MM-dd HH:mm"

                            // Define the desired date format pattern for the output
                            val outputPattern = "yyyy-MM-dd"

                            val inputFormat = SimpleDateFormat(inputPattern)
                            val outputFormat = SimpleDateFormat(outputPattern)

                            var date = inputFormat.parse(hotspot.obsDt)
                            var localDate = outputFormat.format(date)

                            val daysBetween =
                                Period.between(LocalDate.parse(localDate), LocalDate.now()).days

                            var commonBirdName = hotspot.comName?.lowercase()

                            if (GlobalClass.currentUser.isMetric == false)
                            {
                                filterDistance = milesToKilometers(filterDistance)
                            }


                           // if (commonBirdName?.contains(filterSearchBirdName.lowercase()) == true || filterSearchBirdName == "") {
                            if (filterSearchBirdName.lowercase() in commonBirdName!! || filterSearchBirdName == "") {
                                if (daysBetween <= (filterTimeFrame * 7)) {
                                    if (distanceInKm <= filterDistance) { //200

                                       // counterMap = counterMap + 1

                                        /*
                                         defaultUserImage = defaultUserImage.mutate()
            defaultUserImage.colorFilter = PorterDuffColorFilter(context.resources.getColor(R.color.sub_grey), PorterDuff.Mode.SRC_IN)
                                         */
                                       // var tintColor = resources.getColor(R.color.baby_blue)
                                        //var dividedDistance = distanceInKm


                                        var tintColor = when {
                                            distanceInKm <= filterDistance/3 -> resources.getColor(R.color.confirmation_green) //red
                                            distanceInKm <= filterDistance/2 -> resources.getColor(R.color.mediumOrange)
                                            //distanceInKm <= filterDistance -> resources.getColor(R.color.farRed)
                                            else -> {resources.getColor(R.color.farRed)}
                                        }

                                        val paint = Paint()
                                        val colorFilter: ColorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
                                        paint.colorFilter = colorFilter

                                        val tintedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                                        val canvas = Canvas(tintedBitmap)
                                        canvas.drawBitmap(bitmap, 0f, 0f, paint)


                                        // Set options for the resulting symbol layer.
                                        val pointAnnotationOptions: PointAnnotationOptions =
                                            PointAnnotationOptions()
                                                // Define a geographic coordinate from the hotspot's lat and lng.
                                                .withPoint(
                                                    Point.fromLngLat(
                                                        hotspot.lng!!,
                                                        hotspot.lat!!
                                                    )
                                                )
                                                // Specify the bitmap you assigned to the point annotation
                                                // The bitmap will be added to map style automatically.
                                                .withIconImage(tintedBitmap/*bitmap*/)
                                        // Add the resulting pointAnnotation to the map.
                                        pointAnnotationManager?.create(pointAnnotationOptions)
                                    }
                                }
                            }
                        }
                        //Toast.makeText(requireContext(), counterMap.toString(), Toast.LENGTH_SHORT).show()
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






    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun addSightingAnnotationsToMap() {
        //Toast.makeText(requireContext(), checkPermissions().toString(), Toast.LENGTH_SHORT).show()
        if (checkPermissions())
        {
            // Create an instance of the Annotation API and get the PointAnnotationManager.
            bitmapFromDrawableRes(
                requireContext(),
                R.drawable.imgbird
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
                        for (sighting in GlobalClass.userObservations) {
                            if (sighting.userID == GlobalClass.currentUser.userID) {
                                // Calculate the distance between the user's location and the hotspot.
                                val pointAnnotationOptions: PointAnnotationOptions =
                                    PointAnnotationOptions()
                                        // Define a geographic coordinate from the hotspot's lat and lng.
                                        .withPoint(
                                            Point.fromLngLat(
                                                sighting.long!!,
                                                sighting.lat!!
                                            )
                                        )
                                        // Specify the bitmap you assigned to the point annotation
                                        // The bitmap will be added to map style automatically.
                                        .withIconImage(bitmap)
                                // Add the resulting pointAnnotation to the map.
                                pointAnnotationManager?.create(pointAnnotationOptions)
                            }
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


    private fun kilometersToMiles(kilometers: Double): Double {
        // 1 kilometer = 0.62137119 miles
        return kilometers * 0.62137119
    }

    private fun milesToKilometers(miles: Double): Double{
        return miles / 0.62137119
    }



    // Function to calculate distance between two points in km using Haversine formula
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        var measurement = earthRadiusKm * c

//        if (GlobalClass.currentUser.isMetric == false)
//        {
//            measurement = kilometersToMiles(measurement)
//        }

        return measurement
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