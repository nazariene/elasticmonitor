package ru.nazariene.elasticmonitor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TypeConverterUtilTest {

    @Test
    public void testValidInteger_isOk() {
        int result = TypeConverterUtil.convert("1", "int");
        assertEquals(1, result);
    }

    @Test
    public void testInteger_Invalid_throwsException() {
        assertThrows(NumberFormatException.class, () -> {
            TypeConverterUtil.convert("not_int", "int");
        });
    }

    @Test
    public void testOutOfRangeInteger_throwsException() {
        assertThrows(NumberFormatException.class, () -> {
            TypeConverterUtil.convert(Integer.MAX_VALUE + "0", "int");
        });
    }

    @Test
    public void testValidDouble_isOk() {
        double result = TypeConverterUtil.convert("1.1", "double");
        assertEquals(1.1, result);
    }

    @Test
    public void testValidDouble_AsInteger_isOk() {
        double result = TypeConverterUtil.convert("1", "double");
        assertEquals(1, result);
    }

    @Test
    public void testBoolean_true_isTrue() {
        boolean result = TypeConverterUtil.convert("true", "boolean");
        assertTrue(result);
    }

    @Test
    public void testBoolean_True_isTrue() {
        boolean result = TypeConverterUtil.convert("True", "boolean");
        assertTrue(result);
    }

    @Test
    public void testBoolean_false_isFalse() {
        boolean result = TypeConverterUtil.convert("false", "boolean");
        assertFalse(result);
    }

    @Test
    public void testBoolean_False_isFalse() {
        boolean result = TypeConverterUtil.convert("False", "boolean");
        assertFalse(result);
    }

    @Test
    public void testBoolean_1_isFalse() {
        boolean result = TypeConverterUtil.convert("1", "boolean");
        assertFalse(result);
    }

    @Test
    public void testBoolean_emptyString_isFalse() {
        boolean result = TypeConverterUtil.convert("", "boolean");
        assertFalse(result);
    }



}

