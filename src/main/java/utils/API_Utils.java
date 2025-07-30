package utils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import io.restassured.path.json.JsonPath;

public class API_Utils {
    public static Response getAutosuggestResponse(String apiUrl, ValidationUtils.TestLogger logger) {
        Response response = RestAssured.get(apiUrl);
        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            logger.logPass("✅ API Response received successfully with status code: " + statusCode);
        } else {
            logger.logFail("❌ API request failed with status code: " + statusCode);
            throw new AssertionError("Expected status code 200, but got: " + statusCode);
        }
        return response;
    }

    public static Response getAndValidateApiResponse(String url, ValidationUtils.TestLogger logger) {
        Response response = RestAssured.get(url);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            logger.logPass("✅ API Response received successfully with status code: " + statusCode);
        } else {
            logger.logFail("❌ API request failed with status code: " + statusCode);
            throw new AssertionError("Expected status code 200, but got: " + statusCode);
        }

        return response;
    }

    public static List<String> getSuggestionsTitlesList(Response response, String docType, String path) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> suggestions = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            if (docType.equals(product.get("doctype"))) {
                String suggestion = (String) product.get(path);
                if (suggestion != null && !suggestion.trim().isEmpty()) {
                    suggestions.add(suggestion.trim());
                }
            }
        }
        return suggestions;
    }

    public static List<String> getSuggestionsFirstImageUrlsList(Response response, String docType) {
        List<Map<String, Object>> products = response.jsonPath().getList("response.products");
        List<String> imageUrls = new ArrayList<>();

        for (Map<String, Object> product : products) {
            if (docType.equals(product.get("doctype")) || product.get("doctype") == null) {
                Object imageUrlObj = product.get("imageUrl");

                if (imageUrlObj instanceof List) {
                    List<?> imageList = (List<?>) imageUrlObj;
                    if (!imageList.isEmpty()) {
                        imageUrls.add(imageList.get(0).toString());
                    }
                } else if (imageUrlObj instanceof String) {
                    // fallback if imageUrl is not an array but a single string
                    imageUrls.add(imageUrlObj.toString());
                }
            }
        }

        return imageUrls;
    }


    public static List<String> getSuggestionsPriceStrings(Response response, String docType, String fieldName) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> prices = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            if (docType.equals(product.get("doctype"))) {
                Object priceValue = product.get(fieldName);

                if (priceValue instanceof Number) {
                    double price = ((Number) priceValue).doubleValue();
                    if (price % 1 == 0) {
                        prices.add(String.valueOf((int) price)); // e.g., 1799.0 → "1799"
                    } else {
                        prices.add(String.valueOf(price)); // keep decimals like 1799.5
                    }
                } else if (priceValue instanceof String) {
                    String numeric = ((String) priceValue).replaceAll("[^\\d.]", ""); // remove $, Rs, etc.
                    if (!numeric.isEmpty()) {
                        try {
                            double price = Double.parseDouble(numeric);
                            if (price % 1 == 0) {
                                prices.add(String.valueOf((int) price));
                            } else {
                                prices.add(String.valueOf(price));
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Could not parse price: " + priceValue);
                        }
                    }
                }
            }
        }

        return prices;
    }

    public static Response getSearchResultResponse(String apiUrl) {
        Response response = RestAssured.get(apiUrl);
        response.then().statusCode(200); // Assert 200 OK
        return response;
    }

    public static List<String> getProductTitles(Response response) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> titles = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            String title = (String) product.get("title");
            if (title != null && !title.trim().isEmpty()) {
                titles.add(title.trim());
            }
        }
        return titles;
    }

    public static List<String> getProductImageUrls(Response response) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> imageUrls = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            Object imageUrlObj = product.get("imageUrl");

            if (imageUrlObj instanceof List) {
                List<?> imageList = (List<?>) imageUrlObj;
                if (!imageList.isEmpty()) {
                    imageUrls.add(imageList.get(0).toString());
                }
            } else if (imageUrlObj instanceof String) {
                imageUrls.add(imageUrlObj.toString());
            }
        }
        return imageUrls;
    }

    public static List<String> getProductUrls(Response response, int limit) {
        List<String> urls = new ArrayList<>();
        try {
            JsonPath jsonPath = response.jsonPath();
            List<Map<String, Object>> products = jsonPath.getList("response.products");
            
            if (products != null) {
                int count = Math.min(products.size(), limit);
                for (int i = 0; i < count; i++) {
                    String productUrl = jsonPath.getString("response.products[" + i + "].productUrl");
                    if (productUrl != null && !productUrl.isEmpty()) {
                        urls.add(productUrl);
                    }
                }
            }
        } catch (Exception e) {
            // Return empty list if any error occurs
        }
        return urls;
    }

    public static int getTotalProductCount(Response response) {
        return response.jsonPath().getInt("response.numberOfProducts");
    }

    public static List<String> getProductSkus(Response response) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> skus = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            String sku = (String) product.get("sku");
            if (sku != null && !sku.trim().isEmpty()) {
                skus.add(sku.trim());
            }
        }
        return skus;
    }

