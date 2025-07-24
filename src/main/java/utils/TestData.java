package utils;

import java.util.Map;

public class TestData {
    private Map<String, String> data;
    
    public TestData(String fileName, String testKey) {
        this.data = ExcelReader.readRowByKeyAsMap(fileName, testKey);
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
} 