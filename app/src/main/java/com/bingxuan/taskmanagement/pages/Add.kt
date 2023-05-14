package com.bingxuan.taskmanagement.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bingxuan.taskmanagement.R
import com.bingxuan.taskmanagement.parseDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage() {
    var name by remember {
        mutableStateOf("")
    }

    var date: Date? by remember {
        mutableStateOf(null)
    }

    var dateSelectMenuExpanded by remember {
        mutableStateOf(false)
    }



    Scaffold(topBar = {
        TopAppBar(title = { Text("新增代办") }, navigationIcon = {
            IconButton(onClick = {/* TODO */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "返回"
                )
            }
        }

        )
    }) { padding ->
        Box(
            modifier = Modifier.padding(padding),
        ) {
            var dateDialogOpen by remember {
                mutableStateOf(false)
            }
            var timeDialogOpen by remember {
                mutableStateOf(false)
            }

            var newDate: Date? by remember {
                mutableStateOf(null)
            }
            DateDialog(
                dateDialogOpen,
                { date ->
                    newDate = date
                    dateDialogOpen = false
                    timeDialogOpen = true
                },
                onDismiss = { dateDialogOpen = false })

            TimeDialog(timeDialogOpen, { hour, minute ->
                newDate?.hours = hour
                newDate?.minutes  = minute
                date = newDate
                timeDialogOpen = false
            },{timeDialogOpen = false})

            Column(
                modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End
            ) {
                OutlinedTextField(value = name,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    label = { Text("名称") },
                    onValueChange = { value -> name = value })
                ListItem(
                    headlineContent = {
                        Text(date?.let { "${parseDate(it)} 后提醒" } ?: "不提醒")
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(
                                id = if (date == null) {
                                    R.drawable.baseline_calendar_today_24
                                } else {
                                    R.drawable.baseline_today_24
                                }
                            ), contentDescription = "日期"
                        )
                    },
                    trailingContent = {
                        DropdownMenu(
                            expanded = dateSelectMenuExpanded,
                            onDismissRequest = { dateSelectMenuExpanded = false },
                        ) {
                            DropdownMenuItem(onClick = {
                                date = null
                                dateSelectMenuExpanded = false
                            }, text = {
                                Text("不提醒")
                            })

                            DropdownMenuItem(onClick = {
                                dateDialogOpen = true
                                dateSelectMenuExpanded = false

                            }, text = {
                                Text("选择提醒日期")
                            })

                            Divider()

                            DropdownMenuItem(onClick = {
                                newDate = Date()
                                newDate?.let { it.time += 1 * 24 * 60 * 60 * 1000 }

                                dateSelectMenuExpanded = false
                                timeDialogOpen = true
                            }, text = {
                                Text("明天")
                            })

                            DropdownMenuItem(onClick = {
                                newDate = Date()
                                newDate?.let { it.time += 2 * 24 * 60 * 60 * 1000 }

                                dateSelectMenuExpanded = false
                                timeDialogOpen = true
                            }, text = {
                                Text("后天")
                            })
                        }
                    },

                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = rememberRipple(),
                        onClick = {
                            dateSelectMenuExpanded = true
                        }
                    )

                )

                Row(
                    modifier = Modifier.padding(12.dp),
                ) {
                    Button(
                        onClick = { /*TODO*/ }, enabled = name != ""
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "取消"
                        )
                        Text("添加")
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    open: Boolean, onAccept: (date: Date) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    if(open) DatePickerDialog(
        confirmButton = {
            TextButton(
                onClick = { onAccept(Date(datePickerState.selectedDateMillis ?: 0)) },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "取消")
            }
        },
        onDismissRequest = onDismiss
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
    if(open) DatePickerDialog(
        confirmButton = {
            TextButton(onClick = { onAccept(timePickerState.hour,timePickerState.minute) }) {
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
        TimePicker(state = timePickerState, modifier = Modifier.padding(8.dp, 12.dp).fillMaxWidth())
    }
}

@Preview
@Composable
fun PreviewAddPage() {
    AddPage()
}