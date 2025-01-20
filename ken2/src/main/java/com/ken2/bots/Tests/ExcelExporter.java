package com.ken2.bots.Tests;

import java.io.FileWriter;
import java.io.IOException;

public class ExcelExporter {
    public static void saveResultsToCSV(double mean, double lowerBound, double upperBound) {
        try (FileWriter writer = new FileWriter("win_rate_results.csv")) {
            writer.append("Metric,Value\n");
            writer.append("Mean,").append(String.valueOf(mean * 100)).append("\n");
            writer.append("Lower Bound,").append(String.valueOf(lowerBound * 100)).append("\n");
            writer.append("Upper Bound,").append(String.valueOf(upperBound * 100)).append("\n");
            System.out.println("Results saved to win_rate_results.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        double mean = 0.974;
        double lowerBound = 0.969199000104145;
        double upperBound =  0.978800999895855;

        saveResultsToCSV(mean, lowerBound, upperBound);
    }
}
