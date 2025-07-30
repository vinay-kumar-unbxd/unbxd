package utils;

import java.util.Map;

public class TestData {
    private Map<String, String> data;
    private Map<String, String> searchData;
    
    public TestData(String fileName, String testKey) {
        this.data = ExcelReader.readRowByKeyAsMap(fileName, testKey);
    }
    
    // Constructor for search test data
    public TestData(String fileName, String testKey, boolean isSearchData) {
        if (isSearchData) {
            this.searchData = ExcelReader.readRowByKeyAsMap(fileName, testKey);
        } else {
            this.data = ExcelReader.readRowByKeyAsMap(fileName, testKey);
        }
    }
    
    public String getSiteUrl() {
        return data.get("siteUrl");
    }
    
    public String getBaseUrl() {
        return data.get("baseUrl");
    }
    
    public String getAutosuggest() {
        return data.get("autosuggest");
    }
    
    public String getSearch() {
        return data.get("search");
    }

    public String getAutosuggestEndpoint() {
        return data.get("autosuggestEndpoint");
    }
    
    public String getQuery() {
        return data.get("query");
    }
    
    public String getApiKey() {
        return data.get("apiKey");
    }
    
    public String getSearchEndpoint() {
        return data.get("searchEndpoint");
    }
    
    public String getAutosuggestApiUrl() {
        return getBaseUrl() + getAutosuggest() + getQuery() + getAutosuggestEndpoint();
    }
    
    public String getSearchApiUrl() {
        return getBaseUrl() + getSearch() + getQuery() + getSearchEndpoint();
    }
    
    public String getValue(String key) {
        return data.get(key);
    }
    
    // ========== SEARCH TEST DATA METHODS ==========
    public String getSingleWordQuery() {
        return searchData != null ? searchData.get("single word query") : null;
    }

    public String getTwoWordQuery() {
        return searchData != null ? searchData.get("2 word query") : null;
    }
 
    public String getSpellCheckQuery() {
        return searchData != null ? searchData.get("spell check") : null;
    }
    

    public String getVertical() {
        return searchData != null ? searchData.get("verticals") : null;
    }
    

    public static TestData forSearchQueries(String vertical) {
        return new TestData("search_test_data.xlsx", vertical, true);
    }
    
    public String getSearchValue(String columnName) {
        return searchData != null ? searchData.get(columnName) : null;
    }
    

    public boolean hasSearchData() {
        return searchData != null && !searchData.isEmpty();
    }
} 