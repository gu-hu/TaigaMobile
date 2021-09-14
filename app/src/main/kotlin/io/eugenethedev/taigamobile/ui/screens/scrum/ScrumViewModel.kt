package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.TaigaApp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.repositories.ISprintsRepository
import io.eugenethedev.taigamobile.domain.repositories.ITasksRepository
import io.eugenethedev.taigamobile.ui.commons.*
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class ScrumViewModel : ViewModel() {
    @Inject lateinit var tasksRepository: ITasksRepository
    @Inject lateinit var sprintsRepository: ISprintsRepository
    @Inject lateinit var session: Session
    @Inject lateinit var screensState: ScreensState

    val projectName: String get() = session.currentProjectName

    val stories = MutableResultFlow<List<CommonTask>?>()
    private var currentStoriesQuery = ""
    private var currentStoriesPage = 0
    private var maxStoriesPage = Int.MAX_VALUE

    val sprints = MutableResultFlow<List<Sprint>>()
    private var currentSprintPage = 0
    private var maxSprintPage = Int.MAX_VALUE

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() {
        if (screensState.shouldReloadScrumScreen) {
            reset()
        }

        if (stories.value is NothingResult) {
            loadStories()
            loadSprints()
        }
    }
    
    fun loadStories(query: String = "") = viewModelScope.launch {
        query.takeIf { it != currentStoriesQuery }?.let {
            currentStoriesQuery = it
            currentStoriesPage = 0
            maxStoriesPage = Int.MAX_VALUE
            stories.value = NothingResult()
        }

        if (currentStoriesPage == maxStoriesPage) return@launch

        stories.loadOrError {
            tasksRepository.getBacklogUserStories(++currentStoriesPage, query).also {
                if (it.isEmpty()) maxStoriesPage = currentStoriesPage
            }.let {
                stories.value.data.orEmpty() + it
            }
        }
    }

    fun loadSprints() = viewModelScope.launch {
        if (currentSprintPage == maxSprintPage) return@launch

        sprints.loadOrError {
            sprintsRepository.getSprints(++currentSprintPage).also {
                if (it.isEmpty()) maxSprintPage = currentSprintPage
            }.let {
                sprints.value.data.orEmpty() + it
            }
        }
    }

    fun createSprint(name: String, start: LocalDate, end: LocalDate) = viewModelScope.launch {
        sprints.loadOrError(R.string.permission_error) {
            sprintsRepository.createSprint(name, start, end)
            resetSprints()
            loadSprints().join()
            sprints.value.data
        }
    }

    private fun resetSprints() {
        sprints.value = NothingResult()
        currentSprintPage = 0
        maxSprintPage = Int.MAX_VALUE
    }

    fun reset() {
        stories.value = NothingResult()
        currentStoriesQuery = ""
        currentStoriesPage = 0
        maxStoriesPage = Int.MAX_VALUE

        resetSprints()
    }
}
