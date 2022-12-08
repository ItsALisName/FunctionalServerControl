package by.alis.functionalbans.spigot.Additional.Other;

public class TextUtils {

    public static String setColors(String inputText) {
        return inputText.replace("&", "§");
    }

    public static String getReason(String[] arg, int num){
        StringBuilder sb = new StringBuilder();
        for(int i = num; i < arg.length; i++) {
            sb.append(arg[i]).append(" ");
        }
        return sb.toString().trim();
    }

}
