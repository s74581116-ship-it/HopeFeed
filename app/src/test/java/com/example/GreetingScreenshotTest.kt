package com.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.DonorType
import com.example.data.model.FoodCategory
import com.example.data.model.ImpactStats
import com.example.ui.screens.FeedScreen
import com.example.ui.theme.HopeFeedTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      HopeFeedTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          FeedScreen(
            donations = emptyList(),
            impactStats = ImpactStats(1420, 580.0, 1450.0, 4, 8, 24),
            selectedDonorFilter = null,
            selectedCategoryFilter = FoodCategory.ALL,
            searchQuery = "",
            onDonorFilterChange = {},
            onCategoryFilterChange = {},
            onSearchQueryChange = {},
            onDonationClick = {},
            onAiMatchClick = {},
            onNavigateToDonate = {},
            onNavigateToMap = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
