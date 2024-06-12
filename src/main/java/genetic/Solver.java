package genetic;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.ArrayList;

public class Solver {
    ArrayList<Solution> population;
    double mutationRate;
    double crossRate;

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
    public Solver(Instance instance, int populationSize,double mutationRate,double crossRate){
        this.mutationRate=mutationRate;
        this.crossRate=crossRate;
        population = new ArrayList<>();
        for(int i=0;i<populationSize;i++){
            var s = new Solution(instance);
            s.random();
            population.add(s);
        }
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
