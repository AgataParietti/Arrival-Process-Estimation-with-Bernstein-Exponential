/**
 * Classe che rappresenta una funzione approssimata utilizzando gli esponenziali di Bernstein.
 * L'approssimazione si basa su una funzione discreta f(x) stimata da un istogramma
 * costruito su una finestra di campioni.
 */

import java.util.List;

public class BernsteinExponential {

    private final int degree;
    private final double[] fValues; // f(-log(i / n)) per i = 0..n

    /**
     * Costruisce un'approssimazione BE_n f(x) a partire da una funzione discreta
     * f(-log(i/n)) stimata da una finestra di campioni.
     *
     * @param degree grado del polinomio di Bernstein (n)
     * @param fValues valori della funzione f(-log(i/n)) precomputati (lunghezza n+1)
     */
    public BernsteinExponential(int degree, double[] fValues) {
        if (fValues.length != degree + 1) {
            throw new IllegalArgumentException("fValues deve avere dimensione n+1");
        }
        this.degree = degree;
        this.fValues = fValues;
    }

    /**
     * Valuta l'approssimazione BE_n f(x) per un dato x >= 0.
     * @param x punto in cui valutare la funzione approssimata
     * @return valore approssimato della funzione f in x
     */
    public double evaluate(double x) {
        double sum = 0.0;
        double expNegX = Math.exp(-x);
        double oneMinusExpNegX = 1.0 - expNegX;

        for (int i = 0; i <= degree; i++) {
            double binCoeff = binomialCoefficient(degree, i);
            double term = fValues[i] * binCoeff * Math.pow(expNegX, i) * Math.pow(oneMinusExpNegX, degree - i);
            sum += term;
        }
        return sum;
    }

    /**
     * Calcola il coefficiente binomiale (n su k).
     */
    private static double binomialCoefficient(int n, int k) {
        double res = 1.0;
        for (int i = 1; i <= k; i++) {
            res *= (n - (k - i));
            res /= i;
        }
        return res;
    }

    public double[] getfValues() {
        return fValues;
    }
}
