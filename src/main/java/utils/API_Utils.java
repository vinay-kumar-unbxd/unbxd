package utils;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class API_Utils {
    public static Response getAutosuggestResponse(String apiUrl) {
        Response response = RestAssured.get(apiUrl);
       response.then().statusCode(200); // Assert 200 OK
        return response;
    }

    public static List<String> getSuggestionsTitle(Response response, String docType, String path) {
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

//    public static List<Number> getSuggestionsNumericValue(Response response, String docType, String fieldName) {
//        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
//        List<Number> values = new ArrayList<>();
//
//        for (Map<String, Object> product : allProducts) {
//            if (docType.equals(product.get("doctype"))) {
//                Object fieldValue = product.get(fieldName);
//
//                if (fieldValue instanceof Number) {
//                    values.add((Number) fieldValue); // handles Integer, Double, etc.
//                } else if (fieldValue instanceof String) {
//                    String numericPart = ((String) fieldValue).replaceAll("[^0-9.]", "");
//                    try {
//                        // Check if it’s an integer or double based on presence of "."
//                        Number parsedValue = numericPart.contains(".")
//                                ? Double.parseDouble(numericPart)
//                                : Integer.parseInt(numericPart);
//                        values.add(parsedValue);
//                    } catch (NumberFormatException e) {
//                        System.out.println("❌ Unable to parse numeric field: " + fieldValue);
//                    }
//                }
//            }
//        }
//        return values;
//    }

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

    public static List<String> getFirstImageUrls(Response response, String docType) {
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

    public static List<String> getProductUrls(Response response) {
        List<Map<String, Object>> allProducts = response.jsonPath().getList("response.products");
        List<String> productUrls = new ArrayList<>();

        for (Map<String, Object> product : allProducts) {
            String productUrl = (String) product.get("productUrl");
            if (productUrl != null && !productUrl.trim().isEmpty()) {
                productUrls.add(productUrl.trim());
            }
        }
        return productUrls;
    }

    public static int getTotalProductCount(Response response) {
        return response.jsonPath().getInt("response.numberOfProducts");
    }




}

