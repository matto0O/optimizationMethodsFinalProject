import algos.nsga2.NSGA2Solver;
import utils.Instance;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Instance instance = new Instance();

        NSGA2Solver geneticAlgorithm = new NSGA2Solver(instance,100,0.05,0.5, 100);
        geneticAlgorithm.run();
    }
}
