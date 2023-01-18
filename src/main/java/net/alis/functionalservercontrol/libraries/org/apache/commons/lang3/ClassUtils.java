package net.alis.functionalservercontrol.libraries.org.apache.commons.lang3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.exception.NullArgumentException;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.text.StrBuilder;

public class ClassUtils {
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    public static final String PACKAGE_SEPARATOR = String.valueOf('.');

    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');

    private static final Map primitiveWrapperMap = new HashMap();

    static {
        primitiveWrapperMap.put(boolean.class, Boolean.class);
        primitiveWrapperMap.put(byte.class, Byte.class);
        primitiveWrapperMap.put(char.class, Character.class);
        primitiveWrapperMap.put(short.class, Short.class);
        primitiveWrapperMap.put(int.class, Integer.class);
        primitiveWrapperMap.put(long.class, Long.class);
        primitiveWrapperMap.put(double.class, Double.class);
        primitiveWrapperMap.put(float.class, Float.class);
        primitiveWrapperMap.put(void.class, void.class);
    }

    private static final Map wrapperPrimitiveMap = new HashMap();

    static {
        for (Iterator it = primitiveWrapperMap.keySet().iterator(); it.hasNext(); ) {
            Class primitiveClass = (Class) it.next();
            Class wrapperClass = (Class)primitiveWrapperMap.get(primitiveClass);
            if (!primitiveClass.equals(wrapperClass))
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
        }
    }

    private static final Map abbreviationMap = new HashMap();

    private static final Map reverseAbbreviationMap = new HashMap();

    private static void addAbbreviation(String primitive, String abbreviation) {
        abbreviationMap.put(primitive, abbreviation);
        reverseAbbreviationMap.put(abbreviation, primitive);
    }

