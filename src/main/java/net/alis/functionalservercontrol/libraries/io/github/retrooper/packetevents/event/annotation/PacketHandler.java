

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.annotation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface PacketHandler {
    byte priority() default 2;
}
