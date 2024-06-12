import genetic.Solver;
import models.organization.SchoolClass;
import models.people.Teacher;
import models.rooms.*;
import utils.Generator;
import utils.Instance;
import utils.SolutionHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Instance instance = new Instance();

        Solver geneticAlgorithm = new Solver(instance,10000,0.1,0.5);
        for (int i = 0; i < 1000; i++) {
            geneticAlgorithm.nextGeneration();
            System.out.println(Arrays.toString((geneticAlgorithm.getBestSolution().getFitnesses())));
            System.out.println(SolutionHelper.getCombinedFitness(geneticAlgorithm.getBestSolution()));
            {
                var t = geneticAlgorithm.getBestSolution();
                t.calculateFitness();
            }
        }

        instance.getSchoolClasses().forEach(System.out::println);
    }
}
