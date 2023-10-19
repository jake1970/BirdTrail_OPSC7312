package com.example.birdtrail_opsc7312

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ImageHandler {

    //method to search for image on google images
    suspend fun searchImage(searchTerm: String): String? = withContext(Dispatchers.IO) {
        val encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8")
        val searchEngineId = BuildConfig.IMAGE_ENGINE_ID
        val APIKey = BuildConfig.IMAGE_API_TOKEN
        val url = "https://www.googleapis.com/customsearch/v1?key=$APIKey&cx=$searchEngineId&q=$encodedSearchTerm&searchType=image"

        val result = URL(url).readText()
        val json = JSONObject(result)

        val items = json.getJSONArray("items")
        if (items.length() > 0) {
            val firstResult = items.getJSONObject(0)
            return@withContext firstResult.getString("link")
        }
        return@withContext null
    }

    //---------------------------------------------------------------------------------------------

    //method to get a bitmap from a specified URL
    suspend fun getBitmapFromURL(src: String): Bitmap? = withContext(Dispatchers.IO) {
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        return@withContext BitmapFactory.decodeStream(input)
    }

    //---------------------------------------------------------------------------------------------

    //method to get bitmap image from api
    suspend fun GetImage(searchTerm: String): Bitmap? {
        var URL = searchImage(searchTerm)
        var image = URL?.let { getBitmapFromURL(it) }
        return image
    }
}