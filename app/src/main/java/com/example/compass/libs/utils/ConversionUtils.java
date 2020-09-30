package com.example.compass.libs.utils;

public final class ConversionUtils {

    private ConversionUtils() {

    }

    public static void convertInchFractions(float value, int[] fractions) {

        // denominator is fixed
        int denominator = 32;
        // integer part, can be signed: 1, 0, -3,...
        int integer = (int) value;
        // numerator: always unsigned (the sign belongs to the integer part)
        // + 0.5 - rounding, nearest one: 37.9 / 64 -> 38 / 64; 38.01 / 64 -> 38 / 64
        int numerator = (int) ((Math.abs(value) - Math.abs(integer)) * denominator + 0.5);

        // some fractions, e.g. 24 / 64 can be simplified:
        // both numerator and denominator can be divided by the same number
        // since 64 = 2 ** 6 we can try 2 powers only
        // 24/64 -> 12/32 -> 6/16 -> 3/8
        // In general case (arbitrary denominator) use gcd (Greatest Common Divisor):
        // double factor = gcd(denominator, numerator);
        // denominator /= factor;
        // numerator /= factor;
        while ((numerator % 2 == 0) && (denominator % 2 == 0)) {
            numerator /= 2;
            denominator /= 2;
        }

        fractions[2] = denominator;
        fractions[1] = numerator;
        fractions[0] = (int) integer + numerator / denominator;
    }


    /**
     * Get rounded float value
     *
     * @param digit  represents decimal places
     * @param number to be rounded
     * @return rounded float number
     */
    public static float getDecimalPlaceFloat(int digit, float number) {

        switch (digit) {
            case 0:
                number = Math.round(number);
                break;
            case 1:
                number = Math.round(number * 10f) / 10f;
                break;

            case 2:
                number = Math.round(number * 100f) / 100f;
                break;
        }
        return number;
    }

    public static float convertDegreesToPercent(float degrees) {
        float percent = (float) Math.tan(degrees * Math.PI / 180);
        if (!Float.isNaN(percent)) {
            percent = percent * 100;
            return percent;
        } else {
            return 1000;
        }
    }

    public static float convertPercentToDegrees(float percent) {
        float degrees = (float) (Math.atan(percent / 100) * 180 / Math.PI);
        if (!Float.isNaN(degrees)) {
            return degrees;
        } else {
            return 90;
        }
    }
}
