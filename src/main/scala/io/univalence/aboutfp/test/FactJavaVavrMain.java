package io.univalence.aboutfp.test;

import io.vavr.Function1;
import io.vavr.collection.*;

public class FactJavaVavrMain {

    // Approche impérative
    public static int fact_ip(int n) {
        int result = 1;

        for (int i = 1; i <= n; i++) {
            result *= i;
        }

        return result;
    }

    // Approche récursive
    public static int fact_fp1(int n) {
        if (n <= 1)
            return 1;
        else
            return n * fact_fp1(n - 1);
    }

    // Utilisation de foldLeft
    public static int fact_fp2(int n) {
        return List.rangeClosed(1, n)
                .foldLeft(
                        1,
                        (prod, i) -> prod * i);
    }

    // Utilisation de product
    public static int fact_fp3(int n) {
        return List.rangeClosed(1, n)
                .product()
                .intValue();
    }

    // Utilisation de product avec un Iterator
    public static int fact_fp4(int n) {
        return Iterator.from(1).take(n)
                .product()
                .intValue();
    }

    // flux de résultats de factoriel (corécursion ou codata)
    public static int fact_fp5(int n) {
        return factStream
                .drop(n)
                .head();
    }

    private static Stream<Integer> factStream =
            Stream.from(1).scanLeft(1, (acc, n) -> acc * n);

    // ================================================================================

    public static void main(String[] args) {
        Map<String, Function1<Integer, Integer>> functions =
                LinkedHashMap.of(
                        "ip", FactJavaVavrMain::fact_ip,
                        "fp1", FactJavaVavrMain::fact_fp1,
                        "fp2", FactJavaVavrMain::fact_fp2,
                        "fp3", FactJavaVavrMain::fact_fp3,
                        "fp4", FactJavaVavrMain::fact_fp4,
                        "fp5", FactJavaVavrMain::fact_fp5
                );

        displayResult(functions, 10, 10);

        System.out.println();
        System.out.println("fact stream: "
                + factStream
                .take(10)
                .mkString(", "));
    }

    public static void displayResult(Map<String, Function1<Integer, Integer>> functions, int n, int padding) {
        String header =
                functions.keySet()
                        .map(s -> leftPad(s, ' ', padding))
                        .foldLeft("", (acc, s) -> acc + s);

        System.out.println(rightPad("", ' ', padding) + header);
        for (int i = 0; i <= n; i++) {
            System.out.println(getResult(i, functions.values(), padding));
        }
    }

    public static String getResult(int n, Seq<Function1<Integer, Integer>> functions, int padding) {
        return functions
                .map(f -> leftPad(String.valueOf(f.apply(n)), ' ', padding))
                .foldLeft(rightPad(n + ":", ' ', padding), (acc, s) -> acc + s);
    }

    public static String rightPad(String s, char c, int n) {
        String result = s;

        while (result.length() < n) {
            result += c;
        }

        return result;
    }

    public static String leftPad(String s, char c, int n) {
        String result = s;

        while (result.length() < n) {
            result = c + result;
        }

        return result;
    }

}
