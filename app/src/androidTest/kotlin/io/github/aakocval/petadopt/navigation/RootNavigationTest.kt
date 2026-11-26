package io.github.aakocval.petadopt.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.aakocval.petadopt.feature.auth.navigation.AuthGraph
import io.github.aakocval.petadopt.feature.auth.navigation.LoginDestination
import io.github.aakocval.petadopt.feature.onboarding.navigation.OnboardingDestination
import io.github.aakocval.petadopt.feature.onboarding.navigation.OnboardingGraph
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Drives the real [switchRootTo] against a NavHost with the app's real root routes. Screens are
 * replaced by labels so the test needs no Hilt: what's under test is the back stack.
 */
@RunWith(AndroidJUnit4::class)
class RootNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    private lateinit var navController: NavHostController

    private fun showRoot(start: Any) {
        compose.setContent {
            navController = rememberNavController()
            NavHost(navController = navController, startDestination = start) {
                navigation<OnboardingGraph>(startDestination = OnboardingDestination) {
                    composable<OnboardingDestination> { Text("onboarding") }
                }
                navigation<AuthGraph>(startDestination = LoginDestination) {
                    composable<LoginDestination> { Text("login") }
                }
                composable<MainDestination> { Text("main") }
            }
        }
    }

    private fun assertNothingToGoBackTo() =
        compose.runOnIdle {
            assertNull(navController.previousBackStackEntry)
            assertFalse("Back must leave the app, not return to the previous graph", navController.popBackStack())
        }

    @Test
    fun finishingOnboarding_showsLogin_withNoWayBack() {
        showRoot(OnboardingGraph)
        compose.runOnIdle { navController.switchRootTo(AuthGraph) }

        compose.onNodeWithText("login").assertIsDisplayed()
        assertNothingToGoBackTo()
    }

    @Test
    fun signingIn_replacesLoginWithMain() {
        showRoot(AuthGraph)
        compose.runOnIdle { navController.switchRootTo(MainDestination) }

        compose.onNodeWithText("main").assertIsDisplayed()
        assertNothingToGoBackTo()
    }

    @Test
    fun signingOut_replacesMainWithLogin() {
        showRoot(MainDestination)
        compose.runOnIdle { navController.switchRootTo(AuthGraph) }

        compose.onNodeWithText("login").assertIsDisplayed()
        assertNothingToGoBackTo()
    }

    @Test
    fun switchingToTheCurrentRoot_keepsTheSameEntry() {
        showRoot(MainDestination)
        val before = compose.runOnIdle { navController.currentBackStackEntry?.id }

        compose.runOnIdle { navController.switchRootTo(MainDestination) }

        compose.runOnIdle { assertEquals(before, navController.currentBackStackEntry?.id) }
    }
}
