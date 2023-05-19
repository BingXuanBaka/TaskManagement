package com.bingxuan.taskmanagement.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.bingxuan.taskmanagement.R
import com.bingxuan.taskmanagement.data.Task
import com.bingxuan.taskmanagement.data.TaskDao
import com.bingxuan.taskmanagement.data.TaskDatabase
import com.bingxuan.taskmanagement.data.parseDate
import com.bingxuan.taskmanagement.ui.theme.TaskManagementTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class MainPageViewModel(context: Context, scope: CoroutineScope) : ViewModel() {
    private val dao: TaskDao = TaskDatabase.getDatabase(context = context).getDao()

    val taskList: StateFlow<List<Task>> = dao.getTasksOrderById().stateIn(
        scope = scope, SharingStarted.WhileSubscribed(), initialValue = listOf()
    )

    suspend fun updateTask(task: Task) {
        dao.update(task)
    }
}


@Composable
fun MainPage(
    navController: NavHostController,
    context: Context,
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val viewModel = MainPageViewModel(context = context, scope = scope)
    val state = viewModel.taskList.collectAsState()

    TaskManagementTheme {
        Scaffold(topBar = {
            SearchBar(searchQuery) { query -> searchQuery = query }
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add") {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "添加"
                )
            }
        }) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                TaskItemsContainer(
                    taskList = state.value,
                    onTaskChangeCompleted = { completed, task ->
                        scope.launch {
                            println(task)
                            println(completed)
                            viewModel.updateTask(task.copy(completed = completed))
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(searchQuery: String, setSearchQuery: (query: String) -> Unit) {
    SearchBar(
        modifier = Modifier
            .padding(12.dp, 0.dp)
            .fillMaxWidth(),
        query = searchQuery,
        onQueryChange = { content -> setSearchQuery(content) },
        onSearch = { content -> println(content) },
        active = false,
        onActiveChange = {},
        placeholder = { Text("搜索代办事项") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_search_24),
                contentDescription = "搜索"
            )
        },
    ) {}
}

@Composable
private fun TaskItemsContainer(
    taskList: List<Task>,
    onTaskChangeCompleted: (completed: Boolean, task: Task) -> Unit
) {
    LazyColumn {
        items(taskList) { task ->
            ListItem(headlineContent = {
                Text(task.name)
            }, trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(task.date?.let { parseDate(Date(task.date)) } ?: "")
                    Checkbox(checked = task.completed, onCheckedChange = {
                        onTaskChangeCompleted(it, task)
                    })
                }

            })
        }
    }
}