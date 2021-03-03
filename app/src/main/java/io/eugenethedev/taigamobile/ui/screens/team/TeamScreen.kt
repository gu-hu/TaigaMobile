package io.eugenethedev.taigamobile.ui.screens.team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import dev.chrisbanes.accompanist.glide.GlideImage
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.TeamMember
import io.eugenethedev.taigamobile.ui.components.Loader
import io.eugenethedev.taigamobile.ui.components.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import java.text.SimpleDateFormat

@Composable
fun TeamScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: TeamViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    TeamScreenContent(
        projectName = viewModel.projectName,
        team = team?.data.orEmpty(),
        isLoading = team?.resultStatus == ResultStatus.LOADING,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        }
    )
}

@Composable
fun TeamScreenContent(
    projectName: String,
    team: List<TeamMember> = emptyList(),
    isLoading: Boolean = false,
    onTitleClick: () -> Unit = {}
) = Column(Modifier.fillMaxSize()) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
            ) {
                Text(
                    text = projectName.takeIf { it.isNotEmpty() }
                        ?: stringResource(R.string.choose_project_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 250.dp)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = null
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Loader()
            }
        }
        team.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                items(team) {
                    TeamMemberItem(it)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamMemberItem(
    teamMember: TeamMember
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val dateTimeFormatter = remember { SimpleDateFormat.getDateTimeInstance() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.7f)
    ) {
        GlideImage(
            data = teamMember.avatarUrl ?: R.drawable.default_avatar,
            contentDescription = null,
            fadeIn = true,
            requestBuilder = { error(R.drawable.default_avatar) },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = teamMember.name,
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                text = teamMember.role,
                color = Color.Gray,
                style = MaterialTheme.typography.body1,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.weight(0.3f)
    ) {
        Text(
            text = teamMember.totalPower.toString(),
            style = MaterialTheme.typography.h5
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.power),
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TeamScreenPreview() = TaigaMobileTheme {
    TeamScreenContent(
        projectName = "Name",
        team = List(3) {
            TeamMember(
                id = 0L,
                avatarUrl = null,
                name = "First Last",
                role = "Cool guy",
                totalPower = 14
            )
        }
    )
}