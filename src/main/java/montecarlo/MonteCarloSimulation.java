package montecarlo;

import statistics.InverseStdNormalCDF;
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

        // Exécuter les réalisations initiales
        simulateNRuns(exp, initialNumberOfRuns, rnd, stat);

        // Calculer la demi-largeur initiale de l'intervalle de confiance
        double initialHalfWidth = stat.getConfidenceIntervalHalfWidth(level);

        // Si la précision souhaitée est déjà atteinte, arrêter
        if (initialHalfWidth <= maxHalfWidth) {
            return;
        }

        // Calculer le nombre total de réalisations nécessaires
        long additionalSim = calculateAdditionalSim(stat, level, maxHalfWidth, initialNumberOfRuns, additionalNumberOfRuns);

        // Exécuter les réalisations supplémentaires calculées
        simulateNRuns(exp, additionalSim, rnd, stat);

        // Répéter les simulations additionnelles jusqu'à atteindre la précision souhaitée
        while (true) {
            // Recalculer la demi-largeur de l'intervalle de confiance après les nouvelles réalisations
            double currentHalfWidth = stat.getConfidenceIntervalHalfWidth(level);

            // Vérifier si la demi-largeur est inférieure ou égale à Δmax
            if (currentHalfWidth <= maxHalfWidth) {
                break; // Précision atteinte
            }

            // Si non, effectuer un lot supplémentaire de Nadd simulations
            simulateNRuns(exp, additionalNumberOfRuns, rnd, stat);
        }
    }

    private static long calculateAdditionalSim(StatCollector stat,
                                               double level,
                                               double maxHalfWidth,
                                               long initialNumberOfRuns,
                                               long additionalNumberOfRuns) {

        // Quantile Z_{1-α/2} de la loi normale standard
        double zQuantile = InverseStdNormalCDF.getQuantile((level + (1 - level) / 2));

        // Rapport normalisé : Z_{1-α/2} * S / Δmax
        double normalizedErrorFactor = stat.getStandardDeviation() * zQuantile / maxHalfWidth;

        // Nombre total de réalisations nécessaires estimé
        double estimatedRequiredRuns = normalizedErrorFactor * normalizedErrorFactor;

        // Arrondir au multiple supérieur de Nadd et soustraire les réalisations déjà effectuées
        long roundedTotalRuns = Math.ceilDiv((long) estimatedRequiredRuns, additionalNumberOfRuns) * additionalNumberOfRuns;
        return roundedTotalRuns - initialNumberOfRuns;
    }
}
