package com.wsj.utils;

import java.util.HashSet;
import java.util.Set;

public class TestJaccardSimilarity {
    public static double calculateJaccardSimilarity(String strA, String strB) {
        // 将字符串转换为字符集合
        Set<Character> setA = new HashSet<>();
        for (char c : strA.toCharArray()) {
            setA.add(c);
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
        double jaccardSimilarity = (double) intersection.size() / union.size();

        return jaccardSimilarity;
    }

    public static void main(String[] args) {
        String strA = "['java']";
        String strB = "['c++']";

        double similarity = calculateJaccardSimilarity(strA, strB);
        System.out.println("Jaccard Similarity: " + similarity);
    }
}
