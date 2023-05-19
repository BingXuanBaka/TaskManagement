package com.bingxuan.taskmanagement.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bingxuan.taskmanagement.R
import com.bingxuan.taskmanagement.data.Task
import com.bingxuan.taskmanagement.data.TaskDao
import com.bingxuan.taskmanagement.data.TaskDatabase
import com.bingxuan.taskmanagement.data.parseDate
import kotlinx.coroutines.launch
import java.util.Date


class AddPageViewModel(context: Context) {
    private val dao: TaskDao = TaskDatabase.getDatabase(context = context).getDao()

    var name by mutableStateOf("")
    var date: Date? by mutableStateOf(null)
    suspend fun insertTask() {
        println(name != "")
        dao.insert(Task(name = name, date = date?.time))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPage(navController: NavHostController, context: Context) {
    val viewModel = AddPageViewModel(context = context)


    val coroutineScope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(title = { Text("新增代办") }, navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
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
            AddPageBody(name = viewModel.name,
                setName = { viewModel.name = it },
                date = viewModel.date,
                setDate = { viewModel.date = it },
                addButtonDisabled = viewModel.name == "",
                onAddButtonClicked = {
                    coroutineScope.launch {
                        viewModel.insertTask()
                        navController.popBackStack()
                    }
                })

        }

    }
}

@Composable
fun AddPageBody(
    name: String,
    setName: (name: String) -> Unit,
    date: Date?,
    setDate: (date: Date?) -> Unit,
    addButtonDisabled: Boolean,
    onAddButtonClicked: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End
    ) {
        OutlinedTextField(value = name,
            modifier = Modifier
                .padding(11.dp)
                .fillMaxWidth(),
            label = { Text("名称") },
            onValueChange = { setName(it) })

        OptionsList(date) { setDate(it) }

        Row(
            modifier = Modifier.padding(11.dp),
        ) {
            Button(
                onClick = {
                    onAddButtonClicked()
                }, enabled = !addButtonDisabled
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "添加"
                )
                Text("添加")
            }
        }

    }
}

@Composable
private fun OptionsList(
    date: Date?, setDate: (date: Date?) -> Unit
) {
    var dateSelectMenuExpanded by remember {
        mutableStateOf(false)
    }

    ListItem(headlineContent = {
        Text(date?.let { "${parseDate(it)} 后提醒" } ?: "不提醒")
    }, leadingContent = {
        Icon(
            painter = painterResource(
                id = if (date == null) {
                    R.drawable.baseline_calendar_today_24
                } else {
                    R.drawable.baseline_today_24
                }
            ), contentDescription = "日期"
        )
    }, trailingContent = {
        DateSelectMenu(
            dateSelectMenuExpanded,
            { expanded: Boolean -> dateSelectMenuExpanded = expanded },
            setDate
        )
    },

        modifier = Modifier.clickable(interactionSource = MutableInteractionSource(),
            indication = rememberRipple(),
            onClick = {
                dateSelectMenuExpanded = true
            })

    )
}

@Composable
private fun DateSelectMenu(
    menuExpanded: Boolean,
    setMenuExpanded: (expanded: Boolean) -> Unit,
    setDate: (date: Date?) -> Unit
) {
    var newDate: Date? by remember {
        mutableStateOf(null)
    }

    var dateDialogOpen by remember {
        mutableStateOf(false)
    }
    var timeDialogOpen by remember {
        mutableStateOf(false)
    }

    val oneDay = 1 * 24 * 60 * 60 * 1000

    DateDialog(dateDialogOpen, { date ->
        newDate = date
        dateDialogOpen = false
        timeDialogOpen = true
    }, onDismiss = { dateDialogOpen = false })

    TimeDialog(timeDialogOpen, { hour, minute ->
        newDate?.hours = hour
        newDate?.minutes = minute
        setDate(newDate)
        timeDialogOpen = false
    }, { timeDialogOpen = false })

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = { setMenuExpanded(false) },
    ) {
        DropdownMenuItem(onClick = {
            setDate(null)
            setMenuExpanded(false)
        }, text = {
            Text("不提醒")
        })

        DropdownMenuItem(onClick = {
            dateDialogOpen = true
            setMenuExpanded(false)
        }, text = {
            Text("选择提醒日期")
        })

        Divider()

        DropdownMenuItem(onClick = {
            newDate = Date()
            newDate?.let { it.time += oneDay }

            setMenuExpanded(false)
            timeDialogOpen = true
        }, text = {
            Text("明天")
        })

        DropdownMenuItem(onClick = {
            newDate = Date()
            newDate?.let { it.time += oneDay }

            setMenuExpanded(false)
            timeDialogOpen = true
        }, text = {
            Text("后天")
        })
    }
}

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