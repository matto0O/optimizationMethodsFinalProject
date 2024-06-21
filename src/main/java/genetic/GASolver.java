package genetic;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.ArrayList;

public class GASolver extends GeneticBase{

    public GASolver(Instance instance, int populationSize, double mutationRate, double crossRate) {
        super(instance, populationSize, mutationRate, crossRate);
    }

    public Solution getBestSolution(){
        return population.get(0);
    }

    public void nextGeneration(){
        ArrayList<Solution> newPopulation = new ArrayList<>();
        for(int i=0;i<population.size();i++){
            var parentA = selectParent();
            var parentB = selectParent();
            var n = Solution.cross(parentA,parentB,crossRate);
            newPopulation.add(n);
        }
        population=newPopulation;
        mutate();
        evaluatePopulation();
    }

}
