package com.example.scoreup;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    //kiểm tra xem FrameLayout có được hiển thị hay không
    @Test
    public void testIsActivityInView() {
        Espresso.onView(withId(R.id.container_main)).check(matches(isDisplayed()));
    }

    //kiểm tra xem BottomNavigationView có được hiển thị hay không
    @Test
    public void testIsBottomNavInView() {
        Espresso.onView(withId(R.id.bottom_nav_main)).check(matches(isDisplayed()));
    }

    @Test
    public void testIsQuizQuestionDisplayed() {

        // Kiểm tra xem TextView hiển thị câu hỏi có được hiển thị hay không
        Espresso.onView(withId(R.id.question_textview)).check(matches(isDisplayed()));
    }

    //kiểm tra xem Button có được hiển thị hay không
    @Test
    public void testIsButtonInView() {
        Espresso.onView(withId(R.id.btn_Finish)).check(matches(isDisplayed()));
    }

}