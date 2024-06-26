package com.example.articlesappandroid.dashboard.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.articlesappandroid.ArticlesApp
import com.example.articlesappandroid.auth.application.AuthViewModel
import com.example.articlesappandroid.common.helpers.viewModelFactory
import com.example.articlesappandroid.dashboard.application.DashboardState
import com.example.articlesappandroid.dashboard.application.DashboardViewModel
import com.example.articlesappandroid.dashboard.domain.Filters
import com.example.articlesappandroid.dashboard.domain.getName
import com.example.articlesappandroid.dashboard.presentation.composables.ArticlesList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController){
    var filtersExpanded by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(Filters.ALL) }
    val viewModel = viewModel<DashboardViewModel>(
        factory = viewModelFactory{
            DashboardViewModel(ArticlesApp.di.dashboardRepository)
            }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text("Articles")
                },
                actions = {
                    IconButton(onClick = { filtersExpanded = !filtersExpanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Filters"
                        )
                    }
                    DropdownMenu(
                        expanded = filtersExpanded,
                        onDismissRequest = { filtersExpanded = false }
                    ) {
                        Filters.entries.map {
                            DropdownMenuItem(
                                text = { Text(it.getName()) },
                                onClick = {
                                    filter = it
                                    filtersExpanded = false
                                    viewModel.fetchData(filter)
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Add article FAB"
                    )
            }
        }
    ){
        Surface (modifier = Modifier
            .padding(it)
            .fillMaxSize()){
            when(state){
                is DashboardState.Data -> {
                    val castedState = state as DashboardState.Data
                        ArticlesList(
                        articles = castedState.articles,
                        actualPage = castedState.actualPage,
                        morePagesLoading = castedState.morePagesLoading,
                        deletingInProgressArticleIds = castedState.deletingInProgressArticleIds,
                        filter = filter
                        )
                }
                is DashboardState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){ Button(
                    onClick = {
                        viewModel.fetchData(filter)
                    }) {
                    Text(text = "Retry")
                }}
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){ CircularProgressIndicator()}
            }
        }
    }
}