package io.eugenethedev.taigamobile.data.api

import io.eugenethedev.taigamobile.domain.entities.Comment
import io.eugenethedev.taigamobile.domain.entities.ProjectInSearch
import io.eugenethedev.taigamobile.domain.entities.User
import retrofit2.Response
import retrofit2.http.*

interface TaigaApi {
    companion object {
        const val API_PREFIX = "/api/v1/"
    }

    @POST("auth")
    suspend fun auth(@Body authRequest: AuthRequest): AuthResponse

    @GET("projects?order_by=user_order&discover_mode=true")
    suspend fun getProjects(
        @Query("q") query: String,
        @Query("page") page: Int
    ): List<ProjectInSearch>

    @GET("projects/{id}")
    suspend fun getProject(@Path("id") projectId: Long): ProjectResponse

    @GET("userstories/filters_data")
    suspend fun getUserStoriesFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("tasks/filters_data")
    suspend fun getTasksFiltersData(@Query("project") project: Long): FiltersDataResponse

    @GET("userstories")
    suspend fun getUserStories(
        @Query("project") project: Long,
        @Query("milestone") sprint: Any,
        @Query("status") status: Long,
        @Query("page") page: Int
    ): List<CommonTaskResponse>

    @GET("tasks?order_by=us_order")
    suspend fun getTasks(
        @Query("project") project: Long,
        @Query("milestone") sprint: Long?,
        @Query("user_story") userStory: Any,
        @Query("page") page: Int?
    ): List<CommonTaskResponse>

    @GET("milestones")
    suspend fun getSprints(
        @Query("project") project: Long,
        @Query("page") page: Int
    ): List<SprintResponse>

    @GET("userstories/{id}")
    suspend fun getUserStory(@Path("id") storyId: Long): CommonTaskResponse

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") taskId: Long): CommonTaskResponse

    @GET("history/userstory/{id}?type=comment")
    suspend fun getUserStoryComments(@Path("id") userStoryId: Long): List<Comment>

    @GET("history/task/{id}?type=comment")
    suspend fun getTaskComments(@Path("id") taskId: Long): List<Comment>

    @POST("history/userstory/{id}/delete_comment")
    suspend fun deleteUserStoryComment(
        @Path("id") userStoryId: Long,
        @Query("id") commentId: String
    )

    @POST("history/task/{id}/delete_comment")
    suspend fun deleteTaskComment(
        @Path("id") taskId: Long,
        @Query("id") commentId: String
    )

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Long): User

    @GET("users/me")
    suspend fun getMyProfile(): User

    @GET("projects/{id}/member_stats")
    suspend fun getMemberStats(@Path("id") projectId: Long): MemberStatsResponse

    @GET("milestones/{id}")
    suspend fun getSprint(@Path("id") sprintId: Long): SprintResponse

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskStatus(
        @Path("id") id: Long,
        @Body changeStatusRequest: ChangeStatusRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStorySprint(
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskSprint(
        @Path("id") id: Long,
        @Body changeSprintRequest: ChangeSprintRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeAssigneesRequest
    )

    @PATCH("userstories/{id}")
    suspend fun changeUserStoryWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskAssignees(
        @Path("id") id: Long,
        @Body changeAssigneesRequest: ChangeTaskAssigneesRequest
    )

    @PATCH("tasks/{id}")
    suspend fun changeTaskWatchers(
        @Path("id") id: Long,
        @Body changeWatchersRequest: ChangeWatchersRequest
    )

    @PATCH("userstories/{id}")
    suspend fun createUserStoryComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("tasks/{id}")
    suspend fun createTaskComment(
        @Path("id") id: Long,
        @Body createCommentRequest: CreateCommentRequest
    )

    @PATCH("userstories/{id}")
    suspend fun editUserStory(
        @Path("id") id: Long,
        @Body editTaskRequest: EditTaskRequest
    )

    @PATCH("tasks/{id}")
    suspend fun editTask(
        @Path("id") id: Long,
        @Body editTaskRequest: EditTaskRequest
    )

    @POST("userstories")
    suspend fun createUserStory(@Body createUserStoryRequest: CreateUserStoryRequest): CommonTaskResponse

    @POST("tasks")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest): CommonTaskResponse

    @DELETE("userstories/{id}")
    suspend fun deleteUserStory(@Path("id") id: Long): Response<Void>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Long): Response<Void>

}
