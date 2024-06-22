import algos.ga.GASolver;
import utils.Instance;
import utils.SolutionHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 10) {
            System.out.println("Usage: <rooms> <classes> <coursesPerClass> <teachers> <peroidsADay> <teachersWorkHours> <teacher fitness multiplier> <students fitness multiplier> <output file name> [algorithm args]");
            System.out.println("ex: GASolver 100 5 30 100 12 30 10000 0.1 0.5 100");
            return;
        }

        int argnum=0;

        String algorithmName = args[argnum++];
        int rooms= Integer.parseInt(args[argnum++]),
                classes= Integer.parseInt(args[argnum++]),
                coursesPerClass= Integer.parseInt(args[argnum++]),
                teachers= Integer.parseInt(args[argnum++]),
                peroidsADay= Integer.parseInt(args[argnum++]),
                teachersWorkHours= Integer.parseInt(args[argnum++]);

        Instance instance = new Instance(rooms, classes, coursesPerClass, teachers, peroidsADay, teachersWorkHours);

        SolutionHelper.setFitnessMult(Float.parseFloat(args[argnum++]),Float.parseFloat(args[argnum++]));

        FileOutputStream fileOutputStream=new FileOutputStream(args[argnum++]);

        if ("GA".equalsIgnoreCase(algorithmName)) {
            if (args.length < 14) {
                System.out.println("usage: <...others> <population> <mutation> <crossover> <generations>");
                System.out.println("For genetic algorithm, please provide all parameters.");
                return;
            }

            int populationSize = Integer.parseInt(args[argnum++]);
            double mutationRate = Double.parseDouble(args[argnum++]);
            double crossoverRate = Double.parseDouble(args[argnum++]);
            int gen = Integer.parseInt(args[argnum++]);

            GASolver geneticAlgorithm = new GASolver(instance, populationSize, mutationRate, crossoverRate, gen);
            //SolutionHelper.setFitnessDimensions(fitnessDimensions);

//            for (int i = 0; i < gen; i++) {
//                geneticAlgorithm.nextGeneration();
//                System.out.println("Generation " + (i + 1) + ":");
//                System.out.println("Fitnesses: " + Arrays.toString(geneticAlgorithm.getBestSolution().getFitnesses()));
//                System.out.println("Combined Fitness: " + SolutionHelper.getCombinedFitness(geneticAlgorithm.getBestSolution()));
//                System.out.println();
//                //todo add file output
//            }

            geneticAlgorithm.run();
        } else if ("NSGA2".equalsIgnoreCase(algorithmName)) {
            //todo
        } else {
            System.out.println("Unsupported algorithm: " + algorithmName);
        }
    }
}
