package de.klaro.reformcloud2.executor.api.common.utility.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class Links {

    public static List<String> toLowerCase(List<String> list) {
        List<String> strings = new ArrayList<>();
        list.forEach(new Consumer<String>() {
            @Override
            public void accept(String string) {
                strings.add(string.toLowerCase());
            }
        });

        return strings;
    }

    public static <T> List<T> unmodifiable(List<T> in) {
        return Collections.unmodifiableList(in);
    }

    public static <T> List<T> newList(List<T> in) {
        return new ArrayList<>(in);
    }
}
