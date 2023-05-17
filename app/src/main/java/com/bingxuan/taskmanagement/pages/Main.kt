package com.bingxuan.taskmanagement.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bingxuan.taskmanagement.R
import com.bingxuan.taskmanagement.Task
import com.bingxuan.taskmanagement.ui.theme.TaskManagementTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    TaskManagementTheme {
        Scaffold(
            topBar = {
                SearchBar(
                    modifier = Modifier
                        .padding(12.dp, 0.dp)
                        .fillMaxWidth(),
                    query = searchQuery,
                    onQueryChange = { content -> searchQuery = content },
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add") {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "添加"
                    )
                }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                TaskItemsContainer()
            }
        }
    }
}

@Composable
fun TopSearchBar(){

}

@Composable
fun TaskItemsContainer() {
    val taskList by remember {
        mutableStateOf(
            mutableStateListOf(
                Task(name = "this is a task", completed = false),
                Task(name = "this is also a task", completed = false),
                Task(name = "this is a completed task", completed = true),
            )
        )
    }

    LazyColumn {
        item {
            taskList.forEachIndexed { index, task ->
                ListItem(headlineContent = {
                    Text(task.name)
                }, trailingContent = {
                    Checkbox(checked = taskList[index].completed, onCheckedChange = { checked ->

                        taskList[index] = task.copy(completed = checked)

                    })
                })
            }
        }
    }
}