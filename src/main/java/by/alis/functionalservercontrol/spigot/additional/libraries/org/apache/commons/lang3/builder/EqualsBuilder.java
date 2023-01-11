package by.alis.functionalservercontrol.spigot.additional.libraries.org.apache.commons.lang3.builder;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import by.alis.functionalservercontrol.spigot.additional.libraries.org.apache.commons.lang3.ArrayUtils;

public class EqualsBuilder {
    private boolean isEquals = true;

    public static boolean reflectionEquals(Object lhs, Object rhs) {
        return reflectionEquals(lhs, rhs, false, null, null);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, Collection excludeFields) {
        return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, String[] excludeFields) {
        return reflectionEquals(lhs, rhs, false, null, excludeFields);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null, null);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class reflectUpToClass) {
        return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, null);
    }

    public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class reflectUpToClass, String[] excludeFields) {
        Class testClass;
        if (lhs == rhs)
            return true;
        if (lhs == null || rhs == null)
            return false;
        Class lhsClass = lhs.getClass();
        Class rhsClass = rhs.getClass();
        if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs))
                testClass = rhsClass;
        } else if (rhsClass.isInstance(lhs)) {
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs))
                testClass = lhsClass;
        } else {
            return false;
        }
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        try {
            reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
            while (testClass.getSuperclass() != null && testClass != reflectUpToClass) {
                testClass = testClass.getSuperclass();
                reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return equalsBuilder.isEquals();
    }

    private static void reflectionAppend(Object lhs, Object rhs, Class clazz, EqualsBuilder builder, boolean useTransients, String[] excludeFields) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible((AccessibleObject[])fields, true);
        for (int i = 0; i < fields.length && builder.isEquals; i++) {
            Field f = fields[i];
            if (!ArrayUtils.contains((Object[])excludeFields, f.getName()) && f.getName().indexOf('$') == -1 && (useTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()))
                try {
                    builder.append(f.get(lhs), f.get(rhs));
                } catch (IllegalAccessException e) {
                    throw new InternalError("Unexpected IllegalAccessException");
                }
        }
    }

    public EqualsBuilder appendSuper(boolean superEquals) {
        if (!this.isEquals)
            return this;
        this.isEquals = superEquals;
        return this;
    }

    public EqualsBuilder append(Object lhs, Object rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        Class lhsClass = lhs.getClass();
        if (!lhsClass.isArray()) {
            this.isEquals = lhs.equals(rhs);
        } else if (lhs.getClass() != rhs.getClass()) {
            setEquals(false);
        } else if (lhs instanceof long[]) {
            append((long[])lhs, (long[])rhs);
        } else if (lhs instanceof int[]) {
            append((int[])lhs, (int[])rhs);
        } else if (lhs instanceof short[]) {
            append((short[])lhs, (short[])rhs);
        } else if (lhs instanceof char[]) {
            append((char[])lhs, (char[])rhs);
        } else if (lhs instanceof byte[]) {
            append((byte[])lhs, (byte[])rhs);
        } else if (lhs instanceof double[]) {
            append((double[])lhs, (double[])rhs);
        } else if (lhs instanceof float[]) {
            append((float[])lhs, (float[])rhs);
        } else if (lhs instanceof boolean[]) {
            append((boolean[])lhs, (boolean[])rhs);
        } else {
            append((Object[])lhs, (Object[])rhs);
        }
        return this;
    }

    public EqualsBuilder append(long lhs, long rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(int lhs, int rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(short lhs, short rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(char lhs, char rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(byte lhs, byte rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(double lhs, double rhs) {
        if (!this.isEquals)
            return this;
        return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }

    public EqualsBuilder append(float lhs, float rhs) {
        if (!this.isEquals)
            return this;
        return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }

    public EqualsBuilder append(boolean lhs, boolean rhs) {
        if (!this.isEquals)
            return this;
        this.isEquals = (lhs == rhs);
        return this;
    }

    public EqualsBuilder append(Object[] lhs, Object[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(long[] lhs, long[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(int[] lhs, int[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(short[] lhs, short[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(char[] lhs, char[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(byte[] lhs, byte[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(double[] lhs, double[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(float[] lhs, float[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {
        if (!this.isEquals)
            return this;
        if (lhs == rhs)
            return this;
        if (lhs == null || rhs == null) {
            setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; i++)
            append(lhs[i], rhs[i]);
        return this;
    }

    public boolean isEquals() {
        return this.isEquals;
    }

    protected void setEquals(boolean isEquals) {
        this.isEquals = isEquals;
    }

    public void reset() {
        this.isEquals = true;
    }
}
