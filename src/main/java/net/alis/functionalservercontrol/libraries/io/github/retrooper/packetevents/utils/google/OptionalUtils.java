

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.google;

import java.util.Optional;

public class OptionalUtils
{
    public static Optional<?> convertToJavaOptional(final Object googleOptional) {
        return Optional.of(GoogleOptionalUtils.getOptionalValue(googleOptional));
    }
}
