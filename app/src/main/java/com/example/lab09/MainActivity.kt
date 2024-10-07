package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab09.ui.theme.Lab09Theme
import com.example.lab09.ui.theme.PostApiService
import com.example.lab09.ui.theme.PostModel
import com.example.lab09.ui.theme.ScreenPosts
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab09.ui.theme.PostViewModel

class MainActivity : ComponentActivity() {
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab09Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProgPrincipal9(postViewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}



@Composable
fun ProgPrincipal9(postViewModel: PostViewModel, padding: Modifier) {
    val urlBase = "https://jsonplaceholder.typicode.com/"
    val retrofit = Retrofit.Builder().baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val servicio = retrofit.create(PostApiService::class.java)
    val navController = rememberNavController()

    Scaffold(
        topBar =    { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        content =   { paddingValues -> Contenido(paddingValues, navController, servicio) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "JSONPlaceHolder Access",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.LightGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Posts") },
            label = { Text("Posts") },
            selected = navController.currentDestination?.route == "posts",
            onClick = { navController.navigate("posts") }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicio: PostApiService
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio" // Ruta de inicio
        ) {
            composable("inicio") { ScreenInicio() }

            composable("posts") { ScreenPosts(navController, servicio) }
            composable("postsVer/{id}", arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )) {
                val postViewModel: PostViewModel = viewModel()

                val postId = it.arguments!!.getInt("id")
                ScreenPost(postViewModel = postViewModel, postId = postId)
            }

        }
    }
}

@Composable
fun ScreenInicio() {
    Text("INICIO")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Composable
fun ScreenPosts(postViewModel: PostViewModel, navController: NavHostController) {
    val listaPosts by postViewModel.posts.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        postViewModel.fetchPosts()
    }

    LazyColumn {
        items(listaPosts) { item ->
            Row(modifier = Modifier.padding(8.dp)) {
                Text(text = item.id.toString(), Modifier.weight(0.05f), textAlign = TextAlign.End)
                Spacer(Modifier.padding(horizontal=1.dp))
                Text(text = item.title, Modifier.weight(0.7f))
                IconButton(
                    onClick = {
                        navController.navigate("postsVer/${item.id}")
                    },
                    Modifier.weight(0.1f)
                ) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Ver")
                }
            }
        }
    }
}

@Composable
fun ScreenPost(postViewModel: PostViewModel, postId: Int) {
    val post by postViewModel.selectedPost.observeAsState()

    LaunchedEffect(Unit) {
        postViewModel.fetchPostById(postId)
    }

    post?.let {
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = it.id.toString(),
                onValueChange = {},
                label = { Text("id") },
                readOnly = true
            )
            OutlinedTextField(
                value = it.userId.toString(),
                onValueChange = {},
                label = { Text("userId") },
                readOnly = true
            )
            OutlinedTextField(
                value = it.title,
                onValueChange = {},
                label = { Text("title") },
                readOnly = true
            )
            OutlinedTextField(
                value = it.body,
                onValueChange = {},
                label = { Text("body") },
                readOnly = true
            )
        }
    } ?: run {
        Text("Cargando post...")
    }
}
