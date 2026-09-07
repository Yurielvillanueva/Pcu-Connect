package edu.pcu.connect.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.ui.graphics.vector.ImageVector
import edu.pcu.connect.model.Role
import edu.pcu.connect.model.isStaff

object Routes {
    const val SIGN_IN = "sign_in"
    const val ONBOARDING = "onboarding"

    const val FEED = "feed"
    const val COMPOSE_ANNOUNCEMENT = "feed/compose"
    const val TEAMS = "teams"
    const val TEAM_CHAT = "teams/{teamId}"
    const val DASHBOARD = "dashboard"
    const val SERVICES = "services"
    const val REQUEST_FORM = "services/{officeId}"
    const val ADMIN_REQUEST_QUEUE = "admin_request_queue"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val ADMIN_PORTAL = "admin"

    fun teamChat(teamId: String) = "teams/$teamId"
    fun requestForm(officeId: String) = "services/$officeId"
}

data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

fun bottomDestinationsFor(role: Role): List<BottomDestination> {
    val base = listOf(
        BottomDestination(Routes.FEED, "Feed", Icons.Filled.Newspaper),
        BottomDestination(Routes.TEAMS, "Teams", Icons.Filled.Forum),
        BottomDestination(Routes.DASHBOARD, "Dashboard", Icons.Filled.CalendarMonth),
        BottomDestination(Routes.SERVICES, "Services", Icons.Filled.RoomService),
        BottomDestination(Routes.PROFILE, "Profile", Icons.Filled.Person),
    )
    return if (role.isStaff()) {
        base + BottomDestination(Routes.ADMIN_PORTAL, "Admin", Icons.Filled.AdminPanelSettings)
    } else {
        base
    }
}
