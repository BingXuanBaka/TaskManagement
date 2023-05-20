package com.bingxuan.taskmanagement.ui.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.bingxuan.taskmanagement.ui.ConfirmDialog
import com.bingxuan.taskmanagement.ui.DateDialog
import com.bingxuan.taskmanagement.ui.TimeDialog
import com.bingxuan.taskmanagement.ui.TopBar
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun EditPage(navController: NavHostController, context: Context, taskID: Int) {
    val coroutineScope = rememberCoroutineScope()
    val dao: TaskDao = TaskDatabase.getDatabase(context = context).getDao()

    suspend fun updateTask(task: Task) = dao.update(task = task)
    suspend fun deleteTask(task: Task) = dao.delete(task = task)

    var currentTask: Task? by remember { mutableStateOf(null) }
    var confirmDeleteDialogOpen: Boolean by remember { mutableStateOf(false) }

    LaunchedEffect(taskID) {
        val task = dao.getTaskByID(taskID).firstOrNull()
        currentTask = task
    }

    Scaffold(topBar = {
        TopBar(title = "编辑待办", navController = navController)
    }) { padding ->
        Box(
            modifier = Modifier.padding(padding),
        ) {
            currentTask?.let { task ->
                EditPageBody(name = task.name,
                    setName = { currentTask = task.copy(name = it) },
                    date = task.date?.let { Date(it) },
                    setDate = { currentTask = task.copy(date = it?.time) },
                    confirmButtonDisabled = task.name == "",
                    onConfirmButtonClicked = {
                        coroutineScope.launch {
                            updateTask(task)
                            navController.popBackStack()
                        }
                    },
                    onDeleteButtonClicked = {
                        confirmDeleteDialogOpen = true
                    })

                ConfirmDialog(open = confirmDeleteDialogOpen,
                    title = "删除",
                    description = "您确定要删除这个待办事项吗？",
                    onConfirm = {
                        coroutineScope.launch {
                            confirmDeleteDialogOpen = false
                            deleteTask(task)
                            navController.popBackStack()
                        }
                    },
                    onDismiss = {
                        confirmDeleteDialogOpen = false
                    })
            } ?: Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
        }
    }
}

@Composable
fun EditPageBody(
    name: String,
    setName: (name: String) -> Unit,
    date: Date?,
    setDate: (date: Date?) -> Unit,
    confirmButtonDisabled: Boolean,
    onConfirmButtonClicked: () -> Unit,
    onDeleteButtonClicked: () -> Unit,
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
            modifier = Modifier.padding(11.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(onClick = {
                onDeleteButtonClicked()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "删除"
                )
                Text("删除待办")
            }

            Button(
                onClick = {
                    onConfirmButtonClicked()
                }, enabled = !confirmButtonDisabled
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_24),
                    contentDescription = "确定"
                )
                Text("确定")
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
            newDate?.let { it.time += oneDay * 2 }

            setMenuExpanded(false)
            timeDialogOpen = true
        }, text = {
            Text("后天")
        })
    }
}
