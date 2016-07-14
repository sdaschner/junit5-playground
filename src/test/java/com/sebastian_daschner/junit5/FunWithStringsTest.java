package com.sebastian_daschner.junit5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.expectThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.api.DynamicTest.stream;

public class FunWithStringsTest {

    private FunWithStrings cut;

    @BeforeEach
    public void setUp() {
        cut = new FunWithStrings();
    }

    @Test
    public void testGetStringLengthSimpleTest() {
        assertEquals("hello:5", cut.getStringLength("hello"), () -> "'hello' (length: 5) wasn't calculated properly");
    }

    @TestFactory
    public Collection<DynamicTest> createGetStringLengthTestsIndividualLambdas() {
        return asList(
                dynamicTest("test: hello", () -> assertEquals(cut.getStringLength("hello"), "hello:5")),
                dynamicTest("test: hel", () -> assertEquals(cut.getStringLength("hel"), "hel:3")),
                dynamicTest("test: h", () -> assertEquals(cut.getStringLength("h"), "h:1")),
                dynamicTest("test:", () -> assertEquals(cut.getStringLength(""), ":0")),
                dynamicTest("test: ", () -> assertEquals(cut.getStringLength(" "), " :1"))
        );
    }

    @TestFactory
    public Stream<DynamicTest> createGetStringLengthTestsStreamDynamicTest() {
        final String[][] data = {
                // input, expected
                {"hello", "hello:5"},
                {"hel", "hel:3"},
                {"h", "h:1"},
                {"", ":0"},
                {" ", " :1"}
        };

        return Stream.of(data).map(o -> dynamicTest("test: " + o[0], () -> assertEquals(o[1], cut.getStringLength(o[0]))));
    }

    @TestFactory
    public Stream<DynamicTest> createGetStringLengthTestsStream() {
        final String[][] data = {
                // input, expected
                {"hello", "hello:5"},
                {"hel", "hel:3"},
                {"h", "h:1"},
                {"", ":0"},
                {" ", " :1"}
        };

        return stream(Stream.of(data).iterator(), o -> "test: " + o[0], o -> assertEquals(o[1], cut.getStringLength(o[0])));
    }

    @Test
    public void testNull() {
        expectThrows(NullPointerException.class, () -> cut.getStringLength(null));
    }

}
