package com.cherry.kmp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralAlertDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer_8dp()
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer_32dp()
                Row(modifier = Modifier.fillMaxWidth()) {
                    RoundedButton(
                        text = positiveButtonText,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        onPositiveClick()
                        onDismissRequest()
                    }
                    Spacer_8dp()
                    RoundedButton(
                        text = negativeButtonText,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        onNegativeClick()
                        onDismissRequest()
                    }
                }
            }
        }
    }
}
