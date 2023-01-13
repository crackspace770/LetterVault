package com.google.developers.lettervault.ui.list

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.developers.lettervault.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListActivityTest{

    @Before
    fun setUp(){
        ActivityScenario.launch(ListActivity::class.java)
    }

    @Test
    fun openAdd(){
        onView(withId(R.id.rv_letters))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
            .perform(click())
        onView(withId(R.id.edSubject))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnClose))
            .check(matches(isDisplayed()))
        onView(withId(R.id.edContent))
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_save))
            .check(matches(isDisplayed()))
        onView(withId(R.id.action_time))
            .check(matches(isDisplayed()))
    }


}