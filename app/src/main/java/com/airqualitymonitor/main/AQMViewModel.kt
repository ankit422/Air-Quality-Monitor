package com.airqualitymonitor.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject


@HiltViewModel
class AQMViewModel @Inject constructor() : ViewModel() {
    private lateinit var webSocketClient: WebSocketClient
    val map = HashMap<Double, Pair<String, String>>().apply {
        put(50.0, Pair("#008000", "Good"))
        put(100.0, Pair("#7CFC00", "Satisfactory"))
        put(200.0, Pair("#FFFF00", "Moderate"))
        put(300.0, Pair("#FFA500", "Poor"))
        put(400.0, Pair("#FF0000", "Very Poor"))
        put(500.0, Pair("#B22222", "Severe"))
    }

    private val cities = MutableLiveData<List<Cities>>()
    private val city = MutableLiveData<Cities>()
    val citiesToShow: LiveData<List<Cities>> = cities
    val cityData: MutableLiveData<MutableList<DataEntry>> = MutableLiveData()

    val cityDetails: LiveData<Cities> = city

    fun getData() {
        createWebSocketClient(URI(WEB_SOCKET_URL))
    }

    private fun createWebSocketClient(coinbaseUri: URI?) {
        webSocketClient = object : WebSocketClient(coinbaseUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
            }

            override fun onMessage(message: String?) {
                if (cities.value == null) {
                    viewModelScope.launch {
                        val list =
                            Gson().fromJson(message, Array<Cities>::class.java).toMutableList()
                        for (item in list) {
                            item.aqi = String.format("%.2f", item.aqi).toDouble()
                            item.status = getPair(item)
                        }
                        cities.postValue(list)
                    }
                } else if (city.value != null && (cityData.value?.size!! < 10)) {
                    val list =
                        Gson().fromJson(message, Array<Cities>::class.java).toMutableList()
                    var index = cityData.value?.size ?: 0
                    for (value in list) {
                        if (value.city == city.value?.city) {
                            val tempList = cityData.value ?: ArrayList()
                            if (tempList.size > 10)
                                tempList.removeAt(0)

                            tempList.add(ValueDataEntry(index, value.aqi.toInt()))
                            cityData.postValue(tempList)
                            break
                        }
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
            }

            override fun onError(ex: Exception?) {
            }

        }
        webSocketClient.connect()
    }

     fun getPair(item: Cities): Pair<String, String> {
        return when {
            item.aqi <= 50 -> map[50.0]!!
            item.aqi <= 100 -> map[100.0]!!
            item.aqi <= 200 -> map[200.0]!!
            item.aqi <= 300 -> map[300.0]!!
            item.aqi <= 400 -> map[400.0]!!
            else -> map[500.0]!!
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.close()
    }

    fun getCity(index: Int?) {
        val item = cities.value?.get(index ?: 0)
        cityData.value =
            mutableListOf(ValueDataEntry(0, item?.aqi?.toInt() ?: 0))
        city.value = item ?: null
    }


    companion object {
        const val WEB_SOCKET_URL = "ws://city-ws.herokuapp.com/"
        const val TAG = "Coinbase"
    }
}
