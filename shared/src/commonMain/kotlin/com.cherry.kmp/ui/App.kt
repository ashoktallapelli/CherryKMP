package com.cherry.kmp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cherry.kmp.data.AppDatabase
import com.cherry.kmp.data.entity.DataModelEntity
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.ui.theme.AppTheme
import com.cherry.kmp.ui.main.MainNav
import com.cherry.kmp.ui.main.MainViewModel
import com.cherry.kmp.ui.navigation.AppNavigation
import com.cherry.kmp.ui.splash.SplashNav
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.random.Random

@Composable
internal fun App(appDatabase: AppDatabase = koinInject()) {

    val scope = rememberCoroutineScope()

    val dao = remember { appDatabase.dataDao() }
    val items = dao.getAllAsFlow().collectAsState(initial = emptyList())
    var tappedItem by remember { mutableStateOf<DataModelEntity?>(null) }
    AppTheme {
        val navigator = rememberNavController()

        LoadItemViews()

//        Box(modifier = Modifier.fillMaxSize()) {
//            NavHost(
//                navController = navigator,
//                startDestination = AppNavigation.Splash.route,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                composable(route = AppNavigation.Splash.route) {
//                    SplashNav(navigateToMain = {
//                        navigator.popBackStack()
//                        navigator.navigate(AppNavigation.Main.route)
//                    })
//                }
//                composable(route = AppNavigation.Main.route) {
//                    MainNav {
//                        navigator.popBackStack()
//                        navigator.navigate(AppNavigation.Splash.route)
//                    }
//                }
//            }
//        }

//        var showContent by remember { mutableStateOf(false) }
//        Column(
//            Modifier
//                .fillMaxSize()
//                .background(Color.Black),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row(modifier = Modifier.padding(16.dp)) {
//                Button(onClick = {
//                    val item = DataModelEntity(name = "item ${Random.nextInt()}")
//                    scope.launch {
//                        dao.insert(item)
//                    }
//                }) {
//                    Text("Add item")
//                }
//                Spacer(Modifier.width(16.dp))
//
//                Button(onClick = {
//                    scope.launch {
//                        tappedItem = null
//                        dao.deleteAll()
//                    }
//                }) {
//                    Text("Delete all")
//                }
//            }
//
//            tappedItem?.run {
//                Text("Tapped $name ")
//            }
//            Spacer(Modifier.height(32.dp))
//
//            Text("Current Items (tap to show content):")
//            LazyColumn {
//                items(items.value.size) { item ->
//                    Text(
//                        items.value[item].name,
//                        modifier = Modifier.clickable {
//                            scope.launch {
//                                tappedItem = dao.getById(items.value[item].id)
//                            }
//                        },
//                        color = Color.White
//                    )
//                }
//            }
//        }
    }
}

@Composable
fun LoadItemViews(viewModel: MainViewModel = koinInject()) {
    val postState by viewModel.quotesResult
    LaunchedEffect(key1 = Unit) {
        viewModel.loadItems()
//        loadData(viewModel)
    }
    when (postState) {
        is UiState.Loading -> {
            LoadingScreen()
        }

        is UiState.Success<*> -> {
            val quoteItems = (postState as UiState.Success<List<Post>>).data
            ItemList(itemQuotes = quoteItems)
        }

        is UiState.Error -> {
            val exception = (postState as UiState.Error).apiError
            ErrorScreen(exception)
        }

        UiState.Initial -> {
//            TODO()
        }
    }
}

@Composable
fun ItemRow(quote: Post) {
    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(all = 10.dp)) {
            Text(
                quote.title,
                fontSize = 25.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                quote.body.toString(), color = Color.Gray, modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun ItemList(itemQuotes: List<Post>) {
//    val context = LocalContext.current
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        itemQuotes.forEach { post ->
            ItemRow(post)
        }
        Button(
            onClick = {
//                context.startActivity(Intent(context, PostsActivity::class.java))
            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            )
        ) {
            Text(text = "Lorem")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(apiError: Throwable) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: ${apiError.message}", color = MaterialTheme.colorScheme.error)
    }
}
