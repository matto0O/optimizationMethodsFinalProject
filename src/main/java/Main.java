import algos.ga.GASolver;
import algos.nsga2.NSGA2Solver;
import algos.sa.SASolver;
import utils.Instance;
import utils.SolutionHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static void auxmain() throws IOException {
        String[] args = new String[]{"GA","100","5","30","100","12","30","1.0","1.0","out.csv","10000","0.1","0.5","100"};
        var sc = new Scanner(System.in);
        for(int i=0;i<args.length;i++){
            System.out.print(args[i]+":");
            var t=sc.nextLine();
            if(!t.isEmpty())
                args[i]=t;
        }
        main(args);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 10) {
            System.out.println("Usage: <solver name> <rooms> <classes> <coursesPerClass> <teachers> <peroidsADay> <teachersWorkHours> <teacher fitness multiplier> <students fitness multiplier> <output file name> [algorithm args]");
            System.out.println("ex: GA 100 5 30 100 12 30 1.0 1.0 out.csv 10000 0.1 0.5 100");
            if (args.length < 1)auxmain();
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

        PrintStream printStream=new PrintStream(args[argnum++]);

        if ("GA".equalsIgnoreCase(algorithmName)) {
            if (args.length < 14) {
                System.out.println("usage: <...others> <population> <mutation> <crossover> <generations>");
                return;
            }

            int populationSize = Integer.parseInt(args[argnum++]);
            double mutationRate = Double.parseDouble(args[argnum++]);
            double crossoverRate = Double.parseDouble(args[argnum++]);
            int gen = Integer.parseInt(args[argnum++]);

            GASolver geneticAlgorithm = new GASolver(instance, populationSize, mutationRate, crossoverRate, gen);

            for (int i = 0; i < gen; i++) {
                geneticAlgorithm.nextGeneration();
                geneticAlgorithm.getBestSolution().calculateFitness();
                System.out.println("generation "+i+" "+SolutionHelper.getCombinedFitness(geneticAlgorithm.getBestSolution()));
                SolutionHelper.savetofile(printStream,geneticAlgorithm.getBestSolution(),i,0);
            }

            //geneticAlgorithm.run();
        } else if ("NSGA2".equalsIgnoreCase(algorithmName)) {
            if (args.length < 14) {
                System.out.println("usage: <...others> <population> <mutation> <crossover> <generations>");
                return;
            }
            int populationSize = Integer.parseInt(args[argnum++]);
            double mutationRate = Double.parseDouble(args[argnum++]);
            double crossoverRate = Double.parseDouble(args[argnum++]);
            int gen = Integer.parseInt(args[argnum++]);
            NSGA2Solver nsga2Solver=new NSGA2Solver(instance, populationSize, mutationRate, crossoverRate, gen);
            for (int i = 0; i < gen; i++) {
                nsga2Solver.nextGeneration();
                nsga2Solver.getBestSolution().calculateFitness();
                System.out.println("generation "+i+" "+SolutionHelper.getCombinedFitness(nsga2Solver.getBestSolution()));
                SolutionHelper.savetofile(printStream,nsga2Solver.getBestSolution(),i,0);
            }
        }else if ("SA".equalsIgnoreCase(algorithmName)) {
            if (args.length < 13) {
                System.out.println("usage: <...others> <starting temp> <geometric decay> <linear tecay> <generations>");
                return;
            }
            double startTemp = Double.parseDouble(args[argnum++]);
            double adecay = Double.parseDouble(args[argnum++]);
            double bdecay = Double.parseDouble(args[argnum++]);
            int gen = Integer.parseInt(args[argnum++]);
            SASolver saSolver=new SASolver(instance,startTemp,adecay,bdecay);
            for (int i = 0; i < gen; i++) {
                saSolver.nextGen();
                saSolver.getBest().calculateFitness();
                System.out.println("generation "+i+" "+SolutionHelper.getCombinedFitness(saSolver.getBest()));
                SolutionHelper.savetofile(printStream,saSolver.getBest(),i,0);
            }
        } else {
            System.out.println("Unsupported algorithm: " + algorithmName);
        }
    }
}