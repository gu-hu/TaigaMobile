package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.ProjectInSearch
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun ProjectSelectorScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: ProjectSelectorViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }
    val coroutineScope = rememberCoroutineScope()

    val projects by viewModel.projects.observeAsState()
    projects?.subscribeOnError(onError)

    var isSelectorVisible by remember { mutableStateOf(true) }
    val selectorAnimationDuration = AnimationConstants.DefaultDurationMillis

    fun navigateBack() = coroutineScope.launch {
        isSelectorVisible = false
        delay(selectorAnimationDuration.toLong())
        navController.popBackStack()
    }

    val isProjectSelected by viewModel.isProjectSelected.observeAsState()
    if (isProjectSelected == true) navigateBack()


    ProjectSelectorScreenContent(
        projects = projects?.data.orEmpty(),
        isVisible = isSelectorVisible,
        isLoading = projects?.resultStatus == ResultStatus.LOADING,
        selectorAnimationDuration = selectorAnimationDuration,
        navigateBack = ::navigateBack,
        loadData = { viewModel.loadData(it) },
        selectProject = viewModel::selectProject
    )

}


@ExperimentalAnimationApi
@Composable
fun ProjectSelectorScreenContent(
    projects: List<ProjectInSearch>,
    isVisible: Boolean = false,
    isLoading: Boolean = false,
    selectorAnimationDuration: Int = AnimationConstants.DefaultDurationMillis,
    navigateBack: () -> Unit = {},
    loadData: (String) -> Unit = {},
    selectProject: (ProjectInSearch) -> Unit  = {}
) = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopStart
) {
    SelectorList(
        titleHint = stringResource(R.string.search_projects_hint),
        items = projects,
        isVisible = isVisible,
        isLoading = isLoading,
        loadData = loadData,
        navigateBack = navigateBack,
        animationDurationMillis = selectorAnimationDuration
    ) {
        ItemProject(
            project = it,
            onClick = { selectProject(it) }
        )
    }
}

@Composable
private fun ItemProject(
    project: ProjectInSearch,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Column {

        project.takeIf { it.isMember || it.isAdmin || it.isOwner  }?.let {
            Text(
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary,
                text = stringResource(
                    when {
                        project.isOwner -> R.string.project_owner
                        project.isAdmin -> R.string.project_admin
                        project.isMember -> R.string.project_member
                        else -> 0
                    }
                )
            )
        }

        Text(
            text = stringResource(R.string.project_name_template).format(project.name, project.slug),
            style = MaterialTheme.typography.body1,
        )
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProjectSelectorScreenPreview() = TaigaMobileTheme {
    ProjectSelectorScreenContent(
        listOf(
            ProjectInSearch(0, "Cool", "slug",false, false, false),
            ProjectInSearch(1, "Cooler", "slug", true, false, false)
        ),
        isVisible = true
    )
}

