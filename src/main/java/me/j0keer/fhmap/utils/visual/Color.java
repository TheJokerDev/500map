package me.j0keer.fhmap.utils.visual;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Color {
  private static final String HEX_PATTERN = "(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})";
  
  private String colorCode;
  
  private final int r;
  
  private final int g;
  
  private final int b;
  
  private final boolean valid;
  
  public static Color from(String paramString) {
    return new Color(paramString);
  }
  
  public static Color from(int paramInt1, int paramInt2, int paramInt3) {
    java.awt.Color color = new java.awt.Color(paramInt1, paramInt2, paramInt3);
    return from(Integer.toHexString(color.getRGB()).substring(2));
  }
  
  private Color(String paramString) {
    if (!paramString.matches("(#(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3}))|(([0-9A-Fa-f]){6}|([0-9A-Fa-f]){3})")) {
      this.valid = false;
      this.r = this.g = this.b = 0;
    } else {
      this.valid = true;
      this.colorCode = paramString.replace("#", "");
      if (this.colorCode.length() == 3) {
        String[] arrayOfString = this.colorCode.split("");
        this.colorCode = arrayOfString[0] + arrayOfString[0] + arrayOfString[1] + arrayOfString[1] + arrayOfString[2] + arrayOfString[2];
      } 
      java.awt.Color color = new java.awt.Color(Integer.parseInt(this.colorCode, 16));
      this.r = color.getRed();
      this.g = color.getGreen();
      this.b = color.getBlue();
    } 
  }
  
  public String getTag() {
    return "{#" + this.colorCode + "}";
  }
  
  public String getColorCode() {
    return this.colorCode;
  }
  
  public int getRed() {
    return this.r;
  }
  
  public int getGreen() {
    return this.g;
  }
  
  public int getBlue() {
    return this.b;
  }
  
  public boolean isValid() {
    return this.valid;
  }
  
  public String getAppliedTag() {
    return isValid() ? (hasHexSupport() ? ("§x"+ Arrays.stream(this.colorCode.split("")).map(paramString -> "§"+ paramString).collect(Collectors.joining())) : MinecraftColor.getClosest(this).getAppliedTag()) : "";
  }
  
  public String getColorTag() {
    return "{#" + this.colorCode + "}";
  }
  
  public static int difference(Color paramColor1, Color paramColor2) {
    return Math.abs(paramColor1.r - paramColor2.r) + Math.abs(paramColor1.g - paramColor2.g) + Math.abs(paramColor1.b - paramColor2.b);
  }
  
  public static boolean hasHexSupport() {
    return true;
  }
  
  public String toString() {
    return getAppliedTag();
  }
}
