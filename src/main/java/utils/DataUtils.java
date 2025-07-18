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
}
