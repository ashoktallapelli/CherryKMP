package com.cherry.kmp.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.compose_multiplatform
import com.cherry.kmp.ui.component.Spacer_64dp
import com.cherry.kmp.ui.component.Spacer_8dp
import com.cherry.kmp.ui.main.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    navController: NavHostController
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Image
        Image(
            painter = painterResource(resource = Res.drawable.compose_multiplatform),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field
        OutlinedTextField(
            value = state.name,
            onValueChange = {
                viewModel.setName(it)
            },
            label = { Text("Name") },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.setEmail(it) },
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Spacer_64dp()

        // Save Button
        Button(
            onClick = { viewModel.saveUserProfile() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null)
            Spacer_8dp()
            Text("Save")
        }
    }
}