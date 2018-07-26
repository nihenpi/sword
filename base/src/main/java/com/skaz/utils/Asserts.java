package com.skaz.utils;

import java.util.Collection;
import java.util.Map;


/**
 * @author jungle
 */
public abstract class Asserts {

    public static void isEquals(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

    public static void isEquals(Object expected, Object actual) {
        isEquals(expected, actual, null);
    }

    public static void isEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        failNotEquals(expected, actual, message);
    }

    public static void fail(String message) {
        if (message == null) {
            throw new AssertionError();
        }
        throw new AssertionError(message);
    }

    public static void failAndPrintStackTrace(String message) {
        Error e = null;
        if (message == null) {
            e = new AssertionError();
        }
        e = new AssertionError(message);
        e.printStackTrace();
        throw e;
    }

    public static String format(Object expected, Object actual, String message) {
        String formatted = "";
        if (message != null && message.length() > 0) {
            formatted = message + " ";
        }
        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }

    public static void failNotEquals(Object expected, Object actual, String message) {
        failAndPrintStackTrace(format(expected, actual, message));
    }


    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void isNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotNull(Object object) {
        isNotNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void hasLength(String text, String message) {
        if (!Strings.hasLength(text)) {
            throw new IllegalArgumentException(message);
        }
    }


    public static void hasLength(String text) {
        hasLength(text, "[Assertion failed] - this String argument must have length; it must not be null or EMPTY");
    }

    public static void hasText(String text, String message) {
        if (!Strings.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(String text) {
        hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, EMPTY, or blank");
    }

    public static void isNotContain(String textToSearch, String substring, String message) {
        if (Strings.hasLength(textToSearch) && Strings.hasLength(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotContain(String textToSearch, String substring) {
        isNotContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }

    public static void isNotEmpty(Object[] array, String message) {
        if (Objects.isEmpty(array)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotEmpty(Object[] array) {
        isNotEmpty(array, "[Assertion failed] - this array must not be EMPTY: it must contain at least 1 element");
    }

    public static void isNotNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    public static void isNotNullElements(Object[] array) {
        isNotNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    public static void isNotEmpty(Collection<?> collection, String message) {
        if (Collections.isEmpty(collection)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotEmpty(Collection<?> collection) {
        isNotEmpty(collection, "[Assertion failed] - this collection must not be EMPTY: it must contain at least 1 element");
    }

    public static void isNotEmpty(Map<?, ?> map, String message) {
        if (Collections.isEmpty(map)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNotEmpty(Map<?, ?> map) {
        isNotEmpty(map, "[Assertion failed] - this map must not be EMPTY; it must contain at least one entry");
    }

    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        isNotNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException((Strings.hasLength(message) ? message + " " : "") + "Object of class [" + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }

    public static void isAssignable(Class<?> superType, Class<?> subType) {
        isAssignable(superType, subType, "");
    }

    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        isNotNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression) {
        state(expression, "[Assertion failed] - this state invariant must be true");
    }

}
