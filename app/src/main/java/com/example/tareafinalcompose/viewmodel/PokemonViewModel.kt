package com.example.tareafinalcompose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tareafinalcompose.data.Pokemon
import com.example.tareafinalcompose.data.RetrofitClient
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {

    private val _pokemons = MutableLiveData<List<Pokemon>>()
    val pokemons: LiveData<List<Pokemon>> get() = _pokemons

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isConsulted = MutableLiveData<Boolean>()
    val isConsulted: LiveData<Boolean> get() = _isConsulted

    fun fetchPokemons() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.pokeApiService.getPokemons()
                _pokemons.value = response.results
                _isConsulted.value = true
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
