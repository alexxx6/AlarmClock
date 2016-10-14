package com.alexxx.alarmclock.helpers;

import java.util.Collection;

public class StringUtil {

    public static String join(String delimiter, Collection list){
        if (list == null || list.isEmpty()){
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder(list.size());
        for (Object element : list) {
            stringBuilder.append(element.toString());
            stringBuilder.append(delimiter);
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }
}
