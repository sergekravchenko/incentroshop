package com.intershop.application.responsive.webshop.b2b.pipelet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.component.mvc.capi.alert.Alert;
import com.intershop.component.mvc.capi.alert.AlertMgr;
/**
 * Determines all alerts for a user.
 */
public class GetBackInStockNotifications extends Pipelet
{
    @Inject
    private AlertMgr alertMgr;

    /**
     * Constant used to access the pipeline dictionary with key 'AlertUser'
     */
    public static final String DN_ALERT_USER = "AlertUser";

    /**
     * Constant used to access the pipeline dictionary with key 'UserAlerts'
     * Iterator of alert for user.
     */
    public static final String DN_USER_ALERTS_ITERATOR = "UserAlerts";

    /**
    * The pipelet's execute method is called whenever the pipelets gets
    * executed in the context of a pipeline and a request. The pipeline
    * dictionary valid for the currently executing thread is provided as
    * a parameter.
    *  
    * @param   dict The pipeline dictionary to be used.
    * @throws  PipeletExecutionException
    *          Thrown in case of severe errors that make the pipelet execute
    *          impossible (e.g. missing required input data).
    */
    public int execute(PipelineDictionary dict)
        throws PipeletExecutionException
    {
        // lookup 'AlertUser' in pipeline dictionary
        User alertUser = dict.getRequired(DN_ALERT_USER);
        
        // get iterator of alerts and put result into dictionary   
        @SuppressWarnings("unchecked")
        Iterator<Alert> alerts = alertMgr.getAlerts(alertUser);
        
        List<Alert> result = new ArrayList<Alert>();

        // filter back in stock notifications
        while( alerts.hasNext() )
        {

            Alert alert = alerts.next();
            if( alert.getHandlerClassName().equalsIgnoreCase("com.intershop.component.mvc.capi.alert.StockConditionHandler") )
            {
                result.add(alert);
            }
        }
        
        dict.put(DN_USER_ALERTS_ITERATOR, result.iterator());
        
        // done.
        return PIPELET_NEXT;                  


    }
}
