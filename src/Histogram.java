import java.util.Arrays;
import java.util.Queue;
import java.util.stream.IntStream;

/**
 * Classe che costruisce un istogramma da un insieme di campioni.
 * Permette anche di ottenere la funzione di distribuzione cumulativa (CDF).
 */
public class Histogram {

    private double[] bins;
    private int[] counts;
    private double binWidth;

    /**
     * Costruttore dell'istogramma.
     * @param numBins numero di bin
     * @param min valore minimo del range
     * @param max valore massimo del range
     */
    public Histogram(int numBins, double min, double max) {
        bins = new double[numBins];
        counts = new int[numBins];
        binWidth = (max - min) / numBins;

        for (int i = 0; i < numBins; i++) {
            bins[i] = min + i * binWidth;
        }
    }

    /**
     * Crea l'istogramma dai campioni forniti.
     * I campioni sono double[] ma viene usata solo la componente sample[1] come valore.
     * @param samples coda di campioni
     */
    public void createHistogram(Queue<double[]> samples) {
        Arrays.fill(counts, 0); // Resetta i contatori

        for (double[] sample : samples) {
            double value = sample[1];
            int binIndex = (int) ((value - bins[0]) / binWidth);
            if (binIndex >= 0 && binIndex < bins.length) {
                counts[binIndex]++;
            }
        }
    }

    /**
     * Calcola la CDF a partire dai conteggi dei bin.
     * @return array con valori cumulativi normalizzati
     */
    public double[] computeCDF() {
        double[] cdf = new double[counts.length];
        int totalSamples = IntStream.of(counts).sum();

        int cumulativeCount = 0;
        for (int i = 0; i < counts.length; i++) {
            cumulativeCount += counts[i];
            cdf[i] = (double) cumulativeCount / totalSamples;
        }
        return cdf;
    }

    public double[] getBins() {
        return bins;
    }

    public int[] getCounts() {
        return counts;
    }

    public double getBinWidth() {
        return binWidth;
    }
}
