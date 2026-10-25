package cl.duoc.aurasacrapp.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.aurasacrapp.view.AgendaScreen
import cl.duoc.aurasacrapp.view.BienvenidaScreen
import cl.duoc.aurasacrapp.view.CrearRutaScreen
import cl.duoc.aurasacrapp.view.EditarFichaScreen
import cl.duoc.aurasacrapp.view.EscanearScreen
import cl.duoc.aurasacrapp.view.EventoScreen
import cl.duoc.aurasacrapp.view.FichaScreen
import cl.duoc.aurasacrapp.view.GuiasScreen
import cl.duoc.aurasacrapp.view.InicioScreen
import cl.duoc.aurasacrapp.view.LoginScreen
import cl.duoc.aurasacrapp.view.MapaScreen
import cl.duoc.aurasacrapp.view.MiRecintoScreen
import cl.duoc.aurasacrapp.view.PerfilScreen
import cl.duoc.aurasacrapp.view.PlanificarVisitaScreen
import cl.duoc.aurasacrapp.view.PublicarEventoScreen
import cl.duoc.aurasacrapp.view.RegistroScreen
import cl.duoc.aurasacrapp.view.RutaDetalleScreen
import cl.duoc.aurasacrapp.view.RutasScreen
import cl.duoc.aurasacrapp.view.VisitasScreen

// Una pestaña de la barra inferior
private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

// Las 5 pantallas principales (se ven después del login)
private val pestanas = listOf(
    Pestana(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    Pestana(Rutas.AGENDA, "Agenda", Icons.Filled.Event),
    Pestana(Rutas.ESCANEAR, "Escanear", Icons.Filled.QrCodeScanner),
    Pestana(Rutas.VISITAS, "Visitas", Icons.Filled.History),
    Pestana(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavigation() {
    // El navController guarda el historial de pantallas y sabe navegar entre ellas
    val navController = rememberNavController()
    val context = LocalContext.current
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    // La barra inferior solo aparece en las pantallas principales
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
                BienvenidaScreen(
                    onIniciarSesion = { navController.navigate(Rutas.LOGIN) },
                    onCrearCuenta = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.LOGIN) {
                LoginScreen(
                    onIngresoExitoso = {
                        navController.navigate(Rutas.INICIO) {
                            // Saca Bienvenida y Login del historial: al apretar "atrás" no vuelve al login
                            popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() },
                    onCrearCuenta = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    onCuentaCreada = {
                        avisar(context, "Cuenta creada")
                        irAlLogin(navController)
                    },
                    onIrALogin = { irAlLogin(navController) },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.INICIO) {
                InicioScreen(
                    onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) },
                    onAdministrarRecinto = { navController.navigate(Rutas.MI_RECINTO) },
                    onVerRutas = { navController.navigate(Rutas.RUTAS) },
                    onVerMapa = { navController.navigate(Rutas.MAPA) }
                )
            }
            composable(Rutas.MAPA) {
                MapaScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) }
                )
            }
            composable(
                route = Rutas.FICHA,
                arguments = listOf(navArgument("recintoId") { type = NavType.LongType })
            ) {
                // El recintoId lo lee FichaViewModel desde SavedStateHandle
                FichaScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirEvento = { id -> navController.navigate(Rutas.evento(id)) },
                    onPlanificarVisita = { id -> navController.navigate(Rutas.planificar(id)) }
                )
            }
            composable(
                route = Rutas.PLANIFICAR,
                arguments = listOf(navArgument("recintoId") { type = NavType.LongType })
            ) {
                PlanificarVisitaScreen(
                    onVolver = { navController.popBackStack() },
                    onGuardar = { avisarYVolver(navController, context, "Visita agendada") }
                )
            }
            composable(Rutas.RUTAS) {
                RutasScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirRuta = { id -> navController.navigate(Rutas.ruta(id)) },
                    onCrearRuta = { navController.navigate(Rutas.CREAR_RUTA) }
                )
            }
            composable(Rutas.CREAR_RUTA) {
                CrearRutaScreen(
                    onVolver = { navController.popBackStack() },
                    onGuardar = { avisarYVolver(navController, context, "Ruta guardada") }
                )
            }
            composable(
                route = Rutas.RUTA,
                arguments = listOf(navArgument("rutaId") { type = NavType.LongType })
            ) {
                RutaDetalleScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) }
                )
            }
            composable(Rutas.AGENDA) {
                AgendaScreen(onAbrirEvento = { id -> navController.navigate(Rutas.evento(id)) })
            }
            composable(
                route = Rutas.EVENTO,
                arguments = listOf(navArgument("eventoId") { type = NavType.LongType })
            ) {
                // El eventoId lo lee EventoViewModel desde SavedStateHandle
                EventoScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) }
                )
            }
            composable(Rutas.ESCANEAR) {
                EscanearScreen(onRecintoEscaneado = { id -> navController.navigate(Rutas.ficha(id)) })
            }
            composable(Rutas.VISITAS) {
                VisitasScreen(onAbrirRecinto = { id -> navController.navigate(Rutas.ficha(id)) })
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    onMiRecinto = { navController.navigate(Rutas.MI_RECINTO) },
                    onCerrarSesion = {
                        navController.navigate(Rutas.BIENVENIDA) {
                            // Borra todo el historial: después de salir no se puede volver atrás
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(Rutas.MI_RECINTO) {
                MiRecintoScreen(
                    onVolver = { navController.popBackStack() },
                    onVerFicha = { id -> navController.navigate(Rutas.ficha(id)) },
                    onPublicarEvento = { navController.navigate(Rutas.PUBLICAR_EVENTO) },
                    onEditarFicha = { navController.navigate(Rutas.EDITAR_FICHA) },
                    onGuias = { navController.navigate(Rutas.GUIAS) }
                )
            }
            composable(Rutas.EDITAR_FICHA) {
                EditarFichaScreen(
                    onVolver = { navController.popBackStack() },
                    onGuardar = { avisarYVolver(navController, context, "Cambios guardados") }
                )
            }
            composable(Rutas.GUIAS) {
                GuiasScreen(onVolver = { navController.popBackStack() })
            }
            composable(Rutas.PUBLICAR_EVENTO) {
                PublicarEventoScreen(
                    onVolver = { navController.popBackStack() },
                    onGuardar = { avisarYVolver(navController, context, "Evento publicado") }
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

// Después de crear la cuenta: va al login sin dejar el registro en el historial
private fun irAlLogin(navController: NavHostController) {
    navController.navigate(Rutas.LOGIN) {
        popUpTo(Rutas.REGISTRO) { inclusive = true }
        launchSingleTop = true
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

// Los formularios son solo visuales: al guardar se muestra un aviso corto y se vuelve atrás
private fun avisarYVolver(navController: NavHostController, context: Context, mensaje: String) {
    avisar(context, mensaje)
    navController.popBackStack()
}

private fun avisar(context: Context, mensaje: String) {
    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
}
