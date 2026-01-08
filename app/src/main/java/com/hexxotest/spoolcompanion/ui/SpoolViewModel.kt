package com.hexxotest.spoolcompanion.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hexxotest.spoolcompanion.models.SpoolListEntry
import com.hexxotest.spoolcompanion.network.SpoolApi
import kotlinx.coroutines.launch
import java.io.IOException

class SpoolViewModel(spoolmanUrl: String) : ViewModel() {

    private val url = spoolmanUrl

    sealed interface UiState {
        data class Success(val spools: List<SpoolListEntry>) : UiState
        data object Error : UiState
        data object Loading : UiState
    }

    var currentUiState: UiState by mutableStateOf(UiState.Loading)
        private set

    init {
        getSpools()
    }

    private fun getSpools() {
        viewModelScope.launch {
            currentUiState = try {
                Log.d("SpoolViewModel", "Fetching spools from URL: $url")
                val spoolApi = SpoolApi(baseUrl = url)
                val spools = spoolApi.retrofitService.getSpoolList()
                Log.d("SpoolViewModel", "Successfully fetched ${spools.size} spools")
                val entries = spools.map { spool ->
                    val color = try {
                        Color(android.graphics.Color.parseColor("#${spool.filament.color_hex}"))
                    } catch (e: Exception) {
                        Log.w("SpoolViewModel", "Invalid color for spool ${spool.id}: '${spool.filament.color_hex}'")
                        Color.Gray
                    }
                    SpoolListEntry(
                        id = spool.id,
                        filamentId = spool.filament.id,
                        vendorName = spool.filament.vendor.name,
                        name = spool.filament.name,
                        color = color,
                        material = spool.filament.material,
                        weight = convertWeightDoubleToString(spool.filament.weight),
                        diameter = spool.filament.diameter,
                        comment = spool.comment
                    )
                }
                UiState.Success(entries)
            } catch (e: Exception) {
                Log.e("SpoolViewModel", "Error loading spools", e)
                Log.e("SpoolViewModel", "Error type: ${e.javaClass.simpleName}")
                Log.e("SpoolViewModel", "Error message: ${e.message}")
                e.printStackTrace()
                UiState.Error
            }
        }
    }

    private fun convertWeightDoubleToString(weight: Double): String {
        // Weight var
        var weightInfo: Double = weight
        // Define unit 'g' default, 'kg' if > 1000 g
        var unit: String = "g"
        // Check if greater than a kilo
        if (weightInfo >= 1000) {
            weightInfo /= 1000.0
            unit = "kg"
        }
        // Return formatted string
        val weightStr: String = weightInfo.toString()
        return if (weightStr.contains(".0")) {
            "${weightStr.substring(0, weightStr.length - 2)} $unit"
        } else {
            "$weightStr $unit"
        }
    }
}
