package cl.duoc.santiagosacro.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.santiagosacro.view.BienvenidaScreen
import cl.duoc.santiagosacro.view.EscanearScreen
import cl.duoc.santiagosacro.view.FichaScreen
import cl.duoc.santiagosacro.view.InicioScreen
import cl.duoc.santiagosacro.view.LoginScreen
import cl.duoc.santiagosacro.view.PerfilScreen
import cl.duoc.santiagosacro.view.VisitasScreen

// Una pestaña de la barra inferior
private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

// Las 4 pantallas principales (se ven después del login)
private val pestanas = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    Pestana(Rutas.ESCANEAR, "Escanear", Icons.Filled.QrCodeScanner),
    Pestana(Rutas.VISITAS, "Mis visitas", Icons.Filled.History),
    Pestana(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavigation() {
    // El navController guarda el historial de pantallas y sabe navegar entre ellas
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    // La barra inferior solo aparece en las 4 pantallas principales
    val mostrarBarra = pestanas.any { it.ruta == rutaActual }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(
                    rutaActual = rutaActual,
                    onSeleccionar = { ruta -> navegarAPestana(navController, ruta) }
                )
            }
        }
    ) { innerPadding ->
        // NavHost: muestra la pantalla que corresponde a la ruta actual
        NavHost(
            navController = navController,
            startDestination = Rutas.BIENVENIDA,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable(Rutas.BIENVENIDA) {
                BienvenidaScreen(onIniciarSesion = { navController.navigate(Rutas.LOGIN) })
            }
            composable(Rutas.LOGIN) {
                LoginScreen(
                    onIngresoExitoso = {
                        navController.navigate(Rutas.INICIO) {
                            // Saca Bienvenida y Login del historial: al apretar "atrás" no vuelve al login
                            popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.INICIO) {
                InicioScreen(onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) })
            }
            composable(
                route = Rutas.FICHA,
                arguments = listOf(navArgument("recintoId") { type = NavType.LongType })
            ) { entrada ->
                val recintoId = entrada.arguments?.getLong("recintoId") ?: 0L
                FichaScreen(recintoId = recintoId, onVolver = { navController.popBackStack() })
            }
            composable(Rutas.ESCANEAR) {
                EscanearScreen(onRecintoEscaneado = { id -> navController.navigate(Rutas.ficha(id)) })
            }
            composable(Rutas.VISITAS) {
                VisitasScreen(onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) })
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    onCerrarSesion = {
                        navController.navigate(Rutas.BIENVENIDA) {
                            // Borra todo el historial: después de salir no se puede volver atrás
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BarraInferior(rutaActual: String?, onSeleccionar: (String) -> Unit) {
    NavigationBar {
        pestanas.forEach { pestana ->
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = { onSeleccionar(pestana.ruta) },
                icon = { Icon(pestana.icono, contentDescription = pestana.titulo) },
                label = { Text(pestana.titulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// Cambia de pestaña sin apilar pantallas repetidas en el historial
private fun navegarAPestana(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(Rutas.INICIO) { saveState = true }  // Inicio queda siempre como base
        launchSingleTop = true                       // no duplica la pantalla si ya estás en ella
        restoreState = true                          // la pestaña vuelve como la dejaste
    }
}
