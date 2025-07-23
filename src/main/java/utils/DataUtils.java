package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataUtils {

    public static List<String> convertNumberListToStringList(List<Number> numberList) {
        return numberList == null
                ? new ArrayList<>()
                : numberList.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public String key;
    public String siteUrl;
    public String baseUrl;
    public String query;
    public String search;
    public String endPointSRP;
    public String searchUrl;

    public String getApiUrl() {
        return baseUrl + search + query + endPointSRP;
    }


    public String getSiteSearchUrl() {
        return siteUrl + searchUrl + query;
    }


}
