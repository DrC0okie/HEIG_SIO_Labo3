package montecarlo;

import org.junit.jupiter.api.Test;
import statistics.StatCollector;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MonteCarloSimulationTest {
    @Test
    void testBirthdayParadoxSimulation() {
        // Paramètres pour l'expérience
        int K = 23;               // Nombre de personnes
        int Y = 365;              // Nombre de jours
        int M = 2;                // Minimum d'occurrences pour succès
        double level = 0.95;      // Niveau de confiance
        double maxHalfWidth = 0.001; // Demi-largeur maximale de l'intervalle
        long initialRuns = 1000;  // Simulations initiales
        long additionalRuns = 100; // Simulations additionnelles

        // Initialiser l'expérience et les outils nécessaires
        BirthdayExperiment experiment = new BirthdayExperiment(K, Y, M);
        Random random = new Random();
        StatCollector stat = new StatCollector();

        // Simuler jusqu'à atteindre la précision
        MonteCarloSimulation.simulateTillGivenCIHalfWidth(
                experiment,
                level,
                maxHalfWidth,
                initialRuns,
                additionalRuns,
                random,
                stat
        );

        // Vérifications des résultats
        double estimatedProbability = stat.getAverage();
        double halfWidth = stat.getConfidenceIntervalHalfWidth(level);
        long totalRuns = stat.getNumberOfObs();

        // Vérification 1 : La probabilité estimée est cohérente avec la théorie (~0.507 pour K=23)
        assertTrue(estimatedProbability > 0.49 && estimatedProbability < 0.52,
                "La probabilité estimée est hors de l'intervalle attendu pour K=23.");

        // Vérification 2 : La demi-largeur respecte la contrainte
        assertTrue(halfWidth <= maxHalfWidth,
                "La demi-largeur de l'intervalle de confiance ne respecte pas la contrainte.");

        // Vérification 3 : Un nombre raisonnable de simulations a été effectué
        assertTrue(totalRuns >= initialRuns,
                "Le nombre total de simulations est insuffisant.");
        assertTrue(totalRuns > initialRuns,
                "Le simulateur semble ne pas ajouter de simulations additionnelles.");

        // Affichage pour information
        System.out.printf("Résultats de la simulation :%n");
        System.out.printf("Probabilité estimée : %.5f%n", estimatedProbability);
        System.out.printf("Demi-largeur de l'intervalle : %.5f%n", halfWidth);
        System.out.printf("Nombre total de simulations : %d%n", totalRuns);
    }
}