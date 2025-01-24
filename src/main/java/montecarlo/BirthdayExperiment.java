package montecarlo;

import java.util.Random;

public class BirthdayExperiment implements Experiment {
    private final int K; // Nombre de personnes
    private final int Y; // Nombre de jours dans une année
    private final int M; // Nombre minimum d'occurrences pour succès


    public BirthdayExperiment(int K, int Y, int M) {
        this.K = K;
        this.Y = Y;
        this.M = M;
    }

    @Override
    public double execute(Random rnd) {
        // Tableau pour compter les occurrences de chaque jour
        int[] dayCount = new int[Y];

        // Génère K anniversaires aléatoires
        for (int i = 0; i < K; i++) {
            int day = rnd.nextInt(Y); // Tirer un jour aléatoire entre 0 et Y-1
            dayCount[day]++;

            // Vérifie si une date atteint M occurrences
            if (dayCount[day] >= M) {
                return 1.0; // Succès
            }
        }
        return 0.0; // Échec
    }
}