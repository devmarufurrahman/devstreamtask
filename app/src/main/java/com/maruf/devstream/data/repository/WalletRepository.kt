package com.maruf.devstream.data.repository

import android.content.Context
import com.maruf.devstream.R
import org.json.JSONObject
import java.io.InputStream

class WalletRepository (private val context: Context) {

    fun loadChartData(): Pair<String, List<Pair<String, Float>>>? {
        return try {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.wallet_chart)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val currency = jsonObject.getString("currency")

            val spendObject = jsonObject.getJSONObject("spend")
            val dataArray = spendObject.getJSONArray("data")

            if (dataArray.length() > 0) {
                val scheduleObject = dataArray.getJSONObject(0)
                val dataPoints = mutableListOf<Pair<String, Float>>()
                for (key in scheduleObject.keys()) {
                    val value = scheduleObject.getDouble(key).toFloat()
                    dataPoints.add(Pair(key, value))
                }
                return Pair(currency, dataPoints)
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}