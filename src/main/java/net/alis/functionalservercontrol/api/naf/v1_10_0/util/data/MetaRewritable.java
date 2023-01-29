package net.alis.functionalservercontrol.api.naf.v1_10_0.util.data;

import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;

public interface MetaRewritable {

    DefaultMeta getMeta();

    void rewrite(RewritableCraftType type, Object param);

}
