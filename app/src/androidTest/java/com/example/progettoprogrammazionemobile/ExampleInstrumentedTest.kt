package com.example.progettoprogrammazionemobile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var login: ActivityScenarioRule<Login> = ActivityScenarioRule(Login::class.java)

    @Test
    fun loginClick() {
        val email = "test1@gmail.com"
        val password = "123456"

        onView(withId(R.id.registrationEmail)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard())

        onView(withId(R.id.loginbutton)).perform((click()))
    }


    @Test
    fun useAppContext() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.progettoprogrammazionemobile", appContext.packageName)
    }
}