 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.SubclassUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public final class WrappedPacketInClientCommand extends WrappedPacket
{
    private static boolean v_1_16;
    private static Class<? extends Enum<?>> enumClientCommandClass;
    
    public WrappedPacketInClientCommand(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        WrappedPacketInClientCommand.v_1_16 = WrappedPacketInClientCommand.version.isNewerThanOrEquals(ServerVersion.v_1_16);
        WrappedPacketInClientCommand.enumClientCommandClass = NMSUtils.getNMSEnumClassWithoutException("EnumClientCommand");
        if (WrappedPacketInClientCommand.enumClientCommandClass == null) {
            WrappedPacketInClientCommand.enumClientCommandClass = SubclassUtil.getEnumSubClass(PacketTypeClasses.Play.Client.CLIENT_COMMAND, "EnumClientCommand");
        }
    }
    
    public ClientCommand getClientCommand() {
        final Enum<?> enumConst = this.readEnumConstant(0, WrappedPacketInClientCommand.enumClientCommandClass);
        return ClientCommand.values()[enumConst.ordinal()];
    }
    
    public void setClientCommand(final ClientCommand command) throws UnsupportedOperationException {
        if (command == ClientCommand.OPEN_INVENTORY_ACHIEVEMENT && WrappedPacketInClientCommand.v_1_16) {
            this.throwUnsupportedOperation(command);
        }
        final Enum<?> enumConst = EnumUtil.valueByIndex(WrappedPacketInClientCommand.enumClientCommandClass, command.ordinal());
        this.writeEnumConstant(0, enumConst);
    }
    
    public enum ClientCommand
    {
        PERFORM_RESPAWN, 
        REQUEST_STATS, 
        @SupportedVersions(ranges = { ServerVersion.v_1_7_10, ServerVersion.v_1_15_2 })
        @Deprecated
        OPEN_INVENTORY_ACHIEVEMENT;
    }
}
