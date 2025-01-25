import montecarlo.*;
import statistics.*;

import java.util.Random;

/**
 * Classe principale pour exécuter les simulations de Monte Carlo sur le paradoxe des anniversaires.
 * <p>
 * Cette classe contient trois simulations principales :
 * <ul>
 *     <li>Simulation 1 : Estimation de la probabilité pour un groupe donné.</li>
 *     <li>Simulation 2 : Étude du seuil de couverture des intervalles de confiance.</li>
 *     <li>Simulation 3 : Détermination du nombre minimal de personnes nécessaires pour une probabilité donnée.</li>
 * </ul>
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public class Main {

    private static final long SEED = 0x134D6EE;
    private static final int DAYS_IN_YEAR = 365;
    private static final String SPACING = "  %-30s : ";

    /**
     * Exécute les trois simulations dans l'ordre et affiche les résultats sur la console.
     */
    public static void main(String[] args) {

        System.out.println("=== Simulation 1 : Estimation de p23 ===");
        runFirstSimulation();

        System.out.println("\n=== Simulation 2 : Seuil de couverture des intervalles ===");
        runSecondSimulation();

        System.out.println("\n=== Simulation 3 : Recherche de K minimal ===");
        runThirdSimulation();
    }

    /**
     * Simulation 1 : Estimation de la probabilité que deux personnes partagent la même date d'anniversaire
     * dans un groupe de 23 personnes, avec des niveaux de précision décroissants.
     * Cette méthode effectue trois itérations successives, en divisant par deux la demi-largeur de
     * l'intervalle de confiance à chaque itération.
     */
    private static void runFirstSimulation() {
        int K = 23;                     // Taille du groupe
        int M = 2;                      // Minimum d'occurrences
        double confidenceLevel = 0.95;  // Niveau de confiance
        double deltaMax = 1e-4;         // Demi-largeur maximale
        long initialRuns = 1_000_000;   // Nombre initial de réalisations
        long additionalRuns = 100_000;  // Nombre de réalisations supplémentaires

        Random random = new Random(SEED);
        BirthdayExperiment experiment = new BirthdayExperiment(K, DAYS_IN_YEAR, M);

        // Répéter la simulation pour différentes demi-largeurs
        for (int i = 0; i < 3; i++) {
            StatCollector stat = new StatCollector();

            long startTime = System.nanoTime();
            // Simuler jusqu'à atteindre la demi-largeur maximale
            MonteCarloSimulation.simulateTillGivenCIHalfWidth(
                    experiment,
                    confidenceLevel,
                    deltaMax,
                    initialRuns,
                    additionalRuns,
                    random,
                    stat
            );
            long endTime = System.nanoTime();

            // Récupérer les résultats
            double estimatedP = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(confidenceLevel);
            long totalRuns = stat.getNumberOfObs();

            // Afficher les résultats formatés
            System.out.printf("Itération %d :%n", i + 1);
            System.out.printf(SPACING + "%.6f%n", "Probabilité estimée", estimatedP);
            System.out.printf(SPACING + "%.6f%n", "Demi-largeur de l'intervalle", halfWidth);
            System.out.printf(SPACING + "[%.6f, %.6f]%n", "Intervalle de confiance",
                    estimatedP - halfWidth,
                    estimatedP + halfWidth);
            System.out.printf(SPACING + "%d%n", "Nombre total de simulations", totalRuns);
            printFormattedTime(startTime, endTime);

            // Diviser la demi-largeur pour l'itération suivante
            deltaMax /= 2;
        }
    }

    /**
     * Simulation 2 : Étude du seuil de couverture des intervalles de confiance.
     * Cette méthode génère 1'000 intervalles de confiance pour des simulations de Monte Carlo,
     * chacune comportant 1'000'000 réalisations.
     */
    private static void runSecondSimulation() {
        int K = 23;                     // Taille du groupe
        int M = 2;                      // Minimum d'occurrences
        double confidenceLevel = 0.95;  // Niveau de confiance
        long sampleSize = 1_000_000;    // Taille de l'échantillon
        int repetitions = 1000;         // Nombre de répétitions
        double p23 = 0.5072972343;      // Valeur théorique de p23

        Random random = new Random(SEED);
        BirthdayExperiment experiment = new BirthdayExperiment(K, DAYS_IN_YEAR, M);

        int intervalsContainingP = 0; // Compteur d'intervalles contenant p23

        long startTime = System.nanoTime();
        for (int i = 0; i < repetitions; i++) {

            StatCollector stat = new StatCollector();

            // Effectuer N = 10^6 simulations
            MonteCarloSimulation.simulateNRuns(experiment, sampleSize, random, stat);

            // Calculer les statistiques
            double estimatedP = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(confidenceLevel);
            double lowerBound = estimatedP - halfWidth;
            double upperBound = estimatedP + halfWidth;

            // Vérifier si l'intervalle contient p23
            if (p23 >= lowerBound && p23 <= upperBound) {
                intervalsContainingP++;
            }
        }
        long endTime = System.nanoTime();

        // Calcul du seuil empirique de couverture
        double coverage = (double) intervalsContainingP / repetitions;

        // Calcul de l'intervalle de confiance pour la couverture
        double z = 1.96; // Quantile pour un niveau de confiance de 95 %
        double marginOfError = z * Math.sqrt(coverage * (1 - coverage) / repetitions);
        double lowerCoverageBound = coverage - marginOfError;
        double upperCoverageBound = coverage + marginOfError;

        // Afficher les résultats
        System.out.printf(SPACING + "%.4f%n", "Seuil empirique de couverture", coverage);
        System.out.printf(SPACING + "[%.4f, %.4f]%n", "Ic pour la couverture",
                lowerCoverageBound, upperCoverageBound);
        printFormattedTime(startTime, endTime);
    }

    /**
     * Simulation 3 : Détermination du nombre minimal de personnes nécessaires pour qu'au moins trois
     * personnes partagent la même date d'anniversaire avec une probabilité supérieure à 0.5.
     * Cette méthode explore des tailles de groupes entre 80 et 100, effectuant 1'000'000 réalisations
     * pour chaque taille de groupe. Elle affiche les résultats pour chaque taille et s'arrête dès que
     * la probabilité dépasse 0.5.
     */
    public static void runThirdSimulation() {
        int M = 3;                      // Minimum d'occurrences
        double confidenceLevel = 0.95;  // Niveau de confiance
        long sampleSize = 1_000_000;    // Taille de l'échantillon
        int startK = 80;                // Taille minimale du groupe
        int endK = 100;                 // Taille maximale du groupe

        // Réinitialiser le générateur pseudo-aléatoire
        Random random = new Random(SEED);

        long startTime = System.nanoTime();

        // Parcourir les tailles de groupes entre 80 et 100
        for (int K = startK; K <= endK; K++) {
            StatCollector stat = new StatCollector();

            // Simuler N = 10^6 réalisations
            MonteCarloSimulation.simulateNRuns(new BirthdayExperiment(K, DAYS_IN_YEAR, M), sampleSize, random, stat);

            // Calculer les statistiques
            double estimatedP = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(confidenceLevel);
            double lowerBound = estimatedP - halfWidth;
            double upperBound = estimatedP + halfWidth;

            // Afficher les résultats
            System.out.printf("Pour K = %d :%n", K);
            System.out.printf(SPACING + "%.6f%n", "Probabilité estimée", estimatedP);
            System.out.printf(SPACING + "[%.6f, %.6f]%n", "Intervalle de confiance", lowerBound, upperBound);

            // Vérifier si la probabilité dépasse 0.5
            if (estimatedP > 0.5) {
                System.out.printf("%n  ** K minimal trouvé : %d **%n", K);
                break; // Arrêter la boucle dès que le K minimal est trouvé
            }
        }
        long endTime = System.nanoTime();
        printFormattedTime(startTime, endTime);
    }

    /**
     * Affiche le temps d'exécution de manière formattée sur la console
     * @param startTimeNs le temps en nanosecondes lorsque l'exécution a commencé.
     * @param endTimeNs   le temps en nanosecondes lorsque l'exécution s'est terminée.
     */
    private static void printFormattedTime(long startTimeNs, long endTimeNs) {
        long durationInNanos = endTimeNs - startTimeNs;
        long durationInSeconds = durationInNanos / 1_000_000_000;
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;

        String msg = "Temps d'exécution";
        if (minutes > 0) {
            System.out.printf(SPACING + "%d min %d s%n%n", msg, minutes, seconds);
        } else {
            System.out.printf(SPACING + "%d s%n%n", msg, seconds);
        }
    }
}
