package com.example.tareafinalcompose.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tareafinalcompose.viewmodel.PokemonViewModel

@Composable
fun PokemonListScreen(navController: NavController) {
    val pokemonViewModel: PokemonViewModel = viewModel()

    // Fetch pokemons when screen is first composed
    pokemonViewModel.fetchPokemons()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("List of Pokemons")

        LazyColumn {
            items(pokemonViewModel.pokemons.value ?: emptyList()) { pokemon ->
                Text(text = pokemon.name)
            }
        }

        Button(onClick = { /* Go back to home screen */ }) {
            Text("Back to Home")
        }
    }
}