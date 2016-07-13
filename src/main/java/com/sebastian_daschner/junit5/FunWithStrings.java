package com.sebastian_daschner.junit5;

public class FunWithStrings {

    public String getStringLength(final String string) {
        return string + ':' + string.length();
    }

    public long getCharCount(final String string, final char character) {
        return string.chars().filter(c -> c == character).count();
    }

}
