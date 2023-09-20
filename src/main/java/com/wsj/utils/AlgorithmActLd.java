package com.wsj.utils;
import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.List;

// 编辑距离算法
public class AlgorithmActLd {
    public static long compareUseActLd(String s1, String s2) {
        // 创建一个LevenshteinDistance对象
        EditDistance<Integer> editDistance = new LevenshteinDistance();
        // 计算两个字符串之间的编辑距离
        return (long)editDistance.apply(s1, s2);
    }

    public static long compareUseActLd(List<String> list1, List<String> list2) {
        // 创建一个LevenshteinDistance对象
        EditDistance<Integer> editDistance = new LevenshteinDistance();
        // 定义一个变量，用于存放总的编辑距离分数
        double totalDistance = 0.0;
        // 遍历两个list中的每个字符串
        for (String s1 : list1) {
            for (String s2 : list2) {
                // 计算两个字符串之间的编辑距离
                long distance = editDistance.apply(s1, s2);
                // 累加到总的编辑距离分数
                totalDistance += distance;
            }
        }
        // 计算两个list的平均编辑距离分数
//        double averageDistance = totalDistance / (list1.size() * list2.size());
        // 计算两个list的相似度分数，这里使用1 - distance / max_length的公式
//        double result = 1 - averageDistance / Math.max(list1.get(0).length(), list2.get(0).length());
        return (long)totalDistance;
    }
}
