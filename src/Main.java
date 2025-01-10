import montecarlo.*;
import statistics.*;

import java.util.Random;

// Juste pour l'exemple
class FairCoinTossExperiment implements Experiment {
	public double execute(Random rnd) {
		return rnd.nextDouble() < 0.5 ? 1.0 : 0.0;
	}
}

public class Main {

	public static void main(String[] args) {
	    // Juste pour l'exemple et vérifier que tout compile
		StatCollector stat = new StatCollector();

		Random rnd = new Random();
		rnd.setSeed(0x1348ABB);

		Experiment exp = new FairCoinTossExperiment();

		MonteCarloSimulation.simulateNRuns(exp, 1_000_000, rnd, stat);

		System.out.printf("**********************%n  Simulation results%n**********************%n");
		System.out.printf("Number of tosses generated: %d%n", stat.getNumberOfObs());
		System.out.printf("Estimated prob. of head:    %.5f%n", stat.getAverage());
		System.out.printf("Confidence interval (95%%):  %.5f +/- %.5f%n", stat.getAverage(), stat.getConfidenceIntervalHalfWidth(0.95));
	}
}
