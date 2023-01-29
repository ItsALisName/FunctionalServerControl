package net.alis.functionalservercontrol.api.naf.v1_10_0.util.data;

import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;

import java.util.UUID;

public abstract class DefaultMeta implements MetaRewritable {

    protected String nickname;
    protected UUID uuid;
    protected FID fid;

    public DefaultMeta(String nickname, UUID uuid, FID fid) {
        this.nickname = nickname;
        this.uuid = uuid;
        this.fid = fid;
    }

    public abstract String getNickname();

    public abstract UUID getUuid();

    public abstract FID getFid();

}
