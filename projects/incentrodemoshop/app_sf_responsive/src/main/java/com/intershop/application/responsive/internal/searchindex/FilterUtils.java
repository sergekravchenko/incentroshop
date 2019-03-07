package com.intershop.application.responsive.internal.searchindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.intershop.beehive.xcs.capi.catalog.CatalogCategory;
import com.intershop.component.search.capi.Attribute;
import com.intershop.component.search.capi.CategoryConfiguration;
import com.intershop.component.search.capi.FilterAttribute;
import com.intershop.component.search.capi.FilterAttributeEntry;
import com.intershop.component.search.capi.SearchIndexConfiguration;
import com.intershop.component.search.capi.SearchIndexQuery;
import com.intershop.component.search.capi.dataprovider.JavaBeanExtractorUtil;

public enum FilterUtils
{
    INSTANCE;
    
    /**
     * returns a query with removed attribute conditions if these conditions are based upon attributes that have 
     * a predecessor filter option configuration.
     * if there are no such predecessor filter configurations the original query is returned.
     *  
     * @param originalQuery the original query
     * @param indexConfig the index configuration to check for predecessor options
     * @param attributeName the attribute name to check if there is a predecessor filter condition to this attribute name
     * @return query with potential removed attribute conditions
     */
    public SearchIndexQuery getRemovedDescendantFilterConditionsQuery(SearchIndexQuery originalQuery, SearchIndexConfiguration indexConfig, String attributeName)
    {
        Map<String, List<String>> descendantAttributeNamesMap = getDescendantAttributeNamesMap(indexConfig);
        if(originalQuery.getCondition(attributeName) == null)
        {
            return removeDescendantConditionFromQuery(originalQuery, descendantAttributeNamesMap, attributeName);
        }
        else
        {
            return originalQuery; 
        }
         
    }
    
    /**
     * removes condition values from the given query that are child categories of the given removedCategoryUUID
     *   
     * @param originalQuery
     * @param filter
     * @param removedCategoryUUID
     * @return a modified query with removed condition values or the original query 
     */
    public SearchIndexQuery getRemovedSelectedChildCategoriesQuery(SearchIndexQuery originalQuery, FilterAttribute filter, String removedCategoryUUID)
    {
        SearchIndexQuery removedSelectedChildCategoriesQuery = originalQuery;
        Iterator<FilterAttributeEntry> entryIt = filter.createFilterAttributeEntryIterator();
        while(entryIt.hasNext())
        {
            FilterAttributeEntry entry = entryIt.next(); 
            
            if(!entry.isSelected())
            {
                continue; 
            }
            if(entry.getValue().equals(removedCategoryUUID))
            {
                continue; 
            }
            else
            {
                CatalogCategory entryCategory = getCategoryFromEntry(entry); 
                if(entryCategory == null)
                {
                    continue; 
                }
                    
                CatalogCategory parent = entryCategory.getParent();  
                while(parent != null)
                {
                    if(parent.getUUID().equals(removedCategoryUUID))
                    {
                        removedSelectedChildCategoriesQuery = removedSelectedChildCategoriesQuery.getRemovedValueCondition(entry.getName(), entryCategory.getUUID());
                    }
                    parent = parent.getParent(); 
                }
            }
        }
        return removedSelectedChildCategoriesQuery; 
    }
    
    public SearchIndexQuery getRemovedCategoryFilterConditionsQuery(SearchIndexQuery originalQuery, SearchIndexConfiguration configuration, CatalogCategory category, boolean isSearch)
    {
        
        SearchIndexQuery removedCategoryFilterConditionsQuery = originalQuery;
        
        List<String> categorySpecFilters = originalQuery.getFilterGroups();
        
        CategoryConfiguration categoryFilterOptionConfig = new CategoryConfiguration();
        categoryFilterOptionConfig.setCategoryName(category.getName());
        categoryFilterOptionConfig.setDomain(category.getDomain().getDomainName());
        
        for(String categorySpecFilter:categorySpecFilters)
        {
            Attribute att = configuration.getAttributeByName(categorySpecFilter);
            
            if(att == null) continue; 
            if(!att.isCategoryFilter()) continue; 
            if(att.getFilterOptions() == null) continue; 
            
            if(att.getFilterOptions().containsCategory(categoryFilterOptionConfig))
            {
                if(category.getParent() != null)
                {
                    CategoryConfiguration parentCategoryFilterOptionConfig = new CategoryConfiguration();
                    parentCategoryFilterOptionConfig.setCategoryName(category.getParent().getName());
                    parentCategoryFilterOptionConfig.setDomain(category.getParent().getDomain().getDomainName());
                    if(!att.getFilterOptions().containsCategory(parentCategoryFilterOptionConfig))
                    {
                        removedCategoryFilterConditionsQuery = removedCategoryFilterConditionsQuery.getRemovedCondition(categorySpecFilter); 
                    }
                    
                }
                else
                {
                    if(isSearch)
                    {
                        removedCategoryFilterConditionsQuery = removedCategoryFilterConditionsQuery.getRemovedCondition(categorySpecFilter); 
                    }
                }
            }
            else
            {
                removedCategoryFilterConditionsQuery = removedCategoryFilterConditionsQuery.getRemovedCondition(categorySpecFilter);
            }
        }
        
        return removedCategoryFilterConditionsQuery; 
        
    }
    
