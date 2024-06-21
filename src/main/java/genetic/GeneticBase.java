package genetic;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.ArrayList;

public class GeneticBase {
    ArrayList<Solution> population;
    double mutationRate;
    double crossRate;

    public GeneticBase(Instance instance, int populationSize, double mutationRate, double crossRate){
        this.mutationRate=mutationRate;
        this.crossRate=crossRate;
        population = initializePopulation(populationSize,instance);
    }

    ArrayList<Solution> initializePopulation(int populationSize, Instance instance){
        population = new ArrayList<>();
        for(int i=0;i<populationSize;i++){
            var s = new Solution(instance);
            s.random();
            population.add(s);
        }
        return population;
    }

    void mutate(){
        population.parallelStream().forEach(solution -> solution.mutate(mutationRate));
    }

    Solution selectParent(){
        double p = Math.random()*Math.random()*Math.random();
        return population.get((int)(p*population.size()));
    }

    void evaluatePopulation(){
        population.parallelStream().forEach(Solution::calculateFitness);
        population.sort(new SolutionHelper());
    }
}
