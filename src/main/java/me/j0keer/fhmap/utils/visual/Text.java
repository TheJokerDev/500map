package me.j0keer.fhmap.utils.visual;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {
  private static boolean gradientEnabled = false;
  
  private static boolean isBuneecord = false;
  
  private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("\\{#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})}");
  
  private static final Pattern HEX_GRADIENT_COLOR_PATTERN = Pattern.compile("(\\{(#[^{]*?)>})(.*?)(\\{(#.*?)<(>?)})");
  
  private static final Pattern STRIP_CHAT_COLOR_PATTERN = Pattern.compile("(§[0-9A-Fa-fK-ORk-or]|&[0-9A-Fa-fK-ORk-or])");
  
  public static String color(String paramString) {
    if (isGradientEnabled())
      paramString = gradientColor(paramString); 
    paramString = gradientColor(paramString);
    paramString = chatColor(paramString);
    return paramString;
  }
  
  public static String stripColor(String paramString) {
    return stripHexColor(stripChatColor(paramString));
  }
  
  public static String stripChatColor(String paramString) {
    return paramString.replaceAll(STRIP_CHAT_COLOR_PATTERN.pattern(), "");
  }
  
  public static String stripHexColor(String paramString) {
    return paramString.replaceAll(HEX_COLOR_PATTERN.pattern(), "");
  }
  
  public static String chatColor(String paramString) {
    Preconditions.checkArgument((paramString != null), "Text must be defined.");
    char[] arrayOfChar = paramString.toCharArray();
    for (byte b = 0; b < arrayOfChar.length - 1; b++) {
      if (arrayOfChar[b] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(arrayOfChar[b + 1]) > -1) {
        arrayOfChar[b] = '§';
        arrayOfChar[b + 1] = Character.toLowerCase(arrayOfChar[b + 1]);
      } 
    } 
    return new String(arrayOfChar);
  }
  
  public static String gradientColor(String paramString) {
    Matcher matcher = HEX_GRADIENT_COLOR_PATTERN.matcher(paramString);
    while (matcher.find()) {
      String str1 = matcher.group();
      Color color1 = Color.from(matcher.group(2).replace("#", "").replace("<", "").replace(">", ""));
      Color color2 = Color.from(matcher.group(5).replace("#", "").replace("<", "").replace(">", ""));
      if (!color1.isValid() || !color2.isValid())
        continue; 
      String str2 = matcher.group(3);
      boolean bool = !matcher.group(6).isEmpty() ? true : false;
      StringBuilder stringBuilder = new StringBuilder();
      Set<String> set = ColorCalculations.getFormats(str2);
      str2 = stripColor(str2);
      for (byte b = 0; b < str2.length(); b++) {
        char c = str2.charAt(b);
        int i = str2.length();
        i = Math.max(i, 2);
        double d = b * 100.0D / (i - 1);
        Color color = ColorCalculations.getColorInBetweenPercent(color1, color2, d);
        stringBuilder.append("{#").append(color.getColorCode()).append("}");
        if (!set.isEmpty())
          for (String str : set)
            stringBuilder.append("§").append(str);
        stringBuilder.append(c);
      } 
      if (bool)
        stringBuilder.append("{#").append(matcher.group(5).replace("#", "")).append(">").append("}"); 
      paramString = paramString.replace(str1, stringBuilder.toString());
      if (bool)
        paramString = gradientColor(paramString); 
    } 
    return hexColor(chatColor(paramString));
  }
  
  public static String hexColor(String paramString) {
    Preconditions.checkArgument((paramString != null), "Text must be defined.");
    Matcher matcher = HEX_COLOR_PATTERN.matcher(paramString);
    while (matcher.find()) {
      String str1 = matcher.group();
      String str2 = str1.replace("{", "").replace("}", "");
      if (isBungeeCord()) {
        paramString = paramString.replace(str1, BungeeColor.colorOf(str2));
        continue;
      } 
      str2 = str2.replace("#", "");
      paramString = paramString.replace(str1, Color.from(str2).getAppliedTag());
    } 
    return paramString;
  }
  
  public static String gradient(String paramString, boolean paramBoolean, Color... paramVarArgs) {
    Preconditions.checkArgument((paramVarArgs.length > 1), "Define 2 or more colors.");
    int i = Math.max(1, paramString.length() / paramVarArgs.length);
    ArrayList<Color> arrayList = new ArrayList();
    byte b = 0;
    for (Color color1 : paramVarArgs) {
      Color color2 = (paramVarArgs.length == b + 1) ? null : paramVarArgs[b + 1];
      if (color2 != null)
        arrayList.addAll(ColorCalculations.getColorsInBetween(color1, color2, i)); 
      b++;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    b = 0;
    for (char c : paramString.toCharArray()) {
      Color color = (arrayList.size() <= b) ? arrayList.get(arrayList.size() - 1) : arrayList.get(b);
      stringBuilder.append(color.getAppliedTag()).append((paramBoolean ? "§l": "") + c);
      b++;
    } 
    return stringBuilder.toString();
  }
  
  public static String firstUpperCase(String paramString) {
    return paramString.substring(0, 1).toUpperCase() + paramString.substring(1).toLowerCase();
  }
  
  public static void setEnableGradient(boolean paramBoolean) {
    gradientEnabled = paramBoolean;
  }
  
  public static boolean isGradientEnabled() {
    return gradientEnabled;
  }
  
  public static void setIsBungeeCord(boolean paramBoolean) {
    isBuneecord = paramBoolean;
  }
  
  public static boolean isBungeeCord() {
    return isBuneecord;
  }
}
