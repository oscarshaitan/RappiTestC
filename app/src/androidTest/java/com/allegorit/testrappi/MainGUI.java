package com.allegorit.testrappi;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)

public class MainGUI {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void FabExist() {
        onView(withId(R.id.Fab));
    }

    @Test
    public void FabClick(){
        onView(withId(R.id.Fab)).perform(click());
    }

    @Test
    public void TopBarExist(){
        onView(withId(R.id.navigation));
    }

    @Test
    public void TopBarPopularExist(){
        onView(withId(R.id.navigation_popular));
    }
    @Test
    public void TopBarTopRatedExist(){
        onView(withId(R.id.navigation_top_rated));
    }
    @Test
    public void TopBarUpcommingExist(){
        onView(withId(R.id.navigation_upcomming));
    }

    @Test
    public void TopBarPopularClick(){
        onView(withId(R.id.navigation_popular)).perform(click());
    }

    @Test
    public void TopBarTopRatedClick(){
        onView(withId(R.id.navigation_top_rated)).perform(click());
    }

    @Test
    public void TopBarUpcomingClick(){
        onView(withId(R.id.navigation_upcomming)).perform(click());
    }

    @Test
    public void MenuExist(){
        onView(withId(R.id.action_bar));
    }

    @Test
    public void MenuClick(){
        onView(withId(R.id.action_bar)).perform(click());
    }


    @Test
    public void RecyclerExist(){
        onView(withId(R.id.gridR));
    }

    @Test
    public void RecyclerScroll(){
        onView(withId(R.id.gridR)).perform(swipeUp());
        onView(withId(R.id.gridR)).perform(swipeDown());
        onView(withId(R.id.gridR)).perform(swipeUp());
        onView(withId(R.id.gridR)).perform(swipeDown());
    }

    @Test
    public void RecyclerVenomMovie(){
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("Venom"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("Rate:"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("6.60"))));
    }

    @Test
    public void RecyclerVenomMovieClick(){
        onView(withText("Venom")).perform(click());
    }

    //works alone ...?¿
    @Test
    public void ChangeListToTopRated(){
        onView(withId(R.id.navigation_top_rated)).perform(click());
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("The Godfather"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("Rate:"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("8.60"))));
    }

    @Test
    public void ChangeListToUpcomming(){
        TopBarUpcomingClick();
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("A-X-L"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("Rate:"))));
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("5.30"))));
    }

    @Test
    public void SearchByCategoryAction(){
        FabClick();
        onView(withText("Category")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("Action")).perform(click());
        onView(withText("SEARCH")).perform(click());
    }

    @Test
    public void SearchOnlineBatman(){
        FabClick();
        onView(withText("Keyword (Online only)")).perform(click());
        onView(withText("OK")).perform(click());

        onView(withId(android.R.id.input)).perform(typeText("Batman"));
        onView(withText("SEARCH")).perform(click());
        onView(withId(R.id.gridR))
                .check(matches(hasDescendant(withText("Batman"))));
    }

    @Test
    public void VenomDetail(){
        RecyclerVenomMovieClick();
        onView(withId(R.id.title)).check(matches(withText("Venom")));
        onView(withId(R.id.year)).check(matches(withText("2018")));
    }



    @Test
    public void MenuSeriesExist(){
        onView(allOf(withId(R.id.title), withText("Series"), childAtPosition(childAtPosition(withId(R.id.content), 0), 0), isDisplayed()));
    }

    @Test
    public void MenuSeriesClick(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Series"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());
    }

    @Test
    public void UpcommingNotExistOnSeries(){
        MenuSeriesClick();
        onView(withId(R.id.navigation_upcomming)).check(matches(not(isDisplayed())));
    }



    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }






}
