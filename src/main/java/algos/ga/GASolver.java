package algos.ga;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.ArrayList;

public class GASolver{
    ArrayList<Solution> population;
    double mutationRate;
    double crossRate;
    int iterations;

    public GASolver(Instance instance, int populationSize, double mutationRate,
                    double crossRate, int iterations) {
        this.mutationRate=mutationRate;
        this.crossRate=crossRate;
        population = initializePopulation(populationSize,instance);
        this.iterations = iterations;
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

    public Solution getBestSolution(){
        return population.getFirst();
    }
    public double getAverageFitness(){
        double s=0;
        for(var sol:population)
            s+=SolutionHelper.getCombinedFitness(sol);
        return s/ population.size();
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

    public void run(){
        for (int i = 0; i < iterations; i++) {
            nextGeneration();
            var t = getBestSolution();
            t.calculateFitness();
        }
    }

}
