package com.reason;

public class Joiner {

    public static String join(String separator, Iterable<String> items) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }

}
