import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        double tMax = 2000;
        double stepSize = 1;
        int windowSize = 100;
        int numBins = 20;
        int numExperiments = 50;

        double logn = Math.log(windowSize);
        int degree = (int) Math.pow(windowSize/logn, 2);
        System.out.println("Grado Bernstein: " + degree);

        // Inizializzazione dei dati comuni (x, lambda_t, cdf_empirical)
        double[] xVals = null;
        double[] lambdaVals = null;
        double[] cdfEmpirical = null;
        List<double[]> bernsteinMatrix = new ArrayList<>();

        for (int exp = 1; exp <= numExperiments; exp++) {
            Sampler sampler = new Sampler(tMax, stepSize);
            List<Double> arrivals = sampler.generateArrivalTimes();
            List<Double> windowed = Sampler.extractInterarrivalWindow(arrivals, windowSize);

            double min = windowed.stream().min(Double::compare).orElse(0.0);
            double max = windowed.stream().max(Double::compare).orElse(min + 1e-3);
            Histogram hist = new Histogram(numBins, min, max);

            Queue<double[]> queue = new LinkedList<>();
            for (int i = 0; i < windowed.size(); i++) {
                queue.add(new double[]{i, windowed.get(i)});
            }
            hist.createHistogram(queue);

            double[] bins = hist.getBins();
            double[] cdf = hist.computeCDF();
            double[] fValues = BernsteinEstimator.fromCDF(cdf, bins, degree);
            BernsteinExponential estimator = new BernsteinExponential(degree, fValues);

            if (exp == 1) {
                xVals = bins;
                lambdaVals = new double[numBins];
                cdfEmpirical = cdf;
                for (int i = 0; i < numBins; i++) {
                    lambdaVals[i] = sampler.getLambda(xVals[i]);
                }
            }

            double[] bernsteinValues = new double[numBins];
            for (int i = 0; i < numBins; i++) {
                bernsteinValues[i] = estimator.evaluate(xVals[i]);
            }
            bernsteinMatrix.add(bernsteinValues);

            System.out.println(">> Esperimento " + exp + " completato.");
        }

        // Scrittura unica in CSV
        try (FileWriter writer = new FileWriter("bernstein_approx_"+tMax+"_"+windowSize+".csv")) {
            // Intestazione
            writer.write("x,lambda_t,cdf_empirical");
            for (int i = 1; i <= numExperiments; i++) {
                writer.write(",bernstein_" + i);
            }
            writer.write("\n");

            for (int i = 0; i < numBins; i++) {
                writer.write(String.format(Locale.US, "%.4f,%.4f,%.4f",
                        xVals[i], lambdaVals[i], cdfEmpirical[i]));
                for (double[] bernsteinValues : bernsteinMatrix) {
                    writer.write(String.format(Locale.US, ",%.4f", bernsteinValues[i]));
                }
                writer.write("\n");
            }
        }

        System.out.println("File creato con successo.");
    }
}