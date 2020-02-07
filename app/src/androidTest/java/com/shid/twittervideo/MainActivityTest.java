package com.shid.twittervideo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.MenuItem;

import com.shid.twittervideo.views.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
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
