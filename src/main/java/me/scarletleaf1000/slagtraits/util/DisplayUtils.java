package me.scarletleaf1000.slagtraits.util;

public class DisplayUtils {

    public static String intToRoman(int num) {
        // 1. Define corresponding values and symbols in descending order
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        // 2. Loop through each value mapping
        for (int i = 0; i < values.length; i++) {
            // Greedy approach: append the symbol while num is greater or equal
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i]; // Subtract the value from the total
            }
        }

        return roman.toString();
    }
}
