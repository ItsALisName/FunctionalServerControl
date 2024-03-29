

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PostTaskEvent
{
    boolean isPostTaskAvailable();
    
    @Nullable
    Runnable getPostTask();
    
    void setPostTask(@NotNull final Runnable p0);
}
