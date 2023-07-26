package me.j0keer.fhmap.utils.visual;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

public class Animation {
  private static final HashMap<String, List<String>> animationCache = new HashMap<>();
  
  public static String wave(String paramString, Color... paramVarArgs) {
    return wave(paramString, true, 5, 10, paramVarArgs);
  }
  public static String wave(String paramString, boolean bold, Color... paramVarArgs) {
    return wave(paramString, bold, 5, 10, paramVarArgs);
  }
  
  public static String wave(String paramString, boolean paramBoolean, int paramInt1, int paramInt2, Color... paramVarArgs) {
    Preconditions.checkArgument((paramVarArgs.length > 1), "Not enough colors provided");
    String str = "wave-" + paramString + "-" + paramBoolean + "-" + paramInt1 + "-" + paramInt2 + "-" + (String)Arrays.<Color>stream(paramVarArgs).map(Color::getColorCode).collect(Collectors.joining("-"));
    if (animationCache.containsKey(str))
      return currentFrame(animationCache.get(str)); 
    ArrayList<String> arrayList = new ArrayList();
    byte b = 0;
    for (Color color1 : paramVarArgs) {
      Color color2 = paramVarArgs[(paramVarArgs.length == b + 1) ? 0 : (b + 1)];
      arrayList.addAll(Collections.nCopies(paramInt1, color1.getAppliedTag() + (paramBoolean ? "§l": "") + paramString));
      ArrayList<String> arrayList1 = new ArrayList();
      arrayList1.addAll(Collections.nCopies(paramString.length(), color1.getAppliedTag()));
      arrayList1.addAll(ColorCalculations.getColorsInBetween(color1, color2, paramInt2).stream().map(Color::getAppliedTag).collect(Collectors.toList()));
      arrayList1.addAll(Collections.nCopies(paramString.length(), color2.getAppliedTag()));
      for (byte b1 = 0; b1 <= arrayList1.size() - paramString.length(); b1++) {
        StringBuilder stringBuilder = new StringBuilder();
        byte b2 = 0;
        for (char c : paramString.toCharArray()) {
          String str1 = arrayList1.get(b2 + b1);
          stringBuilder.append(str1).append(paramBoolean ? "§l": "").append(c);
          b2++;
        } 
        arrayList.add(stringBuilder.toString());
      } 
      arrayList.addAll(Collections.nCopies(paramInt1, color2.getAppliedTag() + (paramBoolean ? "§l": "") + paramString));
      b++;
    } 
    animationCache.put(str, arrayList);
    return currentFrame(arrayList);
  }
  
  public static String fading(String paramString, Color... paramVarArgs) {
    return fading(paramString, true, 10, 20, paramVarArgs);
  }
  
  public static String fading(String paramString, boolean paramBoolean, int paramInt1, int paramInt2, Color... paramVarArgs) {
    Preconditions.checkArgument((paramVarArgs.length > 1), "Not enough colors provided");
    String str = "fading-" + paramString + "-" + paramBoolean + "-" + paramInt1 + "-" + paramInt2 + "-" + (String)Arrays.<Color>stream(paramVarArgs).map(Color::getColorCode).collect(Collectors.joining("-"));
    if (animationCache.containsKey(str))
      return currentFrame(animationCache.get(str)); 
    ArrayList<String> arrayList = new ArrayList();
    byte b = 0;
    for (Color color1 : paramVarArgs) {
      Color color2 = paramVarArgs[(paramVarArgs.length == b + 1) ? 0 : (b + 1)];
      arrayList.addAll(Collections.nCopies(paramInt1, color1.getAppliedTag() + (paramBoolean ? "§l": "") + paramString));
      for (Color color : ColorCalculations.getColorsInBetween(color1, color2, paramInt2))
        arrayList.add(color.getAppliedTag() + (paramBoolean ? "§l": "") + paramString);
      b++;
    } 
    animationCache.put(str, arrayList);
    return currentFrame(arrayList);
  }
  
  private static String currentFrame(List<String> paramList) {
    long l = System.currentTimeMillis() / 50L;
    int i = (int)(l % paramList.size());
    return paramList.get(i);
  }
}
