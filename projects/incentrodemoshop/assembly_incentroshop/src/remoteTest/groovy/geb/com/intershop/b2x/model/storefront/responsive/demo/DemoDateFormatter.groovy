package geb.com.intershop.b2x.model.storefront.responsive.demo

import java.time.format.DateTimeFormatter

/**
 * ENUM of demo date formats used for testing in B2B inSPIRED
 */

enum DemoDateFormatter
{
    SHORT_DATE("MM/dd/yy",Locale.US),
    DEFAULT_DATE("MMM-dd-yyyy",Locale.US);
    
    final DateTimeFormatter dateTimeFormatter;
 
    private DemoDateFormatter(String format,Locale locale)
    {
        dateTimeFormatter=DateTimeFormatter.ofPattern(format,locale);
    }
    
    DateTimeFormatter getFormat(){
        dateTimeFormatter;
    }
}