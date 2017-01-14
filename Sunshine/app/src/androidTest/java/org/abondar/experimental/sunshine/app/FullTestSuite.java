package org.abondar.experimental.sunshine.app;

import junit.framework.TestSuite;
import org.abondar.experimental.sunshine.app.data.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by abondar on 1/9/17.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class, TestFetchWeatherTask.class,
        TestProvider.class, TestUriMatcher.class, TestWeatherContract.class})
public class FullTestSuite  extends TestSuite{


    public FullTestSuite(){
        super();
    }
}
