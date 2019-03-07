package com.intershop.application.responsive.webshop.b2b.internal.basket;

import java.util.Objects;

import javax.inject.Inject;

import com.intershop.beehive.bts.internal.orderprocess.basket.BasketPO;
import com.intershop.beehive.core.capi.util.ObjectMapper;
import com.intershop.component.basket.capi.BasketBO;
import com.intershop.component.basket.capi.BasketBORepository;
import com.intershop.component.repository.capi.BusinessObjectRepositoryContext;
import com.intershop.component.repository.capi.BusinessObjectRepositoryContextProvider;

public class BasketPOToBasketBOObjectMapperImpl implements ObjectMapper<BasketPO, BasketBO>
{

    @Inject
    BusinessObjectRepositoryContextProvider businessObjectRepositoryContextProvider;
    
    @Override
    public BasketBO resolve(BasketPO basketPO)
    {
        Objects.requireNonNull(basketPO);
        
        BusinessObjectRepositoryContext businessObjectRepositoryContext = businessObjectRepositoryContextProvider.getBusinessObjectRepositoryContext();
        BasketBORepository basketBORepository = businessObjectRepositoryContext.getRepository(BasketBORepository.EXTENSION_ID);
        
        BasketBO basketBO = basketBORepository.getBasketBO(basketPO.getUUID());
        return basketBO;
    }
}
