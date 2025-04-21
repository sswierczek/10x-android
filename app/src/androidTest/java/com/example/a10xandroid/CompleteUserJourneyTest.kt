package com.example.a10xandroid

import android.util.Log
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.a10xandroid.data.auth.AuthRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

private const val WAIT_TIME_MS = 30_000L

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CompleteUserJourneyTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        hiltRule.inject()
        composeRule.mainClock.autoAdvance = true
    }

    @Test
    fun testCompleteUserFlow() = runTest {
        // Auth test user
        login()

        // Recommend movie
        waitAndClickOn(tag = "recommendbutton")

        // Add recommendation
        waitAndClickOn(tag = "addtojourrnal")

        // Check it was added
        waitAndClickOn(tag = "addedtojournal")

        back()

        // Add movie
        waitAndClickOn(tag = "addmoviebutton")

        // Search movie
        composeRule.onNodeWithTag("searchinput").performTextInput("Interstellar")

        // Add movie
        waitAndClickOn(tag = "addtojourrnal")

        // Confirm
        waitAndClickOn(tag = "confirm_addtojournal")

        // Check is added
        composeRule.waitUntil(timeoutMillis = WAIT_TIME_MS) {
            composeRule.onAllNodesWithText("Movie added to journal").fetchSemanticsNodes()
                .isNotEmpty()
        }

        back()

        // Open profile
        waitAndClickOn(tag = "userprofile")

        // Logout
        waitAndClickOn(tag = "logout")

        // Check is on login screen
        composeRule.waitUntil(timeoutMillis = WAIT_TIME_MS) {
            composeRule.onAllNodesWithText("Welcome back").fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun login() {
        composeRule.onNodeWithText("Email").performTextInput("testuser@testuser.pl")
        composeRule.onNodeWithText("Password").performTextInput(BuildConfig.TESTUSER_PASSWORD)
        composeRule.onNodeWithText("Login").performClick()

        composeRule.waitUntil(WAIT_TIME_MS) {
            composeRule.onAllNodesWithText("Journal")
                .fetchSemanticsNodes().size == 1
        }
    }

    private fun back() {
        composeRule.activityRule.scenario.onActivity {
            it.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun waitAndClickOn(tag: String) {
        composeRule.waitUntil(
            timeoutMillis = 30_000
        ) {
            composeRule.onAllNodes(hasTestTagThatContains(tag), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(
            hasTestTagThatContains(tag),
            useUnmergedTree = true
        )[0].performClick()
    }

    private fun hasTestTagThatContains(partialTag: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag containing \"$partialTag\"") { node ->
            val tag = node.config.getOrNull(SemanticsProperties.TestTag)
            tag?.contains(partialTag) == true
        }
    }
}
