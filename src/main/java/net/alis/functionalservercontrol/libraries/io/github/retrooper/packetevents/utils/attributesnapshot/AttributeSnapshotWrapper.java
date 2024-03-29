 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.attributesnapshot;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.SubclassUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

public class AttributeSnapshotWrapper extends WrappedPacket
{
    private static byte constructorMode;
    private static Constructor<?> attributeSnapshotConstructor;
    private static Class<?> attributeSnapshotClass;
    private static Class<?> attributeBaseClass;
    private static Field iRegistryAttributeBaseField;
    private static Method getiRegistryByMinecraftKeyMethod;
    private static boolean stringKeyPresent;
    
    public AttributeSnapshotWrapper(final NMSPacket packet) {
        super(packet);
    }
    
    public AttributeSnapshotWrapper(final String key, final double value, final List<AttributeModifierWrapper> modifiers) {
        super(Objects.requireNonNull(create(key, value, modifiers)).packet);
    }
    
    public static AttributeSnapshotWrapper create(final String key, final double value, final Collection<AttributeModifierWrapper> modifiers) {
        Object nmsAttributeSnapshot = null;
        if (AttributeSnapshotWrapper.attributeSnapshotClass == null) {
            AttributeSnapshotWrapper.attributeSnapshotClass = NMSUtils.getNMSClassWithoutException("AttributeSnapshot");
            if (AttributeSnapshotWrapper.attributeSnapshotClass == null) {
                AttributeSnapshotWrapper.attributeSnapshotClass = SubclassUtil.getSubClass(PacketTypeClasses.Play.Server.UPDATE_ATTRIBUTES, "AttributeSnapshot");
            }
        }
        Label_0233: {
            if (AttributeSnapshotWrapper.attributeSnapshotConstructor == null) {
                try {
                    AttributeSnapshotWrapper.attributeSnapshotConstructor = AttributeSnapshotWrapper.attributeSnapshotClass.getConstructor(PacketTypeClasses.Play.Server.UPDATE_ATTRIBUTES, String.class, Double.TYPE, Collection.class);
                    AttributeSnapshotWrapper.constructorMode = 0;
                }
                catch (NoSuchMethodException e5) {
                    try {
                        AttributeSnapshotWrapper.attributeSnapshotConstructor = AttributeSnapshotWrapper.attributeSnapshotClass.getConstructor(String.class, Double.TYPE, Collection.class);
                        AttributeSnapshotWrapper.constructorMode = 1;
                    }
                    catch (NoSuchMethodException e6) {
                        AttributeSnapshotWrapper.constructorMode = 2;
                        if (AttributeSnapshotWrapper.attributeBaseClass == null) {
                            AttributeSnapshotWrapper.attributeBaseClass = NMSUtils.getNMSClassWithoutException("AttributeBase");
                        }
                        if (AttributeSnapshotWrapper.attributeSnapshotConstructor == null) {
                            try {
                                AttributeSnapshotWrapper.attributeSnapshotConstructor = AttributeSnapshotWrapper.attributeSnapshotClass.getConstructor(AttributeSnapshotWrapper.attributeBaseClass, Double.TYPE, Collection.class);
                            }
                            catch (NoSuchMethodException e3) {
                                e3.printStackTrace();
                            }
                        }
                        final Class<?> iRegistryClass = NMSUtils.getNMSClassWithoutException("IRegistry");
                        if (iRegistryClass == null) {
                            break Label_0233;
                        }
                        try {
                            AttributeSnapshotWrapper.iRegistryAttributeBaseField = iRegistryClass.getField("ATTRIBUTE");
                            AttributeSnapshotWrapper.getiRegistryByMinecraftKeyMethod = iRegistryClass.getDeclaredMethod("get", NMSUtils.minecraftKeyClass);
                        }
                        catch (NoSuchFieldException ex2) {}
                        catch (NoSuchMethodException ex3) {}
                    }
                }
            }
        }
        final List<Object> nmsModifiers = new ArrayList<Object>(modifiers.size());
        for (final AttributeModifierWrapper modifier : modifiers) {
            nmsModifiers.add(modifier.getNMSPacket().getRawNMSPacket());
        }
        try {
            switch (AttributeSnapshotWrapper.constructorMode) {
                case 0: {
                    nmsAttributeSnapshot = AttributeSnapshotWrapper.attributeSnapshotConstructor.newInstance(null, key, value, nmsModifiers);
                    break;
                }
                case 1: {
                    nmsAttributeSnapshot = AttributeSnapshotWrapper.attributeSnapshotConstructor.newInstance(key, value, nmsModifiers);
                    break;
                }
                case 2: {
                    final Object minecraftKey = NMSUtils.generateMinecraftKeyNew(key);
                    final Object attributeObj = AttributeSnapshotWrapper.iRegistryAttributeBaseField.get(null);
                    final Object nmsAttributeBase = AttributeSnapshotWrapper.getiRegistryByMinecraftKeyMethod.invoke(attributeObj, minecraftKey);
                    nmsAttributeSnapshot = AttributeSnapshotWrapper.attributeSnapshotConstructor.newInstance(nmsAttributeBase, value, nmsModifiers);
                    break;
                }
                default: {
                    return null;
                }
            }
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException ex4) {
            ex4.printStackTrace();
        }
        return new AttributeSnapshotWrapper(new NMSPacket(nmsAttributeSnapshot));
    }
    
