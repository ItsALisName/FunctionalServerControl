package net.alis.functionalservercontrol.api.naf.v1_10_0.util;

import net.alis.functionalservercontrol.api.exceptions.FunctionalIdFormatException;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class FID implements Comparable<FID> {

    private String functionalId;

    private final static Pattern regexChar = Pattern.compile("[A-Z-a-z][0-9][a-zA-Z]");
    private final static Pattern regexNum = Pattern.compile("[0-9][a-z][0-9]{2}");
    private final static Pattern regexSymb = Pattern.compile("[-_][a-z][0-9][-_]");

    FID(String param, boolean unattached) {
        if(unattached) { //pfff..
            this.functionalId = param;
        }
    }

    public FID(String name) {
        if(name.length() > 16) throw new IllegalArgumentException("Failed get 'FunctionalId' from name '" + name + "' because name length bigger than 16");
        StringBuilder fid = new StringBuilder();
        int i = 0;
        for(char let : name.toCharArray()) {
            i = i + 1;
            if(i < name.length()) {
                fid.append(overrideChar(let) + "-");
            } else {
                fid.append(overrideChar(let));
            }
        }
        this.functionalId = fid.toString();
    }



    private String overrideChar(@NotNull Character letter) {
        String s = Character.toString(letter);
        if(Pattern.compile("[0-9]").matcher(s).find()) {
            return s + "x2" + s;
        } else  {
            if(Pattern.compile("[a-zA-Z]").matcher(s).find()) {
                return s + "x1" + s;
            } else {
                return s + "x3" + s;
            }
        }
    }

    public static FID random() {
        return new FID(RandomStringUtils.randomAlphabetic(4 + (int) (Math.random() * 15)));
    }

    public static @Nullable FID fromString(String string) {
        if(string != null && string.length() >= 14 && string.contains("-")) {
            for(String partOf : string.split("-")) {
                if(regexChar.matcher(partOf).find() || regexSymb.matcher(partOf).find() || regexNum.matcher(partOf).find()) {
                    continue;
                } else {
                    new FunctionalIdFormatException("Unknown format '" + string + "' for 'FunctionalId'!").printStackTrace();
                    return null;
                }
            }
            return new FID(string, true);
        }
        new FunctionalIdFormatException("Unknown format '" + string == null ? "null" : string + "' for 'FunctionalId'!").printStackTrace();
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FID && ((FID) obj).functionalId.equalsIgnoreCase(this.functionalId);
    }

    public boolean equalsIgnoreCase(String param) {
        if(param == null) {
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Cannot invoke FID#equalsIgnoreCase(String) because String is null"));
            return false;
        }
        return this.functionalId.equalsIgnoreCase(param);
    }

    @Override
    public String toString() {
        return this.functionalId;
    }

    @Override
    public int compareTo(@NotNull FID o) {
        return Integer.compare(this.functionalId.length(), o.functionalId.length());
    }
}
