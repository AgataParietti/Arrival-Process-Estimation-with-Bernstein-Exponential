import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 * Classe di utilità per calcolare i coefficienti f(-log(i/n))
 * necessari all'approssimazione di una densità tramite Bernstein Exponential.
 */
public class BernsteinEstimator {

    /**
     * Estrae i coefficienti f(-log(i/n)) da un istogramma dato, per grado n.
     *
     * @param hist istogramma con bin e densità stimata
     * @param degree grado n dello stimatore BE_n
     * @return array di double con f(-log(i/n)) per i = 0..n
     */
    public static double[] fromHistogram(Histogram hist, int degree) {
        double[] fValues = new double[degree + 1];

        // Normalizza i conteggi in modo da ottenere una densità
        int[] counts = hist.getCounts();
        double[] bins = hist.getBins();
        double binWidth = hist.getBinWidth();
        int totalSamples = IntStream.of(counts).sum();

        double[] density = new double[counts.length];
        for (int i = 0; i < counts.length; i++) {
            density[i] = counts[i] / (totalSamples * binWidth);
        }

        // Calcola f(-log(i/n)) per i = 0..n
        for (int i = 0; i <= degree; i++) {
            if (i == 0) {
                fValues[i] = density[0];
                continue;
            }
            double x = -Math.log((double) i / degree);
            int binIndex = (int) ((x - bins[0]) / binWidth);
            if (binIndex >= 0 && binIndex < density.length) {
                fValues[i] = density[binIndex];
            } else {
                fValues[i] = 0.0;
            }
        }

        return fValues;
    }

    public static double[] fromCDF(double[] cdf, double[] bins, int degree) {
        double binWidth = bins[1] - bins[0];
        double[] fValues = new double[degree + 1];

        for (int i = 0; i <= degree; i++) {
            if (i == 0) {
                fValues[i] = cdf[0];
                continue;
            }
            double x = -Math.log((double) i / degree);
            int binIndex = (int) ((x - bins[0]) / binWidth);
            if (binIndex >= 0 && binIndex < cdf.length) {
                fValues[i] = cdf[binIndex];
            } else {
                fValues[i] = 1.0;
            }
        }
        return fValues;
    }
}