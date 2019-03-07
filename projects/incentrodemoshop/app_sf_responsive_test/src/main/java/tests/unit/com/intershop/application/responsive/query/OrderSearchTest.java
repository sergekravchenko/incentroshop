package tests.unit.com.intershop.application.responsive.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.intershop.beehive.core.internal.query.definition.QueryTemplate;
import com.intershop.component.foundation.capi.search.SearchExpression;
import com.intershop.component.foundation.capi.search.SearchExpressionParser;
import com.intershop.dev.query.testsupport.QueryTest;

/**
 * Test for query "order/OrderSearch".
 */
public class OrderSearchTest extends QueryTest
{

    private String domainID, domainID2;
    
    @Override
    protected String getQueryQualifiedName()
    {
        return "app_sf_responsive:order/OrderSearch.query";
    }

    @Before
    public void init() throws Exception
    {
        domainID = (String)getJDBCTools().getColumnValue("DomainInformation", "DomainID", "DomainName", "eTest");
        domainID2 = (String)getJDBCTools().getColumnValue("DomainInformation", "DomainID", "DomainName", "root");
        
        createOrder("Order_1",          "UUID_1",   domainID,   "User_1", "Customer_1", 1, 0, 1.00,    "Mesut2", "Özil1");
        createOrder("Order_2",          "UUID_2",   domainID,   "User_1", "Customer_1", 1, 20, 5.00,    "Mesut1", "Özil2");
        createOrder("Order_3",          "UUID_3",   domainID,   "User_1", "Customer_1", 2, 40, 8.00,    "Mesut3", "Özil1");
        createOrder("Order_OtherDomain", "UUID_4",  domainID2,  "User_1", "Customer_1", 2, 60, 16.00,   "Mesut1", "Özil1");
        createOrder("Order_OtherUser",  "UUID_6",   domainID,   "User_2", "Customer_1", 3, 80, 100.00,  "Mesut4", "Özil2");
        createOrder("Order_OtherOrderType", "UUID_7", domainID, "User_1", "Customer_1", 3, 100, 1234.00, "Mesut1", "Özil3");
        createOrder("Order_OtherCustomer", "UUID_8", domainID,  "User_1", "Customer_2", 4, 120, 12345.00,"Mesut5", "Özil4");
        
        // set initial data, that is needed for all queries
        setDomain(domainID);
        setBuyer("User_1");
    }
    
    @After
    public void cleanUp() throws Exception
    {
        getJDBCTools().cleanUp();
    }
    
    @Test
    public void testOrderSearch() throws Exception
    {
        getQueryExecutor().param("SortAttribute", "OrderTotal").param("SortDirection", "desc");
        getQueryExecutor().param("SearchTerm", createSearchExpression("?esu*"));
        getQueryExecutor().param("ExcludedStatusCodes", Arrays.asList(2,3));
        testQuery(Arrays.asList("UUID_8", "UUID_2", "UUID_1"));
    }
    
    @Test
    public void testOrderSearch_ByDomain() throws Exception
    {
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
        setDomain(domainID2);
        testQuery(Arrays.asList("UUID_4"));
    }
    
    @Test
    public void testOrderSearch_ByCustomer() throws Exception
    {
        setCustomer("Customer_1");
        testQuery(Arrays.asList("UUID_7", "UUID_6", "UUID_3", "UUID_2", "UUID_1"));
        setCustomer("Customer_2");
        testQuery(Arrays.asList("UUID_8"));
    }
    
    @Test
    public void testOrderSearch_ByBuyer() throws Exception
    {
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
        setBuyer("User_2");
        testQuery(Arrays.asList("UUID_6"));
    }
    
    @Test
    public void testOrderSearch_CreationDate_asc() throws Exception
    {
        // sorting by creation date is the default, therefore no "SortAttribute" attribute needed
        getQueryExecutor().param("SortDirection", "asc");
        testQuery(Arrays.asList("UUID_1", "UUID_2", "UUID_3", "UUID_7", "UUID_8"));
    }
    
    @Test
    public void testOrderSearch_CreationDate_desc() throws Exception
    {
        // sorting by creation date is the default, therefore no "SortAttribute" attribute needed
        // sorting desc per default, therefore no "SortDirection" attribute needed
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
    }
    
    @Test
    public void testOrderSearch_GrandTotal_asc() throws Exception
    {
        getQueryExecutor().param("SortAttribute", "OrderTotal").param("SortDirection", "asc");
        testQuery( Arrays.asList("UUID_1", "UUID_2", "UUID_3", "UUID_7", "UUID_8"));
    }
    
    @Test
    public void testOrderSearch_GrandTotal_desc() throws Exception
    {
        getQueryExecutor().param("SortAttribute", "OrderTotal").param("SortDirection", "desc");
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
    }
 
    @Test
    public void testOrderSearch_PurchaserName_desc() throws Exception
    {
        getQueryExecutor().param("SortAttribute", "PurchaserName").param("SortDirection", "desc");
        testQuery(Arrays.asList("UUID_8", "UUID_3", "UUID_1", "UUID_7", "UUID_2"));
    }
    
    @Test
    public void testOrderSearch_PurchaserName_asc() throws Exception
    {
        getQueryExecutor().param("SortAttribute", "PurchaserName").param("SortDirection", "asc");
        testQuery(Arrays.asList("UUID_2", "UUID_7", "UUID_1", "UUID_3", "UUID_8"));
    }
    
