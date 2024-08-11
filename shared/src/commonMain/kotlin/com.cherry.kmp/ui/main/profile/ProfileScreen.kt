package com.cherry.kmp.ui.main.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.email
import cherrykmp.shared.generated.resources.name
import cherrykmp.shared.generated.resources.profile
import com.cherry.kmp.ui.component.CircleImage
import com.cherry.kmp.ui.component.MyToolbar
import com.cherry.kmp.ui.component.Spacer_16dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    navigateToEditProfile: () -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }
    val state by viewModel.state.collectAsState()
    val sheetState =
        androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    Scaffold(topBar = {
        MyToolbar(
            title = stringResource(Res.string.profile),
            showNavigation = false,
            showEditIcon = true,
            onNavigationClick = {},
            onEditClick = { navigateToEditProfile() })
    },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Profile Image
                CircleImage(image = state.image, modifier = Modifier.size(120.dp)) {
                    scope.launch { sheetState.show() }
                }
                Spacer_16dp()
                // Name Field
                OutlinedTextField(
                    enabled = false,
                    value = state.name,
                    onValueChange = {
                        viewModel.setName(it)
                    },
                    label = { Text(stringResource(Res.string.name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer_16dp()
                // Email Field
                OutlinedTextField(
                    enabled = false,
                    value = state.email,
                    onValueChange = { viewModel.setEmail(it) },
                    label = { Text(stringResource(Res.string.email)) },
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
            }
        }
    )
}