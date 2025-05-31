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
                fValues[i] = density[0]; // x = inf, assume primo valore
                continue;
            }
            double x = -Math.log((double) i / degree);
            int binIndex = (int) ((x - bins[0]) / binWidth);
            if (binIndex >= 0 && binIndex < density.length) {
                fValues[i] = density[binIndex];
            } else {
                fValues[i] = 0.0; // fuori dal supporto osservato
            }
        }

        return fValues;
    }

    public static double[] fromCDF(double[] cdf, double[] bins, int degree) {
        double binWidth = bins[1] - bins[0]; // assumiamo bin equispaziati
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
                fValues[i] = 1.0; // La CDF tende a 1
            }
        }
        return fValues;
    }

    /**
     * Metodo completo che riceve una lista di tempi di arrivo, una dimensione della finestra e un grado,
     * e restituisce una funzione BernsteinExponential approssimata dagli ultimi intertempi.
     *
     * @param arrivals lista di arrivi (time points)
     * @param windowSize numero d'intertempi recenti da considerare
     * @param degree grado dello stimatore
     * @param numBins numero di bin da usare per costruire l'istogramma
     * @return oggetto BernsteinExponential approssimante
     */
    public static BernsteinExponential buildEstimator(List<Double> arrivals, int windowSize, int degree, int numBins) {
        if (arrivals.size() < windowSize + 1) {
            throw new IllegalArgumentException("Non ci sono abbastanza campioni per la finestra richiesta.");
        }

        List<Double> interArrivals = new LinkedList<>();
        int start = arrivals.size() - windowSize - 1;
        for (int i = start + 1; i < arrivals.size(); i++) {
            interArrivals.add(arrivals.get(i) - arrivals.get(i - 1));
        }

        double min = interArrivals.stream().min(Double::compare).orElse(0.0);
        double max = interArrivals.stream().max(Double::compare).orElse(min + 1e-3);

        Queue<double[]> queue = new LinkedList<>();
        for (int i = 0; i < interArrivals.size(); i++) {
            queue.add(new double[] {i, interArrivals.get(i)});
        }

        Histogram hist = new Histogram(numBins, min, max);
        hist.createHistogram(queue);
        double[] fValues = fromHistogram(hist, degree);

        return new BernsteinExponential(degree, fValues);
    }
}