package by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.ArrayUtils;
import by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.ClassUtils;
import by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.SystemUtils;

abstract class MemberUtils {
    private static final int ACCESS_TEST = 7;

    private static final Method IS_SYNTHETIC;

    static {
        Method isSynthetic = null;
        if (SystemUtils.isJavaVersionAtLeast(1.5F))
            try {
                isSynthetic = Member.class.getMethod("isSynthetic", ArrayUtils.EMPTY_CLASS_ARRAY);
            } catch (Exception e) {}
        IS_SYNTHETIC = isSynthetic;
    }

    private static final Class[] ORDERED_PRIMITIVE_TYPES = new Class[] { byte.class, short.class, char.class, int.class, long.class, float.class, double.class };

    static void setAccessibleWorkaround(AccessibleObject o) {
        if (o == null || o.isAccessible())
            return;
        Member m = (Member)o;
        if (Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers()))
            try {
                o.setAccessible(true);
            } catch (SecurityException e) {}
    }

    static boolean isPackageAccess(int modifiers) {
        return ((modifiers & 0x7) == 0);
    }

    static boolean isAccessible(Member m) {
        return (m != null && Modifier.isPublic(m.getModifiers()) && !isSynthetic(m));
    }

    static boolean isSynthetic(Member m) {
        if (IS_SYNTHETIC != null)
            try {
                return ((Boolean)IS_SYNTHETIC.invoke(m, null)).booleanValue();
            } catch (Exception e) {}
        return false;
    }

    static int compareParameterTypes(Class[] left, Class[] right, Class[] actual) {
        float leftCost = getTotalTransformationCost(actual, left);
        float rightCost = getTotalTransformationCost(actual, right);
        return (leftCost < rightCost) ? -1 : ((rightCost < leftCost) ? 1 : 0);
    }

    private static float getTotalTransformationCost(Class[] srcArgs, Class[] destArgs) {
        float totalCost = 0.0F;
        for (int i = 0; i < srcArgs.length; i++) {
            Class srcClass = srcArgs[i];
            Class destClass = destArgs[i];
            totalCost += getObjectTransformationCost(srcClass, destClass);
        }
        return totalCost;
    }

    private static float getObjectTransformationCost(Class srcClass, Class destClass) {
        if (destClass.isPrimitive())
            return getPrimitivePromotionCost(srcClass, destClass);
        float cost = 0.0F;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
                cost += 0.25F;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }
        if (srcClass == null)
            cost += 1.5F;
        return cost;
    }

    private static float getPrimitivePromotionCost(Class srcClass, Class destClass) {
        float cost = 0.0F;
        Class cls = srcClass;
        if (!cls.isPrimitive()) {
            cost += 0.1F;
            cls = ClassUtils.wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; i++) {
            if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
                cost += 0.1F;
                if (i < ORDERED_PRIMITIVE_TYPES.length - 1)
                    cls = ORDERED_PRIMITIVE_TYPES[i + 1];
            }
        }
        return cost;
    }
}