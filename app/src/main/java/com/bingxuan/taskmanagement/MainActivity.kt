package com.bingxuan.taskmanagement

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.bingxuan.taskmanagement.ui.theme.TaskManagementTheme
import java.util.*

data class Task(
    var name: String,
    var completed: Boolean,
    var date: Date? = null,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContent {
            TaskManagementTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    DefaultPreview()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TaskManagementTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(0.dp, 12.dp)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            SearchBar(query = searchQuery,
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
                trailingIcon = {
                    IconButton(
                        onClick = { menuExpanded = !menuExpanded }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_more_vert_24),
                            contentDescription = "查看选项"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = !menuExpanded })
                    {
                        DropdownMenuItem(
                            onClick = {
                                /* TODO */
                            },
                            text = {
                                Text("设置")
                            })
                    }

                }) {}
            TaskItemsContainer()
        }

    }

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

//@Composable
//fun TaskItem(task: Task){
//    var completed by remember { mutableStateOf(task.completed)}
//    ListItem(
//        headlineContent = {
//            Text(task.name)
//        },
//        trailingContent = {
//            Checkbox(
//                checked = completed,
//                onCheckedChange = {checked -> completed = checked}
//            )
//        }
//    )
//}