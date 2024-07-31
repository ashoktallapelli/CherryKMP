package com.cherry.kmp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.camera
import cherrykmp.shared.generated.resources.choose_an_option
import cherrykmp.shared.generated.resources.gallery
import org.jetbrains.compose.resources.stringResource


@Composable
fun ImageOptionSheet(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.choose_an_option),
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
        Spacer_8dp()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ElevatedButton(
                onClick = {
                    onGalleryClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer_8dp()
                Text(stringResource(Res.string.gallery))
            }
            Spacer_16dp()
            ElevatedButton(
                onClick = {
                    onCameraClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Camera, contentDescription = null)
                Spacer_8dp()
                Text(stringResource(Res.string.camera))
            }
        }
    }
}