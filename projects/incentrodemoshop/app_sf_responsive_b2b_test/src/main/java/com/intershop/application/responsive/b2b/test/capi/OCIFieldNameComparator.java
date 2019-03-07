package com.intershop.application.responsive.b2b.test.capi;

import java.util.Comparator;

/**
 * comparator is used for sorting OCI basket data field names <br>
 * allows to display OCI fields in order based on their index
 */
public class OCIFieldNameComparator implements Comparator<String>
{
    // indicates any error we might have extracting the index part from the oci field name
    final int OCI_FIELD_NAME_INDEX_ERROR = Integer.MAX_VALUE;
    
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(String o1, String o2)
    {
        int compareResult = 0;
        
        int indexFromString1 = extractIndexFromString(o1);
        int indexFromString2 = extractIndexFromString(o2);
        
        if(indexFromString1 == indexFromString2)
        {
            compareResult = compareFieldNameString(o1, o2);
        }
        else
        {
            if(indexFromString1 < indexFromString2)
            {
                compareResult = -1;
            }
            else
            {
                if(indexFromString1 > indexFromString2)
                {
                    compareResult = 1;
                }
            }
        }
        return compareResult;
    }


    /**
     * the method implements a customized order for the OCI field names
     * @param o1 - String 1 to compare with
     * @param o2 - String 2 to compare with
     * @return - int [-1, 0, 1]
     */
    private int compareFieldNameString(String o1, String o2)
    {
        // TODO implement customized string sorting order here, if required
        return o1.compareTo(o2);
    }

    /**
     * OCI FieldNames match the pattern: "NEW_ITEM-&lt;Field name&gt;[&lt;index&gt;]" <br>
     * - e.g.: "NEW_ITEM-MATNR[&lt;index&gt;]" <br>
     * except for field 'longtext' the pattern is: "NEW_ITEM-LONGTEXT_&lt;index&gt;:132[]" <br>
     * 
     * @param ociFieldName - String to extract the index from 
     * @return int - the index value or an error value
     */
    private int extractIndexFromString(String ociFieldName)
    {
        final String fieldNamesCommonID = "NEW_ITEM";
        final String fieldNamesStartID = "[";
        final String fieldNamesEndID = "]";
        final String longTextStartID = fieldNamesCommonID+"-LONGTEXT_";
        final String longTextEndID = ":";
        String stringToBeParsedIntoAnInteger=null;
        String startID=null, endID=null;
        int returnIndex = OCI_FIELD_NAME_INDEX_ERROR;
        int posIndexStart = -1, posIndexEnd = -1;
        
        if(ociFieldName!=null)
        {
            if(ociFieldName.startsWith(fieldNamesCommonID))
            {
                // determine the start and end identifier string by current 'OCI Field Name'
                if(ociFieldName.startsWith(longTextStartID))
                {
                    startID = longTextStartID;
                    endID   = longTextEndID;
                }
                else
                {
                    startID = fieldNamesStartID;
                    endID   = fieldNamesEndID;
                }
            
                // extract substring and parse integer
                posIndexStart = ociFieldName.indexOf(startID);
                posIndexEnd = ociFieldName.indexOf(endID);
                if(posIndexStart > -1 && posIndexEnd > -1 && posIndexStart < posIndexEnd)
                {
                    stringToBeParsedIntoAnInteger = ociFieldName.substring(0,posIndexEnd);
                    stringToBeParsedIntoAnInteger = stringToBeParsedIntoAnInteger.substring(posIndexStart+startID.length());
                    try
                    {
                        returnIndex = Integer.parseInt(stringToBeParsedIntoAnInteger);
                    }
                    catch(NumberFormatException nfe)
                    {
                        returnIndex = OCI_FIELD_NAME_INDEX_ERROR;
                    }
                }
            }
        }
        return returnIndex;
    }
}
