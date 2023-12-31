package com.example.birdtrail_opsc7312

import android.util.Log
import com.example.example.HotspotJson2KtKotlin
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class eBirdAPIHandler
{
    //api call to get region observations
    suspend fun getRecentObservations(regionCode: String): String = withContext(Dispatchers.IO)
    {
        val url = URL("https://api.ebird.org/v2/data/obs/${regionCode}/recent")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-eBirdApiToken", BuildConfig.EBIRD_TOKEN)
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()

            // Parse the JSON response into eBirdJson2KtKotlin objects
            val listType = object : TypeToken<List<eBirdJson2KtKotlin>>() {}.type
            val observations: List<eBirdJson2KtKotlin> = Gson().fromJson(response, listType)

            // Add the observations to the global list
            GlobalClass.hotspots = arrayListOf<eBirdJson2KtKotlin>()
            GlobalClass.hotspots.addAll(observations)

            //Log.d("E-BIRD OUTPUT", response)
            return@withContext response
        }
        else
        {
            return@withContext "Error: $responseCode"
        }
    }

    //---------------------------------------------------------------------------------------------

    //api call to get hotspots nearby to the user
    suspend fun getNearbyHotspots(long: Double, lat: Double): String = withContext(Dispatchers.IO)
    {
        val url = URL("https://api.ebird.org/v2/ref/hotspot/geo?lat=${lat}&lng=${long}&fmt=json&back=21&dist=60")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-eBirdApiToken", BuildConfig.EBIRD_TOKEN)
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()

            // Parse the JSON response into eBirdJson2KtKotlin objects
            val listType = object : TypeToken<List<HotspotJson2KtKotlin>>() {}.type
            val hotspots: List<HotspotJson2KtKotlin> = Gson().fromJson(response, listType)

            // Add the observations to the global list
            GlobalClass.nearbyHotspots = arrayListOf<HotspotJson2KtKotlin>()
            GlobalClass.nearbyHotspots.addAll(hotspots)

            //Log.d("E-BIRD OUTPUT", response)
            return@withContext response
        }
        else
        {
            return@withContext "Error: $responseCode"
        }
    }

    //---------------------------------------------------------------------------------------------

    //api call to get bird observations at a specified hotspot
    suspend fun getHotspotBirds(regionCode: String, locationCode: String): String = withContext(Dispatchers.IO)
    {
        val url = URL("https://api.ebird.org/v2/data/obs/${regionCode}/recent?hotspot=true&r=$locationCode&back=21")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-eBirdApiToken", BuildConfig.EBIRD_TOKEN)
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()

            // Parse the JSON response into eBirdJson2KtKotlin objects
            val listType = object : TypeToken<List<eBirdJson2KtKotlin>>() {}.type
            val items: List<eBirdJson2KtKotlin> = Gson().fromJson(response, listType)

            // Add the observations to the global list
            GlobalClass.currentHotspotBirds = arrayListOf<eBirdJson2KtKotlin>()
            GlobalClass.currentHotspotBirds.addAll(items)

            Log.d("E-BIRD OUTPUT", response)
            return@withContext response
        }
        else
        {
            return@withContext "Error: $responseCode"
        }
    }
}