import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Lista di arrivi (come quelli che mi hai fornito)
        Sampler sampler = new Sampler(100.0, 1.0);
        List<Double> arrivals = sampler.generateArrivalTimes();

        // Calcolo degli intertempi
        List<Double> interArrivals = new LinkedList<>();
        for (int i = 1; i < arrivals.size(); i++) {
            interArrivals.add(arrivals.get(i) - arrivals.get(i - 1));
        }

        // Costruzione della coda di sample per Histogram
        Queue<double[]> samples = new LinkedList<>();
        for (int i = 0; i < interArrivals.size(); i++) {
            samples.add(new double[]{i, interArrivals.get(i)});
        }

        // Calcolo min e max per i bin
        double min = interArrivals.stream().min(Double::compare).orElse(0.0);
        double max = interArrivals.stream().max(Double::compare).orElse(min + 1e-3);

        // Istogramma
        int numBins = 50;
        Histogram hist = new Histogram(numBins, min, max);
        hist.createHistogram(samples);

        double[] bins = hist.getBins();
        int[] counts = hist.getCounts();
        double[] cdf = hist.computeCDF();
        double binWidth = hist.getBinWidth();

        // Salva CDF empirica
        try (FileWriter writer = new FileWriter("histogram_output.csv")) {
            writer.write("bin_center;count;empirical_cdf\n");
            for (int i = 0; i < bins.length; i++) {
                double center = bins[i] + binWidth / 2;
                writer.write(String.format("%.6f;%d;%.6f\n", center, counts[i], cdf[i]));
            }
            System.out.println("Salvata la CDF empirica in 'histogram_output.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bernstein estimator per CDF
        int window = bins.length;
        int degree = (int) Math.pow(window / Math.log(window), 2);;
        double[] fValues = BernsteinEstimator.fromCDF(cdf, bins, degree);
        BernsteinExponential estimator = new BernsteinExponential(degree, fValues);

        // Valuta la stima della CDF su bin_center
        try (FileWriter writer = new FileWriter("estimate_output.csv")) {
            writer.write("bin_center;bernstein_estimate\n");
            for (int i = 0; i < bins.length; i++) {
                double center = bins[i] + binWidth / 2;
                double x = center; // punto di valutazione
                double approx = estimator.evaluate(x);
                writer.write(String.format("%.6f;%.6f\n", center, approx));
            }
            System.out.println("Stima Bernstein salvata in 'estimate_output.csv'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}