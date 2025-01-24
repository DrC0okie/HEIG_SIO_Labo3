package montecarlo;

import statistics.StatCollector;

import java.util.Random;

/**
 * This class provides methods for simple Monte Carlo simulations.
 */
public class MonteCarloSimulation {
    /**
     * Private constructor. Makes it impossible to instantiate.
     */
    private MonteCarloSimulation() {
    }

    /**
     * Simulates experiment exp n times, using rnd as a source of pseudo-random numbers and collect
     * the results in stat.
     *
     * @param exp  experiment to be run each time
     * @param n    number of runs to be performed
     * @param rnd  random source to be used to simulate the experiment
     * @param stat collector to be used to collect the results of each experiment
     */
    public static void simulateNRuns(Experiment exp, long n, Random rnd, StatCollector stat) {
        for (long run = 0; run < n; ++run) {
            stat.add(exp.execute(rnd));
        }
    }

    /**
     * First simulates experiment exp initialNumberOfRuns times, then estimates the number of runs
     * needed for a 95% confidence interval half width no more than maxHalfWidth. If final C.I. is
     * too wide, simulates additionalNumberOfRuns before recalculating the C.I. and repeats the process
     * as many times as needed.
     * <p>
     * Uses rnd as a source of pseudo-random numbers and collects the results in stat.
     *
     * @param exp                    experiment to be run each time
     * @param level                  confidence level of the confidence interval
     * @param maxHalfWidth           maximal half width of the confidence interval
     * @param initialNumberOfRuns    initial number of runs to be performed
     * @param additionalNumberOfRuns additional number of runs to be performed if C.I. is too wide
     * @param rnd                    random source to be used to simulate the experiment
     * @param stat                   collector to be used to collect the results of each experiment
     */
    public static void simulateTillGivenCIHalfWidth(Experiment exp,
                                                    double level,
                                                    double maxHalfWidth,
                                                    long initialNumberOfRuns,
                                                    long additionalNumberOfRuns,
                                                    Random rnd,
                                                    StatCollector stat) {
        // Effectuer les simulations initiales
        simulateNRuns(exp, initialNumberOfRuns, rnd, stat);

        // Boucle jusqu'à ce que la demi-largeur respecte maxHalfWidth
        while (true) {
            // Calculer la demi-largeur de l'intervalle de confiance
            double halfWidth = stat.getConfidenceIntervalHalfWidth(level);

            // Si la condition est respectée, arrêter
            if (halfWidth <= maxHalfWidth) {
                break;
            }

            // Sinon, effectuer des simulations supplémentaires
            simulateNRuns(exp, additionalNumberOfRuns, rnd, stat);
        }
    }
}