    static {
        addAbbreviation("int", "I");
        addAbbreviation("boolean", "Z");
        addAbbreviation("float", "F");
        addAbbreviation("long", "J");
        addAbbreviation("short", "S");
        addAbbreviation("byte", "B");
        addAbbreviation("double", "D");
        addAbbreviation("char", "C");
    }

    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;
        return getShortClassName(object.getClass());
    }

    public static String getShortClassName(Class cls) {
        if (cls == null)
            return "";
        return getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className) {
        if (className == null)
            return "";
        if (className.length() == 0)
            return "";
        StrBuilder arrayPrefix = new StrBuilder();
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';')
                className = className.substring(1, className.length() - 1);
        }
        if (reverseAbbreviationMap.containsKey(className))
            className = (String)reverseAbbreviationMap.get(className);
        int lastDotIdx = className.lastIndexOf('.');
        int innerIdx = className.indexOf('$', (lastDotIdx == -1) ? 0 : (lastDotIdx + 1));
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1)
            out = out.replace('$', '.');
        return out + arrayPrefix;
    }

    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;
        return getPackageName(object.getClass());
    }

    public static String getPackageName(Class cls) {
        if (cls == null)
            return "";
        return getPackageName(cls.getName());
    }

    public static String getPackageName(String className) {
        if (className == null || className.length() == 0)
            return "";
        while (className.charAt(0) == '[')
            className = className.substring(1);
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';')
            className = className.substring(1);
        int i = className.lastIndexOf('.');
        if (i == -1)
            return "";
        return className.substring(0, i);
    }

    public static List getAllSuperclasses(Class cls) {
        if (cls == null)
            return null;
        List classes = new ArrayList();
        Class superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static List getAllInterfaces(Class cls) {
        if (cls == null)
            return null;
        List interfacesFound = new ArrayList();
        getAllInterfaces(cls, interfacesFound);
        return interfacesFound;
    }

    private static void getAllInterfaces(Class cls, List interfacesFound) {
        while (cls != null) {
            Class[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (!interfacesFound.contains(interfaces[i])) {
                    interfacesFound.add(interfaces[i]);
                    getAllInterfaces(interfaces[i], interfacesFound);
                }
            }
            cls = cls.getSuperclass();
        }
    }

    public static List convertClassNamesToClasses(List classNames) {
        if (classNames == null)
            return null;
        List classes = new ArrayList(classNames.size());
        for (Iterator it = classNames.iterator(); it.hasNext(); ) {
            String className = (String) it.next();
            try {
                classes.add(Class.forName(className));
            } catch (Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }

    public static List convertClassesToClassNames(List classes) {
        if (classes == null)
            return null;
        List classNames = new ArrayList(classes.size());
        for (Iterator it = classes.iterator(); it.hasNext(); ) {
            Class cls = (Class) it.next();
            if (cls == null) {
                classNames.add(null);
                continue;
            }
            classNames.add(cls.getName());
        }
        return classNames;
    }

    public static boolean isAssignable(Class[] classArray, Class[] toClassArray) {
        return isAssignable(classArray, toClassArray, false);
    }

    public static boolean isAssignable(Class[] classArray, Class[] toClassArray, boolean autoboxing) {
        if (!ArrayUtils.isSameLength((Object[])classArray, (Object[])toClassArray))
            return false;
        if (classArray == null)
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        if (toClassArray == null)
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing))
                return false;
        }
        return true;
    }

    public static boolean isAssignable(Class cls, Class toClass) {
        return isAssignable(cls, toClass, false);
    }

    public static boolean isAssignable(Class cls, Class toClass, boolean autoboxing) {
        if (toClass == null)
            return false;
        if (cls == null)
            return !toClass.isPrimitive();
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null)
                    return false;
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null)
                    return false;
            }
        }
        if (cls.equals(toClass))
            return true;
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive())
                return false;
            if (int.class.equals(cls))
                return (long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass));
            if (long.class.equals(cls))
                return (float.class.equals(toClass) || double.class.equals(toClass));
            if (boolean.class.equals(cls))
                return false;
            if (double.class.equals(cls))
                return false;
            if (float.class.equals(cls))
                return double.class.equals(toClass);
            if (char.class.equals(cls))
                return (int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass));
            if (short.class.equals(cls))
                return (int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass));
            if (byte.class.equals(cls))
                return (short.class.equals(toClass) || int.class.equals(toClass) || long.class.equals(toClass) || float.class.equals(toClass) || double.class.equals(toClass));
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    public static Class primitiveToWrapper(Class cls) {
        Class convertedClass = cls;
        if (cls != null && cls.isPrimitive())
            convertedClass = (Class)primitiveWrapperMap.get(cls);
        return convertedClass;
    }

    public static Class[] primitivesToWrappers(Class[] classes) {
        if (classes == null)
            return null;
        if (classes.length == 0)
            return classes;
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++)
            convertedClasses[i] = primitiveToWrapper(classes[i]);
        return convertedClasses;
    }

    public static Class wrapperToPrimitive(Class cls) {
        return (Class)wrapperPrimitiveMap.get(cls);
    }

    public static Class[] wrappersToPrimitives(Class[] classes) {
        if (classes == null)
            return null;
        if (classes.length == 0)
            return classes;
        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++)
            convertedClasses[i] = wrapperToPrimitive(classes[i]);
        return convertedClasses;
    }

    public static boolean isInnerClass(Class cls) {
        if (cls == null)
            return false;
        return (cls.getName().indexOf('$') >= 0);
    }

    public static Class getClass(ClassLoader classLoader, String className, boolean initialize) throws ClassNotFoundException {
        try {
            Class clazz;
            if (abbreviationMap.containsKey(className)) {
                String clsName = "[" + abbreviationMap.get(className);
                clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
            } else {
                clazz = Class.forName(toCanonicalName(className), initialize, classLoader);
            }
            return clazz;
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = className.lastIndexOf('.');
            if (lastDotIndex != -1)
                try {
                    return getClass(classLoader, className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1), initialize);
                } catch (ClassNotFoundException ex2) {}
            throw ex;
        }
    }

    public static Class getClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return getClass(classLoader, className, true);
    }

    public static Class getClass(String className) throws ClassNotFoundException {
        return getClass(className, true);
    }

    public static Class getClass(String className, boolean initialize) throws ClassNotFoundException {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = (contextCL == null) ? ClassUtils.class.getClassLoader() : contextCL;
        return getClass(loader, className, initialize);
    }

    public static Method getPublicMethod(Class cls, String methodName, Class[] parameterTypes) throws SecurityException, NoSuchMethodException {
        Method declaredMethod = cls.getMethod(methodName, parameterTypes);
        if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers()))
            return declaredMethod;
        List candidateClasses = new ArrayList();
        candidateClasses.addAll(getAllInterfaces(cls));
        candidateClasses.addAll(getAllSuperclasses(cls));
        for (Iterator it = candidateClasses.iterator(); it.hasNext(); ) {
            Method candidateMethod;
            Class candidateClass = (Class) it.next();
            if (!Modifier.isPublic(candidateClass.getModifiers()))
                continue;
            try {
                candidateMethod = candidateClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                continue;
            }
            if (Modifier.isPublic(candidateMethod.getDeclaringClass().getModifiers()))
                return candidateMethod;
        }
        throw new NoSuchMethodException("Can't find a public method for " + methodName + " " + ArrayUtils.toString(parameterTypes));
    }

    private static String toCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        if (className == null)
            throw new NullArgumentException("className");
        if (className.endsWith("[]")) {
            StrBuilder classNameBuffer = new StrBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            String abbreviation = (String)abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }

    public static Class[] toClass(Object[] array) {
        if (array == null)
            return null;
        if (array.length == 0)
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        Class[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++)
            classes[i] = (array[i] == null) ? null : array[i].getClass();
        return classes;
    }

    public static String getShortCanonicalName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;
        return getShortCanonicalName(object.getClass().getName());
    }

    public static String getShortCanonicalName(Class cls) {
        if (cls == null)
            return "";
        return getShortCanonicalName(cls.getName());
    }

    public static String getShortCanonicalName(String canonicalName) {
        return getShortClassName(getCanonicalName(canonicalName));
    }

    public static String getPackageCanonicalName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;
        return getPackageCanonicalName(object.getClass().getName());
    }

    public static String getPackageCanonicalName(Class cls) {
        if (cls == null)
            return "";
        return getPackageCanonicalName(cls.getName());
    }

    public static String getPackageCanonicalName(String canonicalName) {
        return getPackageName(getCanonicalName(canonicalName));
    }

    private static String getCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        if (className == null)
            return null;
        int dim = 0;
        while (className.startsWith("[")) {
            dim++;
            className = className.substring(1);
        }
        if (dim < 1)
            return className;
        if (className.startsWith("L")) {
            className = className.substring(1, className.endsWith(";") ? (className.length() - 1) : className.length());
        } else if (className.length() > 0) {
            className = (String)reverseAbbreviationMap.get(className.substring(0, 1));
        }
        StrBuilder canonicalClassNameBuffer = new StrBuilder(className);
        for (int i = 0; i < dim; i++)
            canonicalClassNameBuffer.append("[]");
        return canonicalClassNameBuffer.toString();
    }
}
