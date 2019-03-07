package geb.com.intershop.b2x.pages.storefront.responsive.costcenter

class DeactivateCostCenterConfirmationPage extends CostCenterConfirmationPage
{
    DeactivateCostCenterConfirmationPage()
    {
        action = "deactivate"
        destination = CostCentersPage
    }
}
