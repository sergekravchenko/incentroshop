package rest.com.intershop.b2x.specs.storefront.common

import org.spockframework.runtime.model.IterationInfo
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IGlobalExtension

class LoggingExtension implements IGlobalExtension {
    void start() {}
    void visitSpec(SpecInfo specInfo) {
        if (specInfo.getPackage().startsWith("rest"))
        {
            specInfo.addListener(new LoggingIterationListener())
        }
    }
    void stop() {}

    static class LoggingIterationListener extends AbstractRunListener  {

        /**
         * Prints the feature method name
         */
        @Override
        void beforeIteration(IterationInfo iteration) {
            println ""
            println "***************************************************************************************"
            println "* Feature method: " + iteration.name 
            println "***************************************************************************************"
        }
    }
}

