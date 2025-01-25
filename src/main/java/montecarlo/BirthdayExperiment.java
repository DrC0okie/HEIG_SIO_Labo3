package montecarlo;

import java.util.Random;

/**
 * Classe implémentant une expérience de Bernoulli simulant le paradoxe des anniversaires.
 * Cette classe permet de simuler une expérience où un groupe de K personnes choisit
 * aléatoirement leurs dates d'anniversaire parmi Y jours possibles. L'expérience retourne
 * un succès (1.0) si au moins une date est choisie M fois ou plus, et un échec (0.0) sinon.
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public class BirthdayExperiment implements Experiment {
    private final int K; // Nombre de personnes
    private final int Y; // Nombre de jours dans une année
    private final int M; // Nombre minimum d'occurrences pour succès


    public BirthdayExperiment(int K, int Y, int M) {
        this.K = K;
        this.Y = Y;
        this.M = M;
    }

    /**
     * Exécute l'expérience
     * @param rnd un générateur de nombres pseudo-aléatoires utilisé pour simuler les anniversaires.
     * @return 1.0 si au moins une date est choisie M fois ou plus (succès), 0.0 sinon (échec).
     */
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
                return 1.0;
            }
        }
        return 0.0;
    }
}