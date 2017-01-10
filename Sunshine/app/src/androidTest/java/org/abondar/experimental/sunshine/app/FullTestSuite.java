package org.abondar.experimental.sunshine.app;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by abondar on 1/9/17.
 */
public class FullTestSuite  extends TestSuite{
    public static Test suite(){
        return new TestSuiteBuilder(FullTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }
}
