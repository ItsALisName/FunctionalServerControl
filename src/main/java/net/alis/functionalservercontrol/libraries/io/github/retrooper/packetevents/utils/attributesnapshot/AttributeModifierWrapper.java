 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.attributesnapshot;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.SubclassUtil;

import java.util.function.Supplier;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.lang.reflect.Constructor;

public class AttributeModifierWrapper extends WrappedPacket
{
    private static boolean supplierPresent;
    private static Class<? extends Enum<?>> operationEnumClass;
    private static Class<?> attributeModifierClass;
    private static Constructor<?> attributeModifierConstructor;
    
    public AttributeModifierWrapper(final NMSPacket packet) {
        super(packet);
    }
    
    public AttributeModifierWrapper(final UUID id, final String name, final double amount, final Operation operation) {
        super(create(id, name, amount, operation).packet);
    }
    
    public static AttributeModifierWrapper create(final UUID id, final String name, final double amount, final Operation operation) {
        if (AttributeModifierWrapper.attributeModifierConstructor == null) {
            try {
                AttributeModifierWrapper.attributeModifierConstructor = AttributeModifierWrapper.attributeModifierClass.getConstructor(UUID.class, String.class, Double.TYPE, (Class)((AttributeModifierWrapper.operationEnumClass == null) ? Integer.TYPE : AttributeModifierWrapper.operationEnumClass));
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        final int operationIndex = operation.ordinal();
        Object attributeModifierObj = null;
        try {
            if (AttributeModifierWrapper.operationEnumClass == null) {
                attributeModifierObj = AttributeModifierWrapper.attributeModifierConstructor.newInstance(id, name, amount, operationIndex);
            }
            else {
                final Enum<?> enumConst = EnumUtil.valueByIndex(AttributeModifierWrapper.operationEnumClass, operationIndex);
                attributeModifierObj = AttributeModifierWrapper.attributeModifierConstructor.newInstance(id, name, amount, enumConst);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException ex2) {
            ex2.printStackTrace();
        }
        return new AttributeModifierWrapper(new NMSPacket(attributeModifierObj));
    }
    
    @Override
    protected void load() {
        AttributeModifierWrapper.supplierPresent = (Reflection.getField(this.packet.getRawNMSPacket().getClass(), Supplier.class, 0) != null);
        AttributeModifierWrapper.operationEnumClass = SubclassUtil.getEnumSubClass(this.packet.getRawNMSPacket().getClass(), "Operation");
        AttributeModifierWrapper.attributeModifierClass = NMSUtils.getNMSClassWithoutException("AttributeModifier");
    }
    
    public double getAmount() {
        return this.readDouble(0);
    }
    
    public void setAmount(final double amount) {
        this.writeDouble(0, amount);
    }
    
    public Operation getOperation() {
        if (AttributeModifierWrapper.operationEnumClass == null) {
            final int operation = this.readInt(0);
            return Operation.values()[operation];
        }
        final Enum<?> enumConst = this.readEnumConstant(0, AttributeModifierWrapper.operationEnumClass);
        return Operation.values()[enumConst.ordinal()];
    }
    
    public void setOperation(final Operation operation) {
        if (AttributeModifierWrapper.operationEnumClass == null) {
            this.writeInt(0, operation.ordinal());
        }
        else {
            final Enum<?> enumConst = EnumUtil.valueByIndex(AttributeModifierWrapper.operationEnumClass, operation.ordinal());
            this.writeEnumConstant(0, enumConst);
        }
    }
    
    public String getName() {
        if (AttributeModifierWrapper.supplierPresent) {
            final Supplier<String> supplier = this.readObject(0, Supplier.class);
            return supplier.get();
        }
        return this.readString(0);
    }
    
    public void setName(final String name) {
        if (AttributeModifierWrapper.supplierPresent) {
            final Supplier<String> supplier = () -> name;
            this.writeObject(0, supplier);
        }
        else {
            this.writeString(0, name);
        }
    }
    
    public UUID getUUID() {
        return this.readObject(0, (Class<? extends UUID>)UUID.class);
    }
    
    public void setUUID(final UUID uuid) {
        this.writeObject(0, uuid);
    }
    
    public NMSPacket getNMSPacket() {
        return this.packet;
    }
    
    public enum Operation
    {
        ADDITION, 
        MULTIPLY_BASE, 
        MULTIPLY_TOTAL;
    }
}
