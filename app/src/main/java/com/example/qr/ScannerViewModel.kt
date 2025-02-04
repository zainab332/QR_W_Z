package com.example.qr

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("ScanPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val scanResultsLiveData = MutableLiveData<List<String>>()

    init {
        // Charger les résultats stockés depuis SharedPreferences au démarrage
        scanResultsLiveData.value = loadScanResults()
    }

    val scanResults: LiveData<List<String>> get() = scanResultsLiveData

    // Ajouter un résultat scanné
    fun addScanResult(result: String) {
        val currentResults = scanResultsLiveData.value?.toMutableList() ?: mutableListOf()
        currentResults.add(0, result) // Ajouter au début de la liste
        saveScanResults(currentResults)
        scanResultsLiveData.value = currentResults // Notifier les observateurs
    }

    // Charger les résultats depuis SharedPreferences
    private fun loadScanResults(): List<String> {
        val json = sharedPreferences.getString("scan_results", null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Sauvegarder les résultats dans SharedPreferences
    private fun saveScanResults(results: List<String>) {
        val json = gson.toJson(results)
        sharedPreferences.edit().putString("scan_results", json).apply()
    }
//Supprimer
    fun deleteScanResult(content: String) {
        val currentResults = scanResultsLiveData.value?.toMutableList() ?: mutableListOf()
        currentResults.remove(content) // Supprimer l'élément correspondant
        saveScanResults(currentResults) // Sauvegarder dans SharedPreferences
        scanResultsLiveData.value = currentResults // Notifier les observateurs
    }


    // Supprimer tout l'historique
    fun clearHistory() {
        sharedPreferences.edit().remove("scan_results").apply()
        scanResultsLiveData.value = emptyList()
    }
}

