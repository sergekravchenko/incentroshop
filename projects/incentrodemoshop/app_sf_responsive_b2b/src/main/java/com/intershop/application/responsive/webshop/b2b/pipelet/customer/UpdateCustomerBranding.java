package com.intershop.application.responsive.webshop.b2b.pipelet.customer;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.component.b2b.capi.customer.CustomerBOBrandingExtension;
import com.intershop.component.customer.capi.CustomerBO;

public class UpdateCustomerBranding extends Pipelet
{
    public static final String DN_CUSTOMER_BO = "CustomerBO";
    public static final String DN_BRANDING_IMAGE = "BrandingImage";

    public int execute(PipelineDictionary dict) throws PipeletExecutionException
    {
        // lookup 'CustomerBO' in pipeline dictionary
        CustomerBO customer = dict.getRequired(DN_CUSTOMER_BO);

        CustomerBOBrandingExtension extension = customer.getExtension(CustomerBOBrandingExtension.class);

        // lookup 'BrandingImage' in pipeline dictionary
        String brandingImage = dict.getOptional(DN_BRANDING_IMAGE);

        // if null the attribute will be removed
        extension.setBrandingImage(brandingImage);

        return PIPELET_NEXT;
    }

}
