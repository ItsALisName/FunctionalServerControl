package net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.builder;

import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ReflectionToStringBuilder extends ToStringBuilder {
    public static String toString(Object object) {
        return toString(object, (ToStringStyle)null, false, false, (Class)null);
    }

    public static String toString(Object object, ToStringStyle style) {
        return toString(object, style, false, false, (Class)null);
    }

    public static String toString(Object object, ToStringStyle style, boolean outputTransients) {
        return toString(object, style, outputTransients, false, (Class)null);
    }

    public static String toString(Object object, ToStringStyle style, boolean outputTransients, boolean outputStatics) {
        return toString(object, style, outputTransients, outputStatics, (Class)null);
    }

    public static String toString(Object object, ToStringStyle style, boolean outputTransients, boolean outputStatics, Class reflectUpToClass) {
        return (new ReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients, outputStatics)).toString();
    }

    public static String toString(Object object, ToStringStyle style, boolean outputTransients, Class reflectUpToClass) {
        return (new ReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients)).toString();
    }

    public static String toStringExclude(Object object, String excludeFieldName) {
        return toStringExclude(object, new String[] { excludeFieldName });
    }

    public static String toStringExclude(Object object, Collection excludeFieldNames) {
        return toStringExclude(object, toNoNullStringArray(excludeFieldNames));
    }

    static String[] toNoNullStringArray(Collection collection) {
        if (collection == null)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        return toNoNullStringArray(collection.toArray());
    }

    static String[] toNoNullStringArray(Object[] array) {
        ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; i++) {
            Object e = array[i];
            if (e != null)
                list.add(e.toString());
        }
        return (String[]) list.<String>toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String toStringExclude(Object object, String[] excludeFieldNames) {
        return (new ReflectionToStringBuilder(object)).setExcludeFieldNames(excludeFieldNames).toString();
    }

    private boolean appendStatics = false;

    private boolean appendTransients = false;

    private String[] excludeFieldNames;

    private Class upToClass = null;

    public ReflectionToStringBuilder(Object object) {
        super(object);
    }

    public ReflectionToStringBuilder(Object object, ToStringStyle style) {
        super(object, style);
    }

    public ReflectionToStringBuilder(Object object, ToStringStyle style, StringBuffer buffer) {
        super(object, style, buffer);
    }

    public ReflectionToStringBuilder(Object object, ToStringStyle style, StringBuffer buffer, Class reflectUpToClass, boolean outputTransients) {
        super(object, style, buffer);
        setUpToClass(reflectUpToClass);
        setAppendTransients(outputTransients);
    }

    public ReflectionToStringBuilder(Object object, ToStringStyle style, StringBuffer buffer, Class reflectUpToClass, boolean outputTransients, boolean outputStatics) {
        super(object, style, buffer);
        setUpToClass(reflectUpToClass);
        setAppendTransients(outputTransients);
        setAppendStatics(outputStatics);
    }

    protected boolean accept(Field field) {
        if (field.getName().indexOf('$') != -1)
            return false;
        if (Modifier.isTransient(field.getModifiers()) && !isAppendTransients())
            return false;
        if (Modifier.isStatic(field.getModifiers()) && !isAppendStatics())
            return false;
        if (getExcludeFieldNames() != null && Arrays.binarySearch((Object[])getExcludeFieldNames(), field.getName()) >= 0)
            return false;
        return true;
    }

    protected void appendFieldsIn(Class clazz) {
        if (clazz.isArray()) {
            reflectionAppendArray(getObject());
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible((AccessibleObject[])fields, true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            if (accept(field))
                try {
                    Object fieldValue = getValue(field);
                    append(fieldName, fieldValue);
                } catch (IllegalAccessException ex) {
                    throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                }
        }
    }

    public String[] getExcludeFieldNames() {
        return this.excludeFieldNames;
    }

    public Class getUpToClass() {
        return this.upToClass;
    }

    protected Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException {
        return field.get(getObject());
    }

    public boolean isAppendStatics() {
        return this.appendStatics;
    }

    public boolean isAppendTransients() {
        return this.appendTransients;
    }

    public ToStringBuilder reflectionAppendArray(Object array) {
        getStyle().reflectionAppendArrayDetail(getStringBuffer(), null, array);
        return this;
    }

    public void setAppendStatics(boolean appendStatics) {
        this.appendStatics = appendStatics;
    }

    public void setAppendTransients(boolean appendTransients) {
        this.appendTransients = appendTransients;
    }

    public ReflectionToStringBuilder setExcludeFieldNames(String[] excludeFieldNamesParam) {
        if (excludeFieldNamesParam == null) {
            this.excludeFieldNames = null;
        } else {
            this.excludeFieldNames = toNoNullStringArray((Object[])excludeFieldNamesParam);
            Arrays.sort((Object[])this.excludeFieldNames);
        }
        return this;
    }

    public void setUpToClass(Class clazz) {
        if (clazz != null) {
            Object object = getObject();
            if (object != null && !clazz.isInstance(object))
                throw new IllegalArgumentException("Specified class is not a superclass of the object");
        }
        this.upToClass = clazz;
    }

    public String toString() {
        if (getObject() == null)
            return getStyle().getNullText();
        Class clazz = getObject().getClass();
        appendFieldsIn(clazz);
        while (clazz.getSuperclass() != null && clazz != getUpToClass()) {
            clazz = clazz.getSuperclass();
            appendFieldsIn(clazz);
        }
        return super.toString();
    }
}

