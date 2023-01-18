package net.alis.functionalservercontrol.libraries.ru.leymooo.fixer;

import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MiniNBTFactory {

    private static Method stackModifier;

    static {
        try {
            stackModifier = NbtFactory.class.getDeclaredMethod("getStackModifier", ItemStack.class);
            stackModifier.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static NbtWrapper<?> fromItemTag(ItemStack stack) {
        StructureModifier<NbtBase<?>> modifier = null;
        try {
            modifier = (StructureModifier<NbtBase<?>>) stackModifier.invoke(null, stack);
        } catch (IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }
        NbtBase<?> result = modifier.read(0);
        if (result != null && result.toString().contains("{\"name\": \"null\"}")) {
            modifier.write(0, null);
            result = modifier.read(0);
        }
        if (result == null) {
            return null;
        }
        return NbtFactory.fromBase(result);
    }

}
