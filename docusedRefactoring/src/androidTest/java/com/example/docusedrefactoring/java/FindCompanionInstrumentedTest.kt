package com.example.docusedrefactoring.java

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.docusedrefactoring.MainActivity
import com.example.docusedrefactoring.testhooks.IdlingEntity
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.greenrobot.eventbus.EventBus
import com.example.docusedrefactoring.R
import org.greenrobot.eventbus.Subscribe
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before
import org.junit.BeforeClass

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4::class)
class FindCompanionInstrumentedTest {

  lateinit var testScenario: ActivityScenario<MainActivity>

  private val idlingResource = SimpleIdlingResource()

  companion object {
    // 1
    val server = MockWebServer()
    // 2
    val dispatcher: Dispatcher = object : Dispatcher() {
      @Throws(InterruptedException::class)
      override fun dispatch(request: RecordedRequest): MockResponse {
        return CommonTestDataUtil.dispatch(request) ?: MockResponse().setResponseCode(
            404
        )
      }
    }

    lateinit var startIntent: Intent

    @BeforeClass
    @JvmStatic
    fun setup() {
// 3
      server.setDispatcher(dispatcher)
      server.start()
// 3
      startIntent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
      startIntent.putExtra(MainActivity.PETFINDER_URI,
          server.url("").toString())
    }
  }

  @Subscribe
  fun onEvent(idlingEntity: IdlingEntity) {
    idlingResource.incrementBy(idlingEntity.incrementValue)
  }

  @Before
  fun beforeTestsRun() {
    testScenario = ActivityScenario.launch(startIntent)
    EventBus.getDefault().register(this)
    IdlingRegistry.getInstance().register(idlingResource)
  }

  @After
  fun afterTestsRun() {
    // eventbus and idling resources unregister.
    IdlingRegistry.getInstance().unregister(idlingResource)
    EventBus.getDefault().unregister(this)
    testScenario.close()
  }

  @Test
  fun pressing_the_find_bottom_menu_item_takes_the_user_to_the_find_page() {
    onView(withId(R.id.searchForCompanionFragment)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withId(R.id.searchFieldText)).check(matches(isDisplayed()))
  }

  @Test
  fun searching_for_a_companion_and_tapping_on_it_takes_the_user_to_the_companion_details() {
    find_and_select_kevin_in_30318()
    onView(withText("Rome, GA")).check(matches(isDisplayed()))

    onView(withText("Domestic Short Hair")).check(matches(isDisplayed()))
            onView(withText("Young")).check(matches(isDisplayed()))
            onView(withText("Female")).check(matches(isDisplayed()))
            onView(withText("Medium")).check(matches(isDisplayed()))
            onView(withText("Meet KEVIN")).check(matches(isDisplayed()))
  }

  @Test
  fun verify_that_companion_details_shows_a_valid_phone_number_and_email() {
    find_and_select_kevin_in_30318()
    onView(withText("(706) 236-4537")).check(matches(isDisplayed()))
    onView(withText("adoptions@gahomelesspets.com")).check(matches(isDisplayed()))
  }

  private fun find_and_select_kevin_in_30318(){
    onView(withId(R.id.searchForCompanionFragment)).perform(click())
    onView(withId(R.id.searchFieldText)).perform(typeText("30318"))
    onView(withId(R.id.searchButton)).perform(click())
    onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
    onView(withText("KEVIN")).perform(click())
  }
}
