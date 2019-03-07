package geb.com.intershop.b2x.model.storefront.responsive.demo

import geb.com.intershop.b2x.model.storefront.responsive.Budget
import geb.com.intershop.b2x.model.storefront.responsive.CostCenter
import geb.com.intershop.b2x.model.storefront.responsive.Budget.BudgetPeriod

/**
 * ENUM of demo budget used in B2B inSpired Showcase
 */
enum DemoBudget
{
    MONHTLY_2500_USD(new BigDecimal("2500.00"), BudgetPeriod.MONTHLY),
    MONHTLY_5000_USD(new BigDecimal("5000.00"), BudgetPeriod.MONTHLY),
    MONHTLY_10000_USD(new BigDecimal("10000.00"), BudgetPeriod.MONTHLY),
    WEEKLY_1000_USD(new BigDecimal("1000.00"), BudgetPeriod.WEEKLY)
 
    final Budget budget
 
    private DemoBudget(BigDecimal amount, BudgetPeriod period) {
        this.budget = new Budget(amount, period);
    }
 
    Budget getBudget() {
       budget
    }
}





