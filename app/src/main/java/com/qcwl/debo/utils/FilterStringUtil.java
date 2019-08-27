package com.qcwl.debo.utils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/28.
 */

public class FilterStringUtil {
    /**
     * 将泛型为String类型的集合去重转化为逗号间隔字符串形式
     *
     * @author 张龙
     */
    public static String removeDuplicate(List<String> list)

    {

        if (list == null || list.size() <= 0) {
            return "";
        }

        String before = list.toString().replaceAll("^\\[| |\\]$", "");
        List<String> after = Arrays.asList(before.split(","));
        Set set = new LinkedHashSet<String>();
        set.addAll(after);
        list.clear();
        list.addAll(set);
        return list.toString().replaceAll("^\\[| |\\]$", "");
    }

}
