/*     */ package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.settings;
/*     */ 
/*     */ import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PacketEventsSettings
/*     */ {
/*     */   private boolean locked;
/*  31 */   private ServerVersion fallbackServerVersion = ServerVersion.v_1_7_10;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkForUpdates = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean compatInjector = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean bStatsEnabled = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketEventsSettings lock() {
/*  56 */     this.locked = true;
/*  57 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PacketEventsSettings backupServerVersion(ServerVersion serverVersion) {
/*  72 */     if (!this.locked) {
/*  73 */       this.fallbackServerVersion = serverVersion;
/*     */     }
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketEventsSettings fallbackServerVersion(ServerVersion version) {
/*  87 */     if (!this.locked) {
/*  88 */       this.fallbackServerVersion = version;
/*     */     }
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketEventsSettings checkForUpdates(boolean checkForUpdates) {
/* 100 */     if (!this.locked) {
/* 101 */       this.checkForUpdates = checkForUpdates;
/*     */     }
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PacketEventsSettings bStats(boolean bStatsEnabled) {
/* 113 */     if (!this.locked) {
/* 114 */       this.bStatsEnabled = bStatsEnabled;
/*     */     }
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PacketEventsSettings compatInjector(boolean compatInjector) {
/* 129 */     if (!this.locked) {
/* 130 */       this.compatInjector = compatInjector;
/*     */     }
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLocked() {
/* 142 */     return this.locked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ServerVersion getBackupServerVersion() {
/* 153 */     return this.fallbackServerVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerVersion getFallbackServerVersion() {
/* 162 */     return this.fallbackServerVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean shouldCheckForUpdates() {
/* 171 */     return this.checkForUpdates;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean shouldUseCompatibilityInjector() {
/* 181 */     return this.compatInjector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isbStatsEnabled() {
/* 190 */     return this.bStatsEnabled;
/*     */   }
/*     */ }


/* Location:              C:\Users\patyj\Downloads\packetevents-1.8.4.jar!\io\github\retrooper\packetevents\settings\PacketEventsSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */