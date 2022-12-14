package by.alis.functionalbans.spigot.Additional.Other;

import org.jetbrains.annotations.NotNull;

public class TextUtils {

    @NotNull
    public static String setColors(@NotNull String inputText) {
        return inputText.replace("&", "§");
    }

    @NotNull
    public static String getReason(@NotNull String[] arg, int num){
        StringBuilder sb = new StringBuilder();
        for(int i = num; i < arg.length; i++) {
            sb.append(arg[i]).append(" ");
        }
        return sb.toString().trim();
    }

}