    /**
     * checks if a category filter item given by the category of this item 
     * should be displayed for the current query conditions (selected filter values) and specified category 
     * specific filters that are set at filter groups of the query
     * 
     * @param query the current search index query
     * @param configuration the current index configuration 
     * @param category the category of the filter item
     * @return true if the filter item should be shown. 
     */
    public boolean isCategoryFilterItemVisibleForSelectedFilters(SearchIndexQuery query, SearchIndexConfiguration configuration, CatalogCategory category)
    {
        if(category == null) return true; 
        
        List<String> categorySpecFilters = query.getFilterGroups();
        
        CategoryConfiguration categoryFilterOptionConfig = new CategoryConfiguration();
        categoryFilterOptionConfig.setCategoryName(category.getName());
        categoryFilterOptionConfig.setDomain(category.getDomain().getDomainName());
        
        for(String categorySpecFilter:categorySpecFilters)
        {
            Attribute att = configuration.getAttributeByName(categorySpecFilter);
            if(att == null) continue; 
            if(!att.isCategoryFilter()) continue; 
            if(att.getFilterOptions() == null) continue; 
            
            if(query.getCondition(att.getName()) != null && !att.getFilterOptions().containsCategory(categoryFilterOptionConfig))
            {
                return false;
            }
        }
        
        return true; 
    }
    
    private CatalogCategory getCategoryFromEntry(FilterAttributeEntry entry)
    {
        Object o = JavaBeanExtractorUtil.INSTANCE.getPropertyValue(entry, "catalogCategory");
        if(o instanceof CatalogCategory)
        {
            return (CatalogCategory)o; 
        }
        return null;
    }

    /**
     * remove recursively query conditions from the given original query based upon the given predecessor -> attribute 
     * name map
     *  
     * @param originalQuery the query that may contain conditions based upon predecessor selected filter values
     * @param descendantAttributeNamesMap a map of predecessor attribute names to attribute names
     * @param attributeName starting attribute name  
     * @return the changed query if conditions where found or the original query   
     */
    private SearchIndexQuery removeDescendantConditionFromQuery(SearchIndexQuery originalQuery, Map<String, List<String>> descendantAttributeNamesMap, String attributeName)
    {
        SearchIndexQuery removedConditionsQuery = originalQuery;
        if(descendantAttributeNamesMap.containsKey(attributeName))
        {
            List<String> descendantAttributeNames = descendantAttributeNamesMap.get(attributeName);
            for(String descendantAttributeName:descendantAttributeNames)
            {
                removedConditionsQuery = removeDescendantConditionFromQuery(
                                removedConditionsQuery.getRemovedCondition(descendantAttributeName), 
                                descendantAttributeNamesMap, descendantAttributeName);
            }
        }
        
        return removedConditionsQuery; 
    }
    
    /**
     * get a map of attribute names out of the index configuration where predecessor-names are map keys 
     * and the attribute name defining the predecessor is the map value.   
     * @param indexConfig
     * @return map of predecessor - attribute names  
     */
    private Map<String, List<String>> getDescendantAttributeNamesMap(SearchIndexConfiguration indexConfig)
    {
        Iterator<Attribute> attIt = indexConfig.createAttributesIterator();
        Map<String,List<String>> descendantAttributeNamesMap = new HashMap<>(); 
        while(attIt.hasNext())
        {
            Attribute a = attIt.next(); 
            if(a.getFilterOptions() != null && a.getFilterOptions().getPredecessor() != null)
            {
                String predecessorName = a.getFilterOptions().getPredecessor().getName();
                if(descendantAttributeNamesMap.get(predecessorName) != null)
                {
                    descendantAttributeNamesMap.get(predecessorName).add(a.getName()); 
                }
                else
                {
                    List<String> names = new ArrayList<>(); 
                    names.add(a.getName()); 
                    descendantAttributeNamesMap.put(a.getFilterOptions().getPredecessor().getName(), names);
                }
            }
        }
        
        if(!descendantAttributeNamesMap.isEmpty())
        {
            return descendantAttributeNamesMap; 
        }
        
        return Collections.emptyMap(); 
    }
}
