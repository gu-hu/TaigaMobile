package io.eugenethedev.taigamobile.viewmodels

import android.content.Context
import io.eugenethedev.taigamobile.BaseUnitTest
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.eugenethedev.taigamobile.domain.repositories.*
import io.eugenethedev.taigamobile.state.Session
import io.eugenethedev.taigamobile.state.Settings
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.createtask.CreateTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.dashboard.DashboardViewModel
import io.eugenethedev.taigamobile.ui.screens.epics.EpicsViewModel
import io.eugenethedev.taigamobile.ui.screens.issues.IssuesViewModel
import io.eugenethedev.taigamobile.ui.screens.kanban.KanbanViewModel
import io.eugenethedev.taigamobile.ui.screens.login.LoginViewModel
import io.eugenethedev.taigamobile.ui.screens.main.MainViewModel
import io.eugenethedev.taigamobile.ui.screens.projectselector.ProjectSelectorViewModel
import io.eugenethedev.taigamobile.ui.screens.scrum.ScrumViewModel
import io.eugenethedev.taigamobile.ui.screens.settings.SettingsViewModel
import io.eugenethedev.taigamobile.ui.screens.sprint.SprintViewModel
import io.eugenethedev.taigamobile.ui.screens.team.TeamViewModel
import io.mockk.*
import org.junit.AfterClass
import kotlin.test.AfterTest

abstract class BaseViewModelTest : BaseUnitTest() {
    protected val mockAppComponent = MockAppComponent()

    // state mocks
    private val mockContext = mockk<Context> {
        every { getSharedPreferences(any(), any()) } returns mockk(relaxed = true)
    }
    protected val mockSession = spyk(Session(mockContext))
    protected val mockSettings = spyk(Settings(mockContext))

    // repository mocks
    protected val mockAuthRepository = mockk<IAuthRepository>(relaxed = true)
    protected val mockTaskRepository = mockk<ITasksRepository>(relaxed = true)
    protected val mockSprintsRepository = mockk<ISprintsRepository>(relaxed = true)
    protected val mockSearchRepository = mockk<ISearchRepository>(relaxed = true)
    protected val mockUsersRepository = mockk<IUsersRepository>(relaxed = true)

    @AfterTest
    fun resetMocks() {
        clearAllMocks()
    }

    companion object {
        @AfterClass
        @JvmStatic
        fun unmock() {
            unmockkAll()
        }
    }

    inner class MockAppComponent : AppComponent {
        override fun inject(mainViewModel: MainViewModel) {
            mainViewModel.session = mockSession
            mainViewModel.settings = mockSettings
        }

        override fun inject(loginViewModel: LoginViewModel) {
            loginViewModel.authRepository = mockAuthRepository
        }

        override fun inject(dashboardViewModel: DashboardViewModel) {
            dashboardViewModel.tasksRepository = mockTaskRepository
            dashboardViewModel.session = mockSession
        }

        override fun inject(scrumViewModel: ScrumViewModel) {
            scrumViewModel.tasksRepository = mockTaskRepository
            scrumViewModel.sprintsRepository = mockSprintsRepository
            scrumViewModel.session = mockSession
        }

        override fun inject(epicsViewModel: EpicsViewModel) {
            epicsViewModel.session = mockSession
            epicsViewModel.tasksRepository = mockTaskRepository
        }

        override fun inject(projectSelectorViewModel: ProjectSelectorViewModel) {
            projectSelectorViewModel.searchRepository = mockSearchRepository
            projectSelectorViewModel.session = mockSession
        }

        override fun inject(sprintViewModel: SprintViewModel) {
            sprintViewModel.tasksRepository = mockTaskRepository
            sprintViewModel.sprintsRepository = mockSprintsRepository
            sprintViewModel.session = mockSession
        }

        override fun inject(commonTaskViewModel: CommonTaskViewModel) {
            commonTaskViewModel.session = mockSession
            commonTaskViewModel.tasksRepository = mockTaskRepository
            commonTaskViewModel.usersRepository = mockUsersRepository
            commonTaskViewModel.sprintsRepository = mockSprintsRepository
        }

        override fun inject(teamViewModel: TeamViewModel) {
            teamViewModel.usersRepository = mockUsersRepository
            teamViewModel.session = mockSession
        }

        override fun inject(settingsViewModel: SettingsViewModel) {
            settingsViewModel.session = mockSession
            settingsViewModel.settings = mockSettings
            settingsViewModel.userRepository = mockUsersRepository
        }

        override fun inject(createTaskViewModel: CreateTaskViewModel) {
            createTaskViewModel.tasksRepository = mockTaskRepository
            createTaskViewModel.session = mockSession
        }

        override fun inject(issuesViewModel: IssuesViewModel) {
            issuesViewModel.session = mockSession
            issuesViewModel.tasksRepository = mockTaskRepository
        }

        override fun inject(kanbanViewModel: KanbanViewModel) {
            kanbanViewModel.tasksRepository = mockTaskRepository
            kanbanViewModel.usersRepository = mockUsersRepository
            kanbanViewModel.session = mockSession
        }

    }
}