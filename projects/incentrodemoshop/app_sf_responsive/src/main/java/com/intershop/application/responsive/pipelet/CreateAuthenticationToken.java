package com.intershop.application.responsive.pipelet;

import com.intershop.beehive.core.capi.pipeline.Pipelet;
import com.intershop.beehive.core.capi.pipeline.PipeletExecutionException;
import com.intershop.beehive.core.capi.pipeline.PipelineDictionary;
import com.intershop.beehive.core.capi.user.User;
import com.intershop.component.rest.capi.auth.Token;
import com.intershop.component.rest.internal.auth.TokenImpl;


/**
 * Creates a token that can be used for authentication.
 */
public class CreateAuthenticationToken extends Pipelet
{

    @Override
    public int execute(PipelineDictionary aPipelineDictionary) throws PipeletExecutionException
    {
        User user = aPipelineDictionary.getRequired("User");
        Token token = new TokenImpl(user);
        aPipelineDictionary.put("Token", token);
        return PIPELET_NEXT;
    }

}
