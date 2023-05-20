package com.bingxuan.taskmanagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bingxuan.taskmanagement.R
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    open: Boolean, onAccept: (date: Date) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    if (open) DatePickerDialog(confirmButton = {
        TextButton(
            onClick = { onAccept(Date(datePickerState.selectedDateMillis ?: 0)) },
            enabled = datePickerState.selectedDateMillis != null
        ) {
            Text(text = "确定")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = "取消")
        }
    }, onDismissRequest = onDismiss
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    open: Boolean, onAccept: (hour: Int, minute: Int) -> Unit, onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()
    if (open) DatePickerDialog(
        confirmButton = {
            TextButton(onClick = { onAccept(timePickerState.hour, timePickerState.minute) }) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        },

        onDismissRequest = onDismiss,
    ) {
        TimePicker(
            state = timePickerState, modifier = Modifier
                .padding(8.dp, 12.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, navController: NavHostController) {
    TopAppBar(title = { Text(title) }, navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "返回"
            )
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDialog(
    open: Boolean, title: String, description: String, onConfirm: () -> Unit, onDismiss: () -> Unit
) {
    if (open) AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,

            ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.headlineSmall)
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text("取消")
                        }
                        TextButton(onClick = onConfirm) {
                            Text("确定")
                        }
                    }
                }
            }
        }

    }
}

@Preview
@Composable
fun ConfirmDialogPreview() {
    ConfirmDialog(open = true,
        title = "test123",
        description = "desc",
        onConfirm = { },
        onDismiss = { })
}