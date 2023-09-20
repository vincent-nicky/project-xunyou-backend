package com.wsj.utils;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity {
    public static double calculateJaccardSimilarity(String strA, String strB) {
        // 将字符串转换为字符集合
//        System.out.println("原字符串："+strA);
        Set<Character> setA = new HashSet<>();
        for (char c : strA.toCharArray()) {
            setA.add(c);
//            System.out.println("拆分的字符串：" + c);
        }

        Set<Character> setB = new HashSet<>();
        for (char c : strB.toCharArray()) {
            setB.add(c);
        }

        // 计算交集和并集的大小
        Set<Character> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<Character> union = new HashSet<>(setA);
        union.addAll(setB);

        // 计算Jaccard相似度
        double result = (double) intersection.size() / union.size();

        // 格式化返回值为8位小数点
        DecimalFormat decimalFormat = new DecimalFormat("#.########");
        String formattedResult = decimalFormat.format(result);
        return Double.parseDouble(formattedResult);
    }
}
