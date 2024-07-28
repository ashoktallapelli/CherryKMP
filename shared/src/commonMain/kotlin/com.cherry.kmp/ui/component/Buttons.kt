package com.cherry.kmp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RoundedButton(
    text: String,
    image: ImageVector? = null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        image?.let {
            Icon(imageVector = it, contentDescription = null)
            Spacer_8dp()
        }
        Text(text)
    }
}

