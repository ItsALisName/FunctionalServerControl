 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums;

public class EnumUtil
{
    public static Enum<?> valueOf(final Class<? extends Enum<?>> cls, final String constantName) {
        for (final Enum<?> enumConstant : (Enum[])cls.getEnumConstants()) {
            if (enumConstant.name().equals(constantName)) {
                return enumConstant;
            }
        }
        return null;
    }
    
    public static Enum<?> valueByIndex(final Class<? extends Enum<?>> cls, final int index) {
        return (Enum<?>)((Enum[])cls.getEnumConstants())[index];
    }
}
