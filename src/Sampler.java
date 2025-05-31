/**
 * Sampler simula un processo di arrivo stocastico in cui il tasso di arrivo 
 * \(\lambda(t)\) varia nel tempo alternando tratti costanti e tratti con pendenza finita.
 * Ogni intertempo viene campionato da una distribuzione esponenziale con parametro \(\lambda(t)\).
 * Questo modello riflette un processo localmente esponenziale ma globalmente tempo-variabile.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Sampler {

    private final Random random = new Random(42);
    private final double tMax;
    private final double stepSize;  // durata di ogni segmento flat o inclinato

    // Stato corrente
    private double lambda = 1.0;
    private boolean flat = true;  // alterna flat/slope

    /**
     * Costruisce un nuovo Sampler.
     * @param tMax tempo massimo fino a cui generare arrivi
     * @param stepSize durata in secondi di ogni segmento (piatto o inclinato)
     */
    public Sampler(double tMax, double stepSize) {
        this.tMax = tMax;
        this.stepSize = stepSize;
    }

    /**
     * Genera una lista di tempi di arrivo fino a tMax.
     * Ogni tempo rappresenta un arrivo, simulato con Exp(λ(t)).
     * @return lista d'istanti di arrivo
     */
    public List<Double> generateArrivalTimes() {
        List<Double> arrivals = new ArrayList<>();
        double t = 0.0;

        while (t < tMax) {
            double lambdaNow = getLambda(t);
            double interarrival = sampleExponential(lambdaNow);
            t += interarrival;
            if (t < tMax) {
                arrivals.add(t);
            }
        }
        return arrivals;
    }

    /**
     * Calcola il valore corrente di λ(t), che varia a tratti:
     * - tratto "piatto": λ costante
     * - tratto "inclinato": λ cresce o decresce lentamente
     * @param t istante attuale
     * @return valore corrente di λ(t)
     */
    public double getLambda(double t) {
        double segment = Math.floor(t / stepSize);
        boolean isFlat = (segment % 2 == 0);

        if (isFlat != flat) {
            flat = isFlat;
            if (flat) {
                // tiene lambda attuale
            } else {
                // cambia lambda leggermente
                double slope = random.nextDouble() * 0.04 - 0.02; // [-0.02, +0.02]
                lambda += slope * stepSize;
                lambda = Math.max(0.1, Math.min(lambda, 3.0));
            }
        }
        return lambda;
    }

    /**
     * Campiona un valore da una distribuzione esponenziale Exp(λ)
     * @param rate parametro λ dell'esponenziale
     * @return un campione positivo
     */
    private double sampleExponential(double rate) {
        return -Math.log(1.0 - random.nextDouble()) / rate;
    }

    /**
     * Estrae gli ultimi intertempi da una lista di tempi di arrivo.
     * @param arrivals lista dei tempi di arrivo
     * @param windowSize numero di intertempi da considerare (W)
     * @return lista con gli ultimi W intertempi
     */
    public static List<Double> extractInterarrivalWindow(List<Double> arrivals, int windowSize) {
        List<Double> interarrivals = new ArrayList<>();
        for (int i = 1; i < arrivals.size(); i++) {
            interarrivals.add(arrivals.get(i) - arrivals.get(i - 1));
        }
        int fromIndex = Math.max(0, interarrivals.size() - windowSize);
        return interarrivals.subList(fromIndex, interarrivals.size());
    }

    /**
     * Metodo di test: genera e stampa i tempi di arrivo simulati.
     * @param args non utilizzato
     */
    public static void main(String[] args) {
        Sampler sampler = new Sampler(100.0, 10.0);
        List<Double> arrivals = sampler.generateArrivalTimes();

        // Stampa i tempi di arrivo
        for (double t : arrivals) {
            System.out.printf("%.4f\n", t);
        }
    }
}