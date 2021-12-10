package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.commonVerticalPadding
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun DashboardScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: DashboardViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.onOpen()
    }

    val workingOn by viewModel.workingOn.collectAsState()
    workingOn.subscribeOnError(onError)

    val watching by viewModel.watching.collectAsState()
    watching.subscribeOnError(onError)

    DashboardScreenContent(
        navController = navController,
        isLoading = listOf(workingOn, watching).any { it is LoadingResult<*> },
        workingOn = workingOn.data.orEmpty(),
        watching = watching.data.orEmpty(),
        navigateToTask = {
            viewModel.changeCurrentProject(it)
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        }
    )

}

@Composable
fun DashboardScreenContent(
    navController: NavController,
    isLoading: Boolean = false,
    workingOn: List<CommonTask> = emptyList(),
    watching: List<CommonTask> = emptyList(),
    navigateToTask: (CommonTask) -> Unit = { _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    var menuVisible by remember { mutableStateOf(false) }

    AppBarWithBackButton(
        title = { Text(stringResource(R.string.dashboard)) },
        actions = {
            IconButton(onClick = { menuVisible = !menuVisible }) {
                Icon(Icons.Default.MoreVert, "")
            }

            DropdownMenu(
                expanded = menuVisible,
                onDismissRequest = { menuVisible = false },
                modifier = Modifier.width(150.dp)
            ) {
                DDItem(navController, R.drawable.ic_team, R.string.team, Routes.team)
                DDItem(navController, R.drawable.ic_settings, R.string.settings, Routes.settings)
            }
        },
    )

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        HorizontalTabbedPager(
            tabs = Tabs.values(),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (Tabs.values()[page]) {
                Tabs.WorkingOn -> TabContent(
                    commonTasks = workingOn,
                    navigateToTask = navigateToTask
                )
                Tabs.Watching -> TabContent(
                    commonTasks = watching,
                    navigateToTask = navigateToTask
                )
            }
        }
    }

}

private enum class Tabs(@StringRes override val titleId: Int) : Tab {
    WorkingOn(R.string.working_on),
    Watching(R.string.watching)
}

@Composable
private fun DDItem(
    navController: NavController,
    @DrawableRes iconId: Int,
    @StringRes nameId: Int,
    route: String,
) = DropdownMenuItem(onClick = { navController.navigate(route) }) {
    Icon(
        painter = painterResource(iconId),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = Color.Gray
    )

    Spacer(Modifier.width(8.dp))

    Text(stringResource(nameId))
}

@Composable
private fun TabContent(
    commonTasks: List<CommonTask>,
    navigateToTask: (CommonTask) -> Unit,
) = LazyColumn(Modifier.fillMaxSize()) {
    SimpleTasksListWithTitle(
        bottomPadding = commonVerticalPadding,
        horizontalPadding = mainHorizontalScreenPadding,
        showExtendedTaskInfo = true,
        commonTasks = commonTasks,
        navigateToTask = { id, _, _ -> navigateToTask(commonTasks.find { it.id == id }!!) },
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() = TaigaMobileTheme {
    DashboardScreenContent(rememberNavController())
}