    @Override
    protected void load() {
        AttributeSnapshotWrapper.stringKeyPresent = (Reflection.getField(this.packet.getRawNMSPacket().getClass(), String.class, 0) != null);
        if (AttributeSnapshotWrapper.attributeBaseClass == null) {
            AttributeSnapshotWrapper.attributeBaseClass = NMSUtils.getNMSClassWithoutException("AttributeBase");
            if (AttributeSnapshotWrapper.attributeBaseClass == null) {
                AttributeSnapshotWrapper.attributeBaseClass = NMSUtils.getNMClassWithoutException("world.entity.ai.attributes.AttributeBase");
            }
        }
        if (AttributeSnapshotWrapper.attributeSnapshotClass == null) {
            AttributeSnapshotWrapper.attributeSnapshotClass = NMSUtils.getNMSClassWithoutException("AttributeSnapshot");
            if (AttributeSnapshotWrapper.attributeSnapshotClass == null) {
                AttributeSnapshotWrapper.attributeSnapshotClass = SubclassUtil.getSubClass(PacketTypeClasses.Play.Server.UPDATE_ATTRIBUTES, "AttributeSnapshot");
            }
        }
        if (AttributeSnapshotWrapper.attributeSnapshotConstructor == null) {
            try {
                AttributeSnapshotWrapper.attributeSnapshotConstructor = AttributeSnapshotWrapper.attributeSnapshotClass.getConstructor(AttributeSnapshotWrapper.attributeBaseClass, Double.TYPE, Collection.class);
            }
            catch (NoSuchMethodException ex) {}
        }
        Class<?> iRegistryClass = NMSUtils.getNMSClassWithoutException("IRegistry");
        if (iRegistryClass == null) {
            iRegistryClass = NMSUtils.getNMClassWithoutException("core.IRegistry");
        }
        if (iRegistryClass != null) {
            try {
                AttributeSnapshotWrapper.iRegistryAttributeBaseField = iRegistryClass.getField("ATTRIBUTE");
                AttributeSnapshotWrapper.getiRegistryByMinecraftKeyMethod = iRegistryClass.getDeclaredMethod("get", NMSUtils.minecraftKeyClass);
            }
            catch (NoSuchFieldException ex2) {}
            catch (NoSuchMethodException ex3) {}
        }
    }
    
    public String getKey() {
        if (AttributeSnapshotWrapper.stringKeyPresent) {
            return this.readString(0);
        }
        final Object attributeBase = this.readObject(0, AttributeSnapshotWrapper.attributeBaseClass);
        final WrappedPacket attributeBaseWrapper = new WrappedPacket(new NMSPacket(attributeBase), AttributeSnapshotWrapper.attributeBaseClass);
        return attributeBaseWrapper.readString(0);
    }
    
    public void setKey(final String identifier) {
        if (AttributeSnapshotWrapper.stringKeyPresent) {
            this.writeString(0, identifier);
        }
        else {
            final Object minecraftKey = NMSUtils.generateMinecraftKeyNew(identifier);
            Object attributeObj = null;
            try {
                attributeObj = AttributeSnapshotWrapper.iRegistryAttributeBaseField.get(null);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Object nmsAttributeBase = null;
            try {
                nmsAttributeBase = AttributeSnapshotWrapper.getiRegistryByMinecraftKeyMethod.invoke(attributeObj, minecraftKey);
            }
            catch (IllegalAccessException | InvocationTargetException ex2) {
                ex2.printStackTrace();
            }
            this.write(AttributeSnapshotWrapper.attributeBaseClass, 0, nmsAttributeBase);
        }
    }
    
    public double getValue() {
        return this.readDouble(0);
    }
    
    public void setValue(final double value) {
        this.writeDouble(0, value);
    }
    
    public Collection<AttributeModifierWrapper> getModifiers() {
        final Collection<?> collection = this.readObject(0, Collection.class);
        final Collection<AttributeModifierWrapper> modifierWrappers = new ArrayList<>(collection.size());
        for (final Object obj : collection) {
            modifierWrappers.add(new AttributeModifierWrapper(new NMSPacket(obj)));
        }
        return modifierWrappers;
    }
    
    public void setModifiers(final Collection<AttributeModifierWrapper> attributeModifiers) {
        final Collection<Object> collection = new ArrayList<Object>(attributeModifiers.size());
        for (final AttributeModifierWrapper modifierWrapper : attributeModifiers) {
            collection.add(modifierWrapper.getNMSPacket().getRawNMSPacket());
        }
        this.writeObject(0, collection);
    }
    
    public NMSPacket getNMSPacket() {
        return this.packet;
    }
}
