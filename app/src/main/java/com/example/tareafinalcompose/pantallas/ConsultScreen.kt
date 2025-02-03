
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tareafinalcompose.viewmodel.PokemonViewModel

@Composable
fun ConsultScreen(navController: NavHostController) {
    val pokemonViewModel: PokemonViewModel = viewModel()

    // Usar observeAsState para observar los cambios en el LiveData del ViewModel
    val pokemons = pokemonViewModel.pokemons.observeAsState(initial = emptyList()).value
    val isLoading = pokemonViewModel.isLoading.observeAsState(initial = false).value
    val isConsulted = pokemonViewModel.isConsulted.observeAsState(initial = false).value

    // Llamar a la función que hace la solicitud para obtener los Pokémon, solo cuando la pantalla se carga
    LaunchedEffect(Unit) {
        pokemonViewModel.fetchPokemons()
    }

    Box(
        modifier = Modifier
            .fillMaxSize() // Hace que el Box ocupe todo el tamaño disponible
            .padding(16.dp), // Añade un padding de 16dp alrededor del contenido
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        if (isLoading) {
            BasicText(
                text = "Cargando...", // Texto de carga
                style = TextStyle(
                    fontSize = 20.sp, // Tamaño de la fuente
                    color = Color.Gray // Color gris para el texto
                )
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente el contenido
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre los elementos de la columna
            ) {
                BasicText(
                    text = "Lista de Pokémon", // Texto del título
                    style = TextStyle(
                        fontSize = 26.sp, // Tamaño de la fuente
                        color = Color(0xFFFFD700) // Color dorado para el texto
                    )
                )

                if (isConsulted) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp) // Padding alrededor del Box
                            .fillMaxWidth() // Hace que el Box ocupe todo el ancho
                    ) {
                        Column {
                            pokemons.forEach { pokemon ->
                                BasicText(
                                    text = pokemon.name, // Nombre del Pokémon
                                    style = TextStyle(
                                        fontSize = 20.sp, // Tamaño de la fuente
                                        color = Color.Black // Color negro para el texto
                                    )
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los botones
                    modifier = Modifier.padding(top = 8.dp) // Padding en la parte superior
                ) {
                    // Botón de "Atrás" para navegar a la pantalla anterior
                    BasicText(
                        text = "Atrás", // Texto del botón
                        modifier = Modifier
                            .padding(8.dp) // Padding alrededor del texto
                            .align(Alignment.CenterVertically) // Centrado verticalmente
                            .clickable {
                                // Usamos el NavController para ir a la pantalla anterior
                                navController.popBackStack()
                            },
                        style = TextStyle(
                            fontSize = 16.sp, // Tamaño de la fuente
                            color = Color.Blue // Color azul para el texto
                        )
                    )
                }
            }
        }
    }
}

