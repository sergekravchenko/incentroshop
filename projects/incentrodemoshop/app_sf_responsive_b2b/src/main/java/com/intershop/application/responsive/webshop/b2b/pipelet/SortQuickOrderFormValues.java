package com.intershop.application.responsive.webshop.b2b.pipelet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;

/**
 * Converts a HashMap with numerical string keys into a LinkedHashMap with the same elements inserted in the keys' numerical order.
 * Parameter <V>
 */
public class SortQuickOrderFormValues<V> extends Pipelet
{
    @Override
    public int execute(PipelineDictionary aPipelineDictionary) throws PipeletExecutionException
    {
        @SuppressWarnings("unchecked")
        HashMap<String, V> map = (HashMap<String, V> )aPipelineDictionary.get("Map");
        ArrayList<String> strList = new ArrayList<>();
        LinkedHashMap<String, V> sortedMap = new LinkedHashMap<>();
        
        // Sorts numerical strings increasingly
        Comparator<String> c = new Comparator<String>()
        {
            @Override
            public
            int compare(String s1, String s2)
            {
                int num1, num2;
                
                // If there are non-numerical strings, put them at the end
                try
                {
                    num1= Integer.parseInt(s1);
                }
                catch(NumberFormatException e)
                {
                    return -1;
                }
                
                try
                {
                    num2 = Integer.parseInt(s2);
                }
                catch(NumberFormatException e)
                {
                    return 1;
                }
                
                
                return num1 - num2;
            }
        };
        
        strList.addAll(map.keySet());
        
        strList.sort(c);
        
        for (String s: strList)
        {
            sortedMap.put(s, map.get(s));
        }

        aPipelineDictionary.put("Map", sortedMap);
        
        return PIPELET_NEXT;
    }

}
