package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

class RemoveCostCenterConfirmationPage extends CostCenterConfirmationPage
{
    RemoveCostCenterConfirmationPage()
    {
        action = "remove"
        destination = CostCentersPage
    }
}
