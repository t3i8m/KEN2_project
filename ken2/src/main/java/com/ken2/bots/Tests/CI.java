package com.ken2.bots.Tests;

import java.util.Arrays;

public class CI {

    // Метод для вычисления доверительного интервала
    public static double[] calculateConfidenceInterval(double[] data, double confidenceLevel) {
        int n = data.length;
        double mean = Arrays.stream(data).average().orElse(0.0);
        double stdDev = Math.sqrt(Arrays.stream(data)
                .map(x -> Math.pow(x - mean, 2))
                .sum() / (n - 1));
        double stdError = stdDev / Math.sqrt(n);

        // Получаем Z-значение для нормального распределения
        double zScore = getZScore(confidenceLevel);
        double marginOfError = zScore * stdError;

        return new double[]{mean - marginOfError, mean + marginOfError};
    }

    // Получение Z-оценки для различных уровней доверия (90%, 95%, 99%)
    public static double getZScore(double confidenceLevel) {
        if (confidenceLevel == 0.90) {
            return 1.645;
        } else if (confidenceLevel == 0.95) {
            return 1.960;
        } else if (confidenceLevel == 0.99) {
            return 2.576;
        } else {
            throw new IllegalArgumentException("Confidence level not supported");
        }
    }

    public static void main(String[] args) {
        // Тестовые данные
        double[] winRates = {0.96, 0.96,0.94, 0.96,  0.96};//rule based(white) vs ab(black)
        double[] winRates2 = {0.91,0.98, 0.94, 0.98, 0.97};//rule based(black) vs ab(white)
        double[] winRates3 = {0.98, 0.97, 0.97, 0.97, 0.98};//alphabetaOld(white) vs ab(black)


        //depth = 3 , win rate =0.65

//        double[] largeSample = generateRandomData(1000, 50, 10);
//        double[] outliersSample = {10, 20, 30, 1000, 2000};

        // Ручное тестирование различных выборок
        System.out.println("Testing small sample:");
        testSample(winRates3, 0.95);

//        System.out.println("\nTesting large sample:");
//        testSample(largeSample, 0.95);
//
//        System.out.println("\nTesting sample with outliers:");
//        testSample(outliersSample, 0.95);
    }

    // Метод для тестирования и вывода результатов
    private static void testSample(double[] sample, double confidenceLevel) {
        double[] interval = calculateConfidenceInterval(sample, confidenceLevel);
        System.out.println("Confidence Interval: [" + interval[0] + ", " + interval[1] + "]");
    }

    // Генерация случайных данных с нормальным распределением
    public static double[] generateRandomData(int size, double mean, double stdDev) {
        double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = mean + stdDev * Math.random();
        }
        return data;
    }
}
