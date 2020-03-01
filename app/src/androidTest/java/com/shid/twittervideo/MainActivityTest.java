package com.shid.twittervideo;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.shid.twittervideo.views.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public static final String LINK = "Anupam";

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.shid.twittervideo", appContext.getPackageName());
    }

    @Test
    public void optionClick() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView((withText("About Twitter Video"))).perform(click());
        onView((withId(R.id.second_activity))).check(matches(isDisplayed()));

    }

    @Test
    public void clickBtnDlFalse(){
        onView(withId(R.id.txt_tweet_url)).perform(typeText(LINK));
        onView(withId(R.id.btn_download)).perform(click());
        onView(withText(R.string.toast_url)).inRoot(withDecorView(not(mainActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