public static String getPopularProductTitle(Response response, int index) {
    String title = response.jsonPath().getString("response.products["+index+"].title");
    if (title == null) {
        throw new RuntimeException("❌ Product title is null");
    }
    if (title.trim().isEmpty()) {
        throw new RuntimeException("❌ Product title is empty");
    }
    return title;
}


    public static String getPopularProductImageUrl(Response response, int index) {
        String imageUrl = response.jsonPath().getString("response.products["+index+"].imageUrl");
        if (imageUrl == null) {
            throw new RuntimeException("❌ Product image URL is null");
        }
        if (imageUrl.trim().isEmpty()) {
            throw new RuntimeException("❌ Product image URL is empty");
        }
        return imageUrl;
    }
    
    public static String getPopularProductUrl(Response response, int index) {
        String productUrl = response.jsonPath().getString("response.products["+index+"].productUrl");
        if (productUrl == null) {
            throw new RuntimeException("❌ Product URL is null");
        }
        if (productUrl.trim().isEmpty()) {
            throw new RuntimeException("❌ Product URL is empty");
        }
        return productUrl;
    }

    public static String getPopularProductSku(Response response, int index) {
        String sku = response.jsonPath().getString("response.products["+index+"].sku");
        if (sku == null) {
            throw new RuntimeException("❌ Product SKU is null");
        }
        if (sku.trim().isEmpty()) {
            throw new RuntimeException("❌ Product SKU is empty");
        }
        return sku;
    }
    
    public static String getDidYouMeanSuggestion(Response response) {
        try {
            List<Map<String, Object>> didYouMeanList = response.jsonPath().getList("didYouMean");
            
            if (didYouMeanList != null && !didYouMeanList.isEmpty()) {
                Map<String, Object> firstSuggestion = didYouMeanList.get(0);
                String suggestion = (String) firstSuggestion.get("suggestion");
                
                if (suggestion != null && !suggestion.trim().isEmpty()) {
                    return suggestion.trim();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getDidYouMeanSuggestionWithFrequency(Response response) {
        try {
            List<Map<String, Object>> didYouMeanList = response.jsonPath().getList("didYouMean");
            
            if (didYouMeanList != null && !didYouMeanList.isEmpty()) {
                Map<String, Object> firstSuggestion = didYouMeanList.get(0);
                String suggestion = (String) firstSuggestion.get("suggestion");
                String frequency = (String) firstSuggestion.get("frequency");
                
                if (suggestion != null && !suggestion.trim().isEmpty()) {
                    return suggestion.trim() + "|" + (frequency != null ? frequency : "0");
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getFallbackQuery(Response response) {
        try {
            String fallbackQuery = response.jsonPath().getString("searchMetaData.fallback.q");
            
            if (fallbackQuery != null && !fallbackQuery.trim().isEmpty()) {
                return fallbackQuery.trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getFallbackReason(Response response) {
        try {
            String fallbackReason = response.jsonPath().getString("searchMetaData.fallback.reason.msg");
            
            if (fallbackReason != null && !fallbackReason.trim().isEmpty()) {
                return fallbackReason.trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

