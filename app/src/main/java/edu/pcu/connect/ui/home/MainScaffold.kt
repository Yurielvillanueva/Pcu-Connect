package edu.pcu.connect.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import edu.pcu.connect.data.AppViewModel
import edu.pcu.connect.model.canManageFeed
import edu.pcu.connect.model.canModerateChats
import edu.pcu.connect.model.canReviewRequests
import edu.pcu.connect.nav.Routes
import edu.pcu.connect.nav.bottomDestinationsFor
import edu.pcu.connect.ui.admin.AdminPortalScreen
import edu.pcu.connect.ui.dashboard.AcademicDashboardScreen
import edu.pcu.connect.ui.feed.CampusFeedScreen
import edu.pcu.connect.ui.feed.ComposeAnnouncementScreen
import edu.pcu.connect.ui.notifications.NotificationsScreen
import edu.pcu.connect.ui.profile.ProfileScreen
import edu.pcu.connect.ui.services.AdminRequestQueueScreen
import edu.pcu.connect.ui.services.RequestFormScreen
import edu.pcu.connect.ui.services.ServicesHubScreen
import edu.pcu.connect.ui.teams.TeamChatScreen
import edu.pcu.connect.ui.teams.TeamsListScreen

@Composable
fun MainScaffold(viewModel: AppViewModel, onSignOut: () -> Unit) {
    val user = viewModel.currentUser ?: return
    val innerNav = rememberNavController()
    val backStackEntry by innerNav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                bottomDestinationsFor(user.role).forEach { destination ->
                    val selected = currentRoute?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            innerNav.navigate(destination.route) {
                                popUpTo(innerNav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = innerNav,
            startDestination = Routes.FEED,
            modifier = androidx.compose.ui.Modifier.padding(padding),
        ) {
            composable(Routes.FEED) {
                CampusFeedScreen(
                    viewModel = viewModel,
                    canCompose = user.role.canManageFeed(),
                    onCompose = { innerNav.navigate(Routes.COMPOSE_ANNOUNCEMENT) },
                    onOpenNotifications = { innerNav.navigate(Routes.NOTIFICATIONS) },
                )
            }
            composable(Routes.COMPOSE_ANNOUNCEMENT) {
                ComposeAnnouncementScreen(
                    viewModel = viewModel,
                    onDone = { innerNav.popBackStack() },
                )
            }
            composable(Routes.TEAMS) {
                TeamsListScreen(
                    viewModel = viewModel,
                    onOpenTeam = { teamId -> innerNav.navigate(Routes.teamChat(teamId)) },
                )
            }
            composable(
                route = Routes.TEAM_CHAT,
                arguments = listOf(navArgument("teamId") { type = NavType.StringType }),
            ) { entry ->
                val teamId = entry.arguments?.getString("teamId").orEmpty()
                TeamChatScreen(
                    viewModel = viewModel,
                    teamId = teamId,
                    currentUserName = user.fullName,
                    canModerate = user.role.canModerateChats(),
                    onBack = { innerNav.popBackStack() },
                )
            }
            composable(Routes.DASHBOARD) {
                AcademicDashboardScreen(viewModel = viewModel)
            }
            composable(Routes.SERVICES) {
                ServicesHubScreen(
                    viewModel = viewModel,
                    canReview = user.role.canReviewRequests(),
                    onOpenOffice = { officeId -> innerNav.navigate(Routes.requestForm(officeId)) },
                    onOpenQueue = { innerNav.navigate(Routes.ADMIN_REQUEST_QUEUE) },
                )
            }
            composable(
                route = Routes.REQUEST_FORM,
                arguments = listOf(navArgument("officeId") { type = NavType.StringType }),
            ) { entry ->
                val officeId = entry.arguments?.getString("officeId").orEmpty()
                RequestFormScreen(
                    viewModel = viewModel,
                    officeId = officeId,
                    requestedBy = user.fullName,
                    onSubmitted = { innerNav.popBackStack() },
                )
            }
            composable(Routes.ADMIN_REQUEST_QUEUE) {
                AdminRequestQueueScreen(viewModel = viewModel, onBack = { innerNav.popBackStack() })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(user = user, onSignOut = onSignOut)
            }
            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(viewModel = viewModel, onBack = { innerNav.popBackStack() })
            }
            composable(Routes.ADMIN_PORTAL) {
                AdminPortalScreen(
                    user = user,
                    onComposeAnnouncement = { innerNav.navigate(Routes.COMPOSE_ANNOUNCEMENT) },
                    onReviewRequests = { innerNav.navigate(Routes.ADMIN_REQUEST_QUEUE) },
                    onModerateChats = { innerNav.navigate(Routes.TEAMS) },
                )
            }
        }
    }
}
