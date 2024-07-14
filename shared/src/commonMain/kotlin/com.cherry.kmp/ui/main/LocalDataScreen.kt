package com.cherry.kmp.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cherry.kmp.data.entity.DataModelEntity
import org.koin.compose.koinInject
import kotlin.random.Random

@Composable
internal fun LocalDataScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllValues()
    }

    val items = viewModel.allItems
    var tappedItem by remember { mutableStateOf<DataModelEntity?>(null) }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = {
                val item = DataModelEntity(name = "item ${Random.nextInt()}")
                viewModel.insert(item)
            }) {
                Text("Add item")
            }
            Spacer(Modifier.width(16.dp))

            Button(onClick = {
                tappedItem = null
                viewModel.deleteAll()
            }) {
                Text("Delete all")
            }
        }

        tappedItem?.run {
            Text("Tapped $name ")
        }
        Spacer(Modifier.height(32.dp))

        Text("Current Items (tap to show content):")
        LazyColumn {
            items(items.value.size) { item ->
                Text(
                    items.value[item].name,
                    modifier = Modifier.clickable {
                        viewModel.getById(items.value[item].id)
                        tappedItem = viewModel.item.value
                    },
                    color = Color.White
                )
            }
        }
    }
}