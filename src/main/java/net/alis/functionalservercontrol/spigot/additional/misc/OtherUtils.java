package net.alis.functionalservercontrol.spigot.additional.misc;

import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.enums.ProtocolVersions;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.StringUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class OtherUtils {
    private static final String DOMAINS_REGEX = "AC|ACADEMY|ACCOUNTANTS|ACTOR|AD|AE|AERO|AF|AG|AGENCY|AI|AIRFORCE|AL|AM|AN|AO|AQ|AR|ARCHI|ARPA|AS|ASIA|ASSOCIATES|AT|AU|AW|AX|AXA|AZ|BA|BAR|BARGAINS|BAYERN|BB|BD|BE|BERLIN|BEST|BF|BG|BH|BI|BID|BIKE|BIZ|BJ|BLACK|BLACKFRIDAY|BLUE|BM|BN|BO|BOUTIQUE|BR|BS|BT|BUILD|BUILDERS|BUZZ|BV|BW|BY|BZ|CA|CAB|CAMERA|CAMP|CAPITAL|CARDS|CARE|CAREER|CAREERS|CASH|CAT|CATERING|CC|CD|CENTER|CEO|CF|CG|CH|CHEAP|CHRISTMAS|CI|CITIC|CK|CL|CLAIMS|CLEANING|CLINIC|CLOTHING|CLUB|CM|CN|CO|CODES|COFFEE|COLLEGE|COLOGNE|COM|COMMUNITY|COMPANY|COMPUTER|CONDOS|CONSTRUCTION|CONSULTING|CONTRACTORS|COOKING|COOL|COOP|COUNTRY|CR|CREDIT|CREDITCARD|CRUISES|CU|CV|CW|CX|CY|CZ|DANCE|DATING|DE|DEMOCRAT|DENTAL|DESI|DIAMONDS|DIGITAL|DIRECTORY|DISCOUNT|DJ|DK|DM|DNP|DO|DOMAINS|DZ|EC|EDU|EDUCATION|EE|EG|EMAIL|ENGINEERING|ENTERPRISES|EQUIPMENT|ER|ES|ESTATE|ET|EU|EUS|EVENTS|EXCHANGE|EXPERT|EXPOSED|FAIL|FARM|FEEDBACK|FI|FINANCE|FINANCIAL|FISH|FISHING|FITNESS|FJ|FK|FLIGHTS|FLORIST|FM|FO|FOO|FOUNDATION|FR|FROGANS|FUND|FURNITURE|FUTBOL|GA|GAL|GALLERY|GB|GD|GE|GF|GG|GH|GI|GIFT|GL|GLASS|GLOBO|GM|GMO|GN|GOP|GOV|GP|GQ|GR|GRAPHICS|GRATIS|GRIPE|GS|GT|GU|GUITARS|GURU|GW|GY|HAUS|HK|HM|HN|HOLDINGS|HOLIDAY|HORSE|HOUSE|HR|HT|HU|ID|IE|IL|IM|IMMOBILIEN|IN|INDUSTRIES|INFO|INK|INSTITUTE|INSURE|INT|INTERNATIONAL|INVESTMENTS|IO|IQ|IR|IS|IT|JE|JETZT|JM|JO|JOBS|JP|KAUFEN|KE|KG|KH|KI|KIM|KITCHEN|KIWI|KM|KN|KOELN|KP|KR|KRED|KW|KY|KZ|LA|LAND|LB|LC|LEASE|LI|LIGHTING|LIMITED|LIMO|LINK|LK|LONDON|LR|LS|LT|LU|LUXURY|LV|LY|MA|MAISON|MANAGEMENT|MANGO|MARKETING|MC|MD|ME|MEDIA|MEET|MENU|MG|MH|MIAMI|MIL|MK|ML|MM|MN|MO|MOBI|MODA|MOE|MONASH|MOSCOW|MP|MQ|MR|MS|MT|MU|MUSEUM|MV|MW|MX|MY|MZ|NA|NAGOYA|NAME|NC|NE|NET|NEUSTAR|NF|NG|NI|NINJA|NL|NO|NP|NR|NU|NYC|NZ|OKINAWA|OM|ONL|ORG|PA|PARIS|PARTNERS|PARTS|PE|PF|PG|PH|PHOTO|PHOTOGRAPHY|PHOTOS|PICS|PICTURES|PINK|PK|PL|PLUMBING|PM|PN|POST|PR|PRO|PRODUCTIONS|PROPERTIES|PS|PT|PUB|PW|PY|QA|QPON|QUEBEC|RE|RECIPES|RED|REISEN|REN|RENTALS|REPAIR|REPORT|REST|REVIEWS|RICH|RO|ROCKS|RODEO|RS|RU|RUHR|RW|RYUKYU|SA|SAARLAND|SB|SC|SCHULE|SD|SE|SERVICES|SEXY|SG|SH|SHIKSHA|SHOES|SI|SINGLES|SJ|SK|SL|SM|SN|SO|SOCIAL|SOHU|SOLAR|SOLUTIONS|SOY|SR|ST|SU|SUPPLIES|SUPPLY|SUPPORT|SURGERY|SV|SX|SY|SYSTEMS|SZ|TATTOO|TAX|TC|TD|TECHNOLOGY|TEL|TF|TG|TH|TIENDA|TIPS|TJ|TK|TL|TM|TN|TO|TODAY|TOKYO|TOOLS|TOWN|TOYS|TP|TR|TRADE|TRAINING|TRAVEL|TT|TV|TW|TZ|UA|UG|UK|UNIVERSITY|UNO|US|UY|UZ|VA|VACATIONS|VC|VE|VEGAS|VENTURES|VG|VI|VIAJES|VILLAS|VISION|VN|VODKA|VOTE|VOTING|VOTO|VOYAGE|VU|WANG|WATCH|WEBCAM|WED|WF|WIEN|WIKI|WORKS|WS|WTC|WTF|XN--3BST00M|XN--3DS443G|XN--3E0B707E|XN--45BRJ9C|XN--55QW42G|XN--55QX5D|XN--6FRZ82G|XN--6QQ986B3XL|XN--80ADXHKS|XN--80AO21A|XN--80ASEHDB|XN--80ASWG|XN--90A3AC|XN--C1AVG|XN--CG4BKI|XN--CLCHC0EA0B2G2A9GCD|XN--CZRU2D|XN--D1ACJ3B|XN--FIQ228C5HS|XN--FIQ64B|XN--FIQS8S|XN--FIQZ9S|XN--FPCRJ9C3D|XN--FZC2C9E2C|XN--GECRJ9C|XN--H2BRJ9C|XN--I1B6B1A6A2E|XN--IO0A7I|XN--J1AMH|XN--J6W193G|XN--KPRW13D|XN--KPRY57D|XN--L1ACC|XN--LGBBAT1AD8J|XN--MGB9AWBF|XN--MGBA3A4F16A|XN--MGBAAM7A8H|XN--MGBAB2BD|XN--MGBAYH7GPA|XN--MGBBH1A71E|XN--MGBC0A9AZCG|XN--MGBERP4A5D4AR|XN--MGBX4CD0AB|XN--NGBC5AZD|XN--NQV7F|XN--NQV7FS00EMA|XN--O3CW4H|XN--OGBPF8FL|XN--P1AI|XN--PGBS0DH|XN--Q9JYB4C|XN--RHQV96G|XN--S9BRJ9C|XN--SES554G|XN--UNUP4Y|XN--WGBH1C|XN--WGBL6A|XN--XKC2AL3HYE2A|XN--XKC2DL3A5EE0H|XN--YFRO4I67O|XN--YGBI2AMMX|XN--ZFR164B|XXX|XYZ|YE|YOKOHAMA|YT|ZA|ZM|ZONE|ZW";

    public static boolean isArgumentIP(String str) {
        String ipPattern = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";
        return Pattern.compile(ipPattern).matcher(str).find();
    }

    public static String getPlayerIPByUUID(UUID uuid) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: return BaseManager.getBaseManager().getIpByUUID(uuid);
            case MYSQL: return BaseManager.getBaseManager().getIpByUUID(uuid);
            case H2: {}
        }
        return "Cannot resolve ip";
    }

    public static boolean isClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static String getServerCoreName(Server server) {
        return server.getVersion().split("-")[1];
    }

    public static boolean isNotNullPlayer(String name) {
        return BaseManager.getBaseManager().getNamesFromAllPlayers().contains(name);
    }

    public static boolean isNotNullPlayer(UUID uuid) {
        return BaseManager.getBaseManager().getUUIDsFromAllPlayers().contains(String.valueOf(uuid));
    }

    public static boolean isNotNullIp(String ip) {
        return BaseManager.getBaseManager().getIpsFromAllPlayers().contains(ip);
    }

    public static boolean isNumber(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isOldServerVersion() {
        Server server = Bukkit.getServer();
        String version = getServerPackageName(server);
        if (version.startsWith("v1_8"))
            return true;
        if (version.startsWith("v1_9"))
            return true;
        if (version.startsWith("v1_10"))
            return true;
        if (version.startsWith("v1_11"))
            return true;
        if (version.startsWith("v1_12"))
            return true;
        if(version.startsWith("v1_13"))
            return true;
        if(version.startsWith("v1_14"))
            return true;
        if(version.startsWith("v1_15"))
            return true;
        return false;
    }

    public static String getServerPackageName(Server server) {
        String packageName = server.getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static int generateRandomNumber() {
        return (int) (Math.random() * (299 + 1));
    }

    public static boolean verifyNickNameFormat(String nickProtocolVersions) {
        if(!getConfigSettings().isNickFormatControlEnabled()) return false;
        boolean a = false;
        for(String blockedFormat : getConfigSettings().getBlockedNickFormats()) {
            String pattern = "(" + blockedFormat.replace("<num>", "[0-9]").replace("<let>", "[a-zA-Z]") + ")";
            if(Pattern.compile(pattern).matcher(nickProtocolVersions).find()) {
                a = true;
                break;
            }
        }
        return a;
    }

    @Nullable
    public static OfflinePlayer getOnlinePlayerByIP(String ip) {
        for(Map.Entry<Player, String> e : TemporaryCache.getOnlineIps().entrySet()) {
            if(e.getValue().equalsIgnoreCase(ip)) {
                return (OfflinePlayer)e.getKey();
            }
        }
        return null;
    }

    @Nullable
    public static OfflinePlayer getOfflinePlayerByName(String name) {
        OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(UUID.fromString(BaseManager.getBaseManager().getUuidByName(name)));
        return player == null ? null : player;
    }

    public static OfflinePlayer getPlayerByIP(String ip) {
        OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(BaseManager.getBaseManager().getUUIDByIp(ip));
        return player == null ? null : player;
    }


    public static boolean isArgumentDomain(String str) {
        String domainPattern = "([a-z-0-9]{1,50})\\.(" + DOMAINS_REGEX.toLowerCase() + ")(?![a-z0-9])";
        return Pattern.compile(domainPattern).matcher(str).find();
    }


    public static boolean isSuppotedVersion(Server server) {
        String version = getServerPackageName(server);
        if (version.startsWith("v1_8"))
            return true;
        if (version.startsWith("v1_9"))
            return true;
        if (version.startsWith("v1_10"))
            return true;
        if (version.startsWith("v1_11"))
            return true;
        if (version.startsWith("v1_12"))
            return true;
        if(version.startsWith("v1_13"))
            return true;
        if(version.startsWith("v1_14"))
            return true;
        if(version.startsWith("v1_15"))
            return true;
        if(version.startsWith("v1_16"))
            return true;
        if(version.startsWith("v1_17"))
            return true;
        if(version.startsWith("v1_18"))
            return true;
        if(version.startsWith("v1_19"))
            return true;
        return false;
    }

    public static boolean isServerSupportMDHoverText() {
        return isClassExists("net.md_5.bungee.api.chat.hover.content.Text");
    }

    public static boolean isServerSupportAdventureApi() {
        return isClassExists("net.kyori.adventure.text.Component");
    }

    public static ProtocolVersions convertProtocolVersion(int protocolVersion) {
        if(protocolVersion < 0) return ProtocolVersions.UNKNOWN;
        if(protocolVersion <= 4) return ProtocolVersions.V7_1_5;
        if(protocolVersion == 5) return ProtocolVersions.V7_6_10;
        if(protocolVersion <= 106) return ProtocolVersions.V8_9;
        if(protocolVersion == 107) return ProtocolVersions.V9;
        if(protocolVersion == 108) return ProtocolVersions.V9_1;
        if(protocolVersion == 109) return ProtocolVersions.V9_2;
        if(protocolVersion <= 203) return ProtocolVersions.V9_3_4;
        if(protocolVersion <= 313) return ProtocolVersions.V10_1_2;
        if(protocolVersion == 315) return ProtocolVersions.V11;
        if(protocolVersion <= 327) return ProtocolVersions.V11_1_2;
        if(protocolVersion <= 336) return ProtocolVersions.V12;
        if(protocolVersion <= 338) return ProtocolVersions.V12_1;
        if(protocolVersion <= 382) return ProtocolVersions.V12_2;
        if(protocolVersion <= 398) return ProtocolVersions.V13;
        if(protocolVersion <= 401) return ProtocolVersions.V13_1;
        if(protocolVersion <= 471) return ProtocolVersions.V13_2;
        if(protocolVersion <= 477) return ProtocolVersions.V14;
        if(protocolVersion <= 480) return ProtocolVersions.V14_1;
        if(protocolVersion <= 485) return ProtocolVersions.V14_2;
        if(protocolVersion <= 490) return ProtocolVersions.V14_3;
        if(protocolVersion <= 564) return ProtocolVersions.V14_4;
        if(protocolVersion <= 573) return ProtocolVersions.V15;
        if(protocolVersion <= 575) return ProtocolVersions.V15_1;
        if(protocolVersion <= 719) return ProtocolVersions.V15_2;
        if(protocolVersion <= 735) return ProtocolVersions.V16;
        if(protocolVersion <= 743) return ProtocolVersions.V16_1;
        if(protocolVersion <= 751) return ProtocolVersions.V16_2;
        if(protocolVersion <= 753) return ProtocolVersions.V16_3;
        if(protocolVersion == 754) return ProtocolVersions.V16_4_5;
        if(protocolVersion == 755) return ProtocolVersions.V17;
        if(protocolVersion == 756) return ProtocolVersions.V17_1;
        if(protocolVersion == 757) return ProtocolVersions.V18_0_1;
        if(protocolVersion == 758) return ProtocolVersions.V18_2;
        if(protocolVersion == 759) return ProtocolVersions.V19;
        if(protocolVersion == 760) return ProtocolVersions.V19_1_2;
        if(protocolVersion == 761) return ProtocolVersions.V19_3;
        return ProtocolVersions.V19_3;
    }

    public static ProtocolVersions getServerVersion(Server server) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        String versionSuffix = parts[parts.length - 1];
        ProtocolVersions serverVersion = null;
        if (!versionSuffix.startsWith("v")) {
            serverVersion = ProtocolVersions.V17_1;
        } else {
            serverVersion = ProtocolVersions.V8.valueOf("V" + versionSuffix.replace("v1_", "").replace("R", ""));
        }
        return serverVersion;
    }

    public static void plugmanInjection() {
        TaskManager.preformAsync(() -> {
            if(Bukkit.getPluginManager().getPlugin("PlugManX") != null) {
                try {
                    File pManConfigFile = new File("plugins/PlugManX/", "config.yml");
                    if(pManConfigFile.exists()) {
                        FileConfiguration pManConfig = YamlConfiguration.loadConfiguration(pManConfigFile);
                        List<String> a = new ArrayList<>(Arrays.asList(StringUtils.substringBetween(pManConfig.getString("ignored-plugins"), "[", "]").split(",")));
                        if(a.contains("FunctionalServerControl")) return;
                        a.add("FunctionalServerControl");
                        pManConfig.set("ignored-plugins", TextUtils.stringToMonolith("[" + String.join(",", a) + "]"));
                        pManConfig.save(pManConfigFile);
                    }
                } catch (IOException ignored) {}
            }
        });
    }

    public static void loadCachedPlayers() {
        TaskManager.preformAsync(() -> {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String randomIp = generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber();
                BaseManager.getBaseManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), randomIp);
            }
        });
    }

    public static void clearChat(CommandSender initiator, Chat.ClearType clearType, @Nullable Player player) {
        TaskManager.preformAsync(() -> {
            if(clearType == Chat.ClearType.PLAYER) {
                if(player.hasPermission("functionalservercontrol.clearchat.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.target-bypass").replace("%1$f", player.getName())));
                    return;
                }
                for(int start = 0; start < 25; start++) {
                    player.sendMessage("");
                }
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.success").replace("%1$f", player.getName())));
                player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.target-notify").replace("%1$f", initiator.getName())));
                return;
            }
            if(clearType == Chat.ClearType.ALL) {
                if(initiator.hasPermission("functionalservercontrol.clearchat.all")) {
                    for(Player target : Bukkit.getOnlinePlayers()) {
                        if(!target.hasPermission("functionalservercontrol.clearchat.bypass") || (target.hasPermission("functionalservercontrol.clearchat.bypass") && initiator.hasPermission("functionalservercontrol.bypass-break"))) {
                            for(int start = 0; start < 25; start++) {
                                target.sendMessage("");
                            }
                            target.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.target-notify").replace("%1$f", initiator.getName())));
                        }
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.all-success")));
                    return;
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.all-no-perms")));
                    return;
                }
            }
        });
    }

}
