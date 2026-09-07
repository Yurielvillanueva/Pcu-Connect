package edu.pcu.connect.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.pcu.connect.data.AppViewModel
import edu.pcu.connect.data.SignInResult
import edu.pcu.connect.ui.home.MainScaffold
import edu.pcu.connect.ui.onboarding.OnboardingScreen
import edu.pcu.connect.ui.signin.SignInScreen

private const val MAIN = "main"

@Composable
fun RootNavHost(viewModel: AppViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SIGN_IN) {
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onSignIn = { email, password, role ->
                    when (viewModel.signIn(email, password, role)) {
                        is SignInResult.Verified -> true
                        SignInResult.NoMatch -> false
                    }
                },
                onSignedIn = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.SIGN_IN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.ONBOARDING) {
            val user = viewModel.currentUser
            if (user != null) {
                OnboardingScreen(
                    user = user,
                    onComplete = {
                        viewModel.completeOnboarding()
                        navController.navigate(MAIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }
        }
        composable(MAIN) {
            MainScaffold(
                viewModel = viewModel,
                onSignOut = {
                    viewModel.signOut()
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
