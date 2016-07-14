package com.sebastian_daschner.junit5;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.opentest4j.AssertionFailedError;

import java.util.function.Supplier;

/**
 * A class showing how Hamcrest Matchers can be used with JUnit 5 in a fluid programming style.
 */
public final class TestUtils {

    private TestUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Asserts that a given matcher is satisfied.
     * <p>
     * Inspired by {@link org.hamcrest.MatcherAssert#assertThat(Object, Matcher)}.
     */
    public static <T> void assertThat(T actual, Matcher<? super T> matcher, Supplier<String> messageSupplier) {
        if (!matcher.matches(actual)) {
            Description description = new StringDescription();
            description.appendText(messageSupplier.get())
                    .appendText("\nExpected: ")
                    .appendDescriptionOf(matcher)
                    .appendText("\n     but: ");
            matcher.describeMismatch(actual, description);

            throw new AssertionFailedError(description.toString());
        }
    }

}
