import montecarlo.*;
import statistics.*;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
//        System.out.println("=== Simulation 1 : Estimation de p23 ===");
//        runFirstSimulation();
//
        System.out.println("\n=== Simulation 2 : Seuil de couverture des intervalles ===");
        runSecondSimulation();

//        System.out.println("\n=== Simulation 3 : (À définir dans l'étape suivante) ===");
//        runThirdSimulation();
    }

    /**
     * Première simulation : estimation de 23 avec différents niveaux de précision.
     */
    private static void runFirstSimulation() {
        int K = 23;
        int Y = 365;
        int M = 2;
        double level = 0.95;
        double maxHalfWidth = 1e-4;
        long initialRuns = 1_000_000;
        long additionalRuns = 100_000;
        long seed = 0x134D6EE;

        Random random = new Random(seed);
        BirthdayExperiment experiment = new BirthdayExperiment(K, Y, M);

        // Répéter la simulation pour différentes demi-largeurs
        for (int i = 0; i < 3; i++) {
            StatCollector stat = new StatCollector();

            // Simuler jusqu'à atteindre la demi-largeur maximale
            MonteCarloSimulation.simulateTillGivenCIHalfWidth(
                    experiment,
                    level,
                    maxHalfWidth,
                    initialRuns,
                    additionalRuns,
                    random,
                    stat
            );

            // Récupérer les résultats
            double estimatedProbability = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(level);
            long totalRuns = stat.getNumberOfObs();

            // Afficher les résultats
            System.out.printf("Itération %d :%n", i + 1);
            System.out.printf("  Probabilité estimée : %.6f%n", estimatedProbability);
            System.out.printf("  Demi-largeur de l'intervalle : %.6f%n", halfWidth);
            System.out.printf("  Intervalle de confiance : [%.6f, %.6f]%n",
                    estimatedProbability - halfWidth,
                    estimatedProbability + halfWidth);
            System.out.printf("  Nombre total de simulations : %d%n%n", totalRuns);

            // Diviser la demi-largeur pour l'itération suivante
            maxHalfWidth /= 2;
        }
    }

    /**
     * Deuxième simulation : étude de la couverture des intervalles de confiance.
     */
    private static void runSecondSimulation() {
        int K = 23;
        int Y = 365;
        int M = 2;
        double level = 0.95;
        long n = 1_000_000;         // Taille de l'échantillon
        int repetitions = 1000;     // Nombre de répétitions
        double p23 = 0.5072972343;  // Valeur théorique de p_{23}
        long seed = 0x134D6EE;      // Graine pseudo-aléatoire

        Random random = new Random(seed);

        int intervalsContainingP = 0; // Compteur d'intervalles contenant p_{23}

        for (int i = 0; i < repetitions; i++) {
            // Créer un collecteur de statistiques
            StatCollector stat = new StatCollector();

            // Effectuer N = 10^6 simulations
            MonteCarloSimulation.simulateNRuns(new BirthdayExperiment(K, Y, M), n, random, stat);

            // Calculer les statistiques
            double estimatedP = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(level);
            double lowerBound = estimatedP - halfWidth;
            double upperBound = estimatedP + halfWidth;

            // Vérifier si l'intervalle contient p23
            if (p23 >= lowerBound && p23 <= upperBound) {
                intervalsContainingP++;
            }
        }

        // Calcul du seuil empirique de couverture
        double coverage = (double) intervalsContainingP / repetitions;

        // Calcul de l'intervalle de confiance pour la couverture
        double z = 1.96; // Quantile pour un niveau de confiance de 95 %
        double marginOfError = z * Math.sqrt(coverage * (1 - coverage) / repetitions);
        double lowerCoverageBound = coverage - marginOfError;
        double upperCoverageBound = coverage + marginOfError;

        // Afficher les résultats
        System.out.printf("Seuil empirique de couverture : %.4f%n", coverage);
        System.out.printf("Intervalle de confiance pour la couverture : [%.4f, %.4f]%n",
                lowerCoverageBound, upperCoverageBound);
    }


    public static void runThirdSimulation() {
        // Paramètres pour l'expérience
        int Y = 365;                // Nombre de jours dans une année
        int M = 3;                  // Minimum d'occurrences
        double level = 0.95;        // Niveau de confiance
        long n = 1_000_000;         // Taille de l'échantillon
        int startK = 80;            // Taille minimale du groupe
        int endK = 100;             // Taille maximale du groupe
        long seed = 0x134D6EE;      // Graine pseudo-aléatoire

        // Réinitialiser le générateur pseudo-aléatoire
        Random random = new Random(seed);

        // Parcourir les tailles de groupes entre 80 et 100
        for (int K = startK; K <= endK; K++) {
            // Créer l'expérience et le collecteur
            BirthdayExperiment experiment = new BirthdayExperiment(K, Y, M);
            StatCollector stat = new StatCollector();

            // Simuler N = 10^6 réalisations
            MonteCarloSimulation.simulateNRuns(experiment, n, random, stat);

            // Calculer les statistiques
            double estimatedP = stat.getAverage();
            double halfWidth = stat.getConfidenceIntervalHalfWidth(level);
            double lowerBound = estimatedP - halfWidth;
            double upperBound = estimatedP + halfWidth;

            // Afficher les résultats
            System.out.printf("Pour K = %d :%n", K);
            System.out.printf("  Probabilité estimée : %.6f%n", estimatedP);
            System.out.printf("  Intervalle de confiance : [%.6f, %.6f]%n", lowerBound, upperBound);

            // Vérifier si la probabilité dépasse 0.5
            if (estimatedP > 0.5) {
                System.out.printf("  ** K minimal trouvé : %d **%n", K);
                break; // Arrêter la boucle dès que le K minimal est trouvé
            }
        }
    }
}
