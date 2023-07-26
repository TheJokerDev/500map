package me.j0keer.fhmap.utils.visual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorCalculations {
  private static final Pattern FORMAT_PATTERN = Pattern.compile("(&[klmnorKLMNOR])");
  
  public static List<Color> getColorsInBetween(Color paramColor1, Color paramColor2, int paramInt) {
    double d1 = (paramColor2.getRed() - paramColor1.getRed()) / paramInt;
    double d2 = (paramColor2.getGreen() - paramColor1.getGreen()) / paramInt;
    double d3 = (paramColor2.getBlue() - paramColor1.getBlue()) / paramInt;
    ArrayList<Color> arrayList = new ArrayList();
    for (byte b = 1; b <= paramInt; b++) {
      int i = (int)Math.round(paramColor1.getRed() + d1 * b);
      int j = (int)Math.round(paramColor1.getGreen() + d2 * b);
      int k = (int)Math.round(paramColor1.getBlue() + d3 * b);
      Color color = Color.from(i, j, k);
      arrayList.add(color);
    } 
    return arrayList;
  }
  
  public static Color getColorInBetweenPercent(Color paramColor1, Color paramColor2, double paramDouble) {
    paramDouble /= 100.0D;
    double d = 1.0D - paramDouble;
    int i = (int)(paramColor2.getRed() * paramDouble + paramColor1.getRed() * d);
    int j = (int)(paramColor2.getGreen() * paramDouble + paramColor1.getGreen() * d);
    int k = (int)(paramColor2.getBlue() * paramDouble + paramColor1.getBlue() * d);
    return Color.from(i, j, k);
  }
  
  public static Set<String> getFormats(String paramString) {
    paramString = paramString.replace("§", "&");
    HashSet<String> hashSet = new HashSet();
    Matcher matcher = FORMAT_PATTERN.matcher(paramString);
    while (matcher.find())
      hashSet.add(matcher.group()); 
    return hashSet;
  }
}
