package io.eugenethedev.taigamobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.eugenethedev.taigamobile.*
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.domain.repositories.IUsersRepository
import io.eugenethedev.taigamobile.ui.commons.MutableResultFlow
import io.eugenethedev.taigamobile.ui.commons.ScreensState
import io.eugenethedev.taigamobile.ui.utils.loadOrError
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    @Inject lateinit var session: Session
    @Inject lateinit var settings: Settings
    @Inject lateinit var userRepository: IUsersRepository
    @Inject lateinit var screensState: ScreensState

    val user = MutableResultFlow<User>()
    val serverUrl get() = session.server

    val themeSetting by lazy { settings.themeSetting }

    init {
        TaigaApp.appComponent.inject(this)
    }

    fun start() = viewModelScope.launch {
        user.loadOrError(preserveValue = false) { userRepository.getMe() }
    }

    fun logout() {
        screensState.modify()
        session.reset()
    }

    fun switchTheme(theme: ThemeSetting) {
        settings.changeThemeSetting(theme)
    }
}