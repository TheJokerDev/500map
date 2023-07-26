package me.j0keer.fhmap.utils.visual;

import java.util.ArrayList;
import java.util.List;

public class LoreScroller {
  public static List<String> scroller(List<String> paramList, int paramInt, long paramLong) {
    if (paramList.size() <= paramInt)
      return paramList; 
    ArrayList<String> arrayList = new ArrayList();
    arrayList.addAll(paramList);
    arrayList.addAll(paramList);
    arrayList.addAll(paramList);
    long l = System.currentTimeMillis() - paramLong;
    int i = Math.round((float)(l / 150L)) % paramList.size();
    return arrayList.subList(i, i + paramInt);
  }
}