    @Test
    public void testOrderSearch_Limit() throws Exception
    {
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
        getQueryExecutor().param("Limit", 4); 
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2"));
        getQueryExecutor().param("Limit", 3);    
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3"));
        getQueryExecutor().param("Limit", 1);
        testQuery(Arrays.asList("UUID_8"));
    }
    
    @Test
    public void testOrderSearch_SearchTerm_BuyerFirstName() throws Exception
    {
        getQueryExecutor().param("SearchTerm", createSearchExpression("Mesut1"));
        testQuery(Arrays.asList("UUID_7", "UUID_2"));
        
        getQueryExecutor().param("SearchTerm", createSearchExpression("Mes*"));
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
    }
    
    @Test
    public void testOrderSearch_SearchTerm_BuyerLastName() throws Exception
    {
        getQueryExecutor().param("SearchTerm", createSearchExpression("Özil1"));
        testQuery(Arrays.asList("UUID_3", "UUID_1"));
        
        getQueryExecutor().param("SearchTerm", createSearchExpression("Öz*"));
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
    }
    
    
    @Test
    public void testOrderSearch_SearchTerm_DocumentNo() throws Exception
    {
        getQueryExecutor().param("SearchTerm", createSearchExpression("Order_1"));
        testQuery(Arrays.asList("UUID_1"));
        
        getQueryExecutor().param("SearchTerm", createSearchExpression("Order*"));
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3", "UUID_2", "UUID_1"));
        
        getQueryExecutor().param("SearchTerm", createSearchExpression("Order_?"));
        testQuery(Arrays.asList("UUID_3", "UUID_2", "UUID_1"));
    }
    
    @Test
    public void testOrderSearch_IncludedStatusCodes() throws Exception
    {
        getQueryExecutor().param("IncludedStatusCodes", Arrays.asList(1));
        testQuery(Arrays.asList("UUID_2", "UUID_1"));
        
        getQueryExecutor().param("IncludedStatusCodes", Arrays.asList(1,2,3));
        testQuery(Arrays.asList("UUID_7", "UUID_3", "UUID_2", "UUID_1"));
    }
    
    @Test
    public void testOrderSearch_ExcludedStatusCodes() throws Exception
    {
        getQueryExecutor().param("ExcludedStatusCodes", Arrays.asList(1));
        testQuery(Arrays.asList("UUID_8", "UUID_7", "UUID_3"));
        
        getQueryExecutor().param("ExcludedStatusCodes", Arrays.asList(1,2,3));
        testQuery(Arrays.asList("UUID_8"));
    }
    
    
    private SearchExpression createSearchExpression(String searchTerm) throws Exception
    { 
        SearchExpressionParser parser = new SearchExpressionParser();
        return parser.parse(searchTerm);
    }
    
    private void createOrder(String documentNo, String uuid, String domainID, String userID, String customerID, int status, 
                    int offset, double grandTotal, String firstName, String lastName) throws Exception
    {
        getJDBCTools().insertInto("ISORDER", true, "UUID", uuid, "DomainID", domainID, "UserID", userID, "CustomerID", customerID,  
                        "DocumentNo", documentNo, "Status", status, "creationDate", new Timestamp(System.currentTimeMillis() + offset), "grandtotalgrosspricepcvalue", grandTotal,
                        "buyerfirstname", firstName, "buyerlastname", lastName);
    }
    
    private void setDomain(String domainID)
    {
        Map<String, Object> domain = new HashMap<>();
        domain.put("UUID", domainID);
        getQueryExecutor().param("OrderDomain", domain);
    }
    
    private void setBuyer(String buyerID)
    {
        Map<String, Object> buyer = new HashMap<>();
        buyer.put("ID", buyerID);
        getQueryExecutor().param("Buyer", buyer);
    }
    
    private void setCustomer(String customerID)
    {
        Map<String, Object> customer = new HashMap<>();
        customer.put("ID", customerID);
        getQueryExecutor().param("Customer", customer);
    }
    
    
    private void testQuery(List<String> expectedUUIDs) throws Exception
    {        
        getQueryExecutor().templateType(QueryTemplate.TYPE_OBJECTS);

        try (ResultSet resultSet = getQueryExecutor().execute())
        {
            List<String> actualUUIDs = new ArrayList<>();
            while (resultSet.next())
            {
                actualUUIDs.add(resultSet.getString("OrderUUID"));
            }
            assertThat(actualUUIDs, is(expectedUUIDs));
        }

        // count and countedobjects query does not consider limit
        if (!getQueryExecutor().getParams().containsKey("Limit"))
        {
            getQueryExecutor().templateType(QueryTemplate.TYPE_COUNT);       
            try (ResultSet resultSet = getQueryExecutor().execute())
            {
                //only one result, and the data of that is the count
                resultSet.next();
                assertThat(resultSet.getInt("count"), is(expectedUUIDs.size()));
            }

            getQueryExecutor().templateType(QueryTemplate.TYPE_COUNTEDOBJECTS);       
            try (ResultSet resultSet = getQueryExecutor().execute())
            {
                List<String> actualUUIDs = new ArrayList<>();
                while (resultSet.next())
                {
                    actualUUIDs.add(resultSet.getString("OrderUUID"));
                }    
                assertThat(actualUUIDs, is(expectedUUIDs));
            }
        }         
    }
}
