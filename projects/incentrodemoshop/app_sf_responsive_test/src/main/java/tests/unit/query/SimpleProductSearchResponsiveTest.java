package tests.unit.query;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.intershop.component.foundation.capi.search.SearchExpression;
import com.intershop.component.foundation.capi.search.SearchExpressionFactory;
import com.intershop.dev.query.testsupport.QueryTest;

@Ignore
public class SimpleProductSearchResponsiveTest extends QueryTest
{

    @Test
    public void testSimpleProductSearch() throws Exception
    {

        Map<String, Object> data =  getJDBCTools().readFirst("select dp.domainid, l.localeid, cc.domainid as catalogCategory, ra.usergroupid, ra.usergroupdomainid, dpl.currency from derivedproduct dp, LOCALEINFORMATION l, catalogcategory cc, product_ra ra, derivedproductlistprice  dpl");

        Map<String, Object> productDomain = new HashMap<>();
        productDomain.put("UUID", String.valueOf(data.get("domainid")));

        Map<String, Object> localeInformation = new HashMap<>();
        localeInformation.put("LocaleID", String.valueOf(data.get("localeid")));

        SearchExpressionFactory factory = new SearchExpressionFactory();

        SearchExpression expression = factory.createSearchExpression(true);
        expression.addPhrase(factory.createExactPhrase("name"));

        Map<String, Object> catalogCategory = new HashMap<>();
        catalogCategory.put("DomainID", String.valueOf(data.get("catalogCategory")));

        Map<String, Object> filterGroup = new HashMap<>();
        filterGroup.put("DomainID",String.valueOf(data.get("usergroupid")) );
        filterGroup.put("ID",String.valueOf(data.get("usergroupdomainid")) );

        Map<String, Object> filters = new HashMap<>();
        filters.put("FilterGroup", filterGroup);

       getQueryExecutor().param("ProductDomain", productDomain)
       .param("LocaleInformation", localeInformation)
       .param("NameOrID", expression)
       .param("CatalogCategory", catalogCategory)
       .param("SortAttribute", "value")
       .param("SortAttributeType", "Integer")
       .param("SortOrder", "desc")
       .param("Filters", filters)
       .param("Currency", String.valueOf(data.get("currency"))).execute();
    }

    @Override
    protected String getQueryQualifiedName()
    {
        return "app_sf_responsive:product/SimpleProductSearch.query";
    }

}
