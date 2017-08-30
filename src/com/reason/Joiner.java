package com.reason;

public class Joiner {

    public static String join(Iterable<String> items) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (!first) {
                sb.append(",");
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }

}
