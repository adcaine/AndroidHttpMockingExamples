package com.handstandsam.httpmocking.tests.mockwebserver;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handstandsam.httpmocking.util.AssetReaderUtil;
import com.joshskeen.weatherview.BuildConfig;
import com.joshskeen.weatherview.MainActivity;
import com.joshskeen.weatherview.R;
import com.joshskeen.weatherview.service.WeatherServiceManager;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class MockWebServerEnqueueTest {

    Logger logger = LoggerFactory.getLogger(MockWebServerEnqueueTest.class);

    final MockWebServer mMockWebServer = new MockWebServer();

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = activityRule.getActivity();
        mMockWebServer.play(BuildConfig.PORT);
        //script MockWebServer to return this JSON
        String assetJson = AssetReaderUtil.asset(activity, "atlanta-conditions.json");
        mMockWebServer.enqueue(new MockResponse().setBody(assetJson));

        String okhttpMockWebServerUrl = mMockWebServer.getUrl("/").toString();
        logger.debug("okhttp mockserver URL: " + okhttpMockWebServerUrl);

        String serviceEndpoint = "http://127.0.0.1:" + BuildConfig.PORT;
        logger.debug("MockWebServer Endpoint: " + serviceEndpoint);
        activity.setWeatherServiceManager(new WeatherServiceManager(serviceEndpoint));

    }

    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    /**
     * Test okhttp mockwebserver Enqueue
     */
    @Test
    public void testMockWebServerEnqueue() {
        activity = activityRule.getActivity();
        logger.debug("testMockWebServerEnqueue");

        onView(ViewMatchers.withId(R.id.editText)).perform(typeText("atlanta"));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.textView)).check(matches(withText(containsString("GA"))));
    }
}
