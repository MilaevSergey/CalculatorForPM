package ua.myapps.calculatorforpm;


public class CalcOperations {

    public static double addition(double a, double b) {
        return a + b;
    }

    public static double substraction(double a, double b) {
        return a - b;
    }

    public static double multiplication(double a, double b) {
        return a * b;
    }

    public static double division(double a, double b) {
        if (b == 0) {
            throw new DivisionByZeroException();
        }
        return a / b;
    }

}
