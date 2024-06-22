package algos.nsga2;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NSGA2Solver{
    ArrayList<DominanceHelper> population;
    double mutationRate;
    double crossRate;
    int iterations;

    public NSGA2Solver(Instance instance, int populationSize, double mutationRate,
                       double crossRate, int iterations) {
        this.mutationRate=mutationRate;
        this.crossRate=crossRate;
        population = initializePopulation(populationSize, instance);
        this.iterations = iterations;

        List<DominanceHelper> dh = dominanceHelpers();
        population.clear();
        population.addAll(dh);
    }

    ArrayList<DominanceHelper> initializePopulation(int populationSize, Instance instance){
        population = new ArrayList<>();
        for(int i=0;i<populationSize;i++){
            var s = new Solution(instance);
            s.random();
            var d = new DominanceHelper(s);
            population.add(d);
        }
        return population;
    }

    void mutate(){
        population.parallelStream().forEach(solution -> solution.mutate(mutationRate));
    }

    DominanceHelper selectParent(){
        double p = Math.random()*Math.random()*Math.random();
        return population.get((int)(p*population.size()));
    }

    void evaluatePopulation(){
        population.parallelStream().forEach(DominanceHelper::calculateFitness);
        population.sort(new SolutionHelper());
    }

    private List<DominanceHelper> dominanceHelpers(){
        return DominanceHelper.getDominanceHelperList(population);
    }

    public ArrayList<ArrayList<DominanceHelper>> nonDominatedSorting(ArrayList<DominanceHelper> dominanceHelperList){
        ArrayList<ArrayList<DominanceHelper>> fronts = new ArrayList<>();
        ArrayList<DominanceHelper> front = new ArrayList<>();

        for (DominanceHelper p : dominanceHelperList) {
            p.dominatedByCount = 0;
            p.dominated.clear();
        }

        for (DominanceHelper p: dominanceHelperList){
            for (DominanceHelper q: dominanceHelperList){
                if (p.dominates(q)){
                    p.dominated.add(q);
                } else if (q.dominates(p)){
                    p.dominatedByCount++;
                }
            }
            if (p.dominatedByCount == 0) {
                p.rank = 0;
                front.add(p);
            }
        }

        if(!front.isEmpty()){
            fronts.add(new ArrayList<>(front));
        }

        int currentFront = 0;

        while (currentFront < fronts.size() && !fronts.get(currentFront).isEmpty()){
           ArrayList<DominanceHelper> nextFront = new ArrayList<>();
           for (DominanceHelper p : fronts.get(currentFront)){
               for (DominanceHelper q: p.dominated){
                   q.dominatedByCount--;
                   if (q.dominatedByCount == 0){
                       q.rank = currentFront+1;
                       nextFront.add(q);
                   }
               }
           }
           currentFront++;
            if(!nextFront.isEmpty()){
                fronts.add(nextFront);
            }
        }

        return fronts;
    }

    public void crowdingDistanceAssignment(ArrayList<DominanceHelper> front){
        for (DominanceHelper p: front){
            p.crowdingDistance = 0;
        }

        front.sort(new CrowdingDistanceSchoolClassObjectiveComparator());
        front.getFirst().crowdingDistance = Double.POSITIVE_INFINITY;
        front.getLast().crowdingDistance = Double.POSITIVE_INFINITY;

        double min = front.getFirst().evaluateTotalClassFitness();
        double max = front.getLast().evaluateTotalClassFitness();

        objectiveCDLoop(front, min, max);

        front.sort(new CrowdingDistanceTeacherObjectiveComparator());
        front.getFirst().crowdingDistance = Double.POSITIVE_INFINITY;
        front.getLast().crowdingDistance = Double.POSITIVE_INFINITY;

        min = front.getFirst().evaluateTotalTeacherFitness();
        max = front.getLast().evaluateTotalTeacherFitness();

        objectiveCDLoop(front, min, max);
    }

    private void objectiveCDLoop(ArrayList<DominanceHelper> front, double min, double max) {
        for (int i = 1; i < front.size()-1; i++){
            double distance = front.get(i+1).crowdingDistance - front.get(i-1).crowdingDistance;
            distance /= (max - min);
            front.get(i).crowdingDistance += distance;
        }
    }

    DominanceHelper tournamentSelection(){
        DominanceHelper p1 = population.get(new Random().nextInt(population.size()));
        DominanceHelper p2 = population.get(new Random().nextInt(population.size()));
        while(p2.equals(p1)){
            p2 = population.get(new Random().nextInt(population.size()));
        }

        if(p1.rank < p2.rank){
            return p1;
        } else if(p1.rank > p2.rank){
            return p2;
        } else {
            if(p1.crowdingDistance > p2.crowdingDistance){
                return p1;
            } else {
                return p2;
            }
        }
    }

    public void nextGeneration(){
        ArrayList<DominanceHelper> offspring = new ArrayList<>(population.size() * 2);

        while(offspring.size() < population.size()){
            DominanceHelper parentA = tournamentSelection();
            DominanceHelper parentB = tournamentSelection();
            Solution child = DominanceHelper.cross(parentA, parentB, crossRate);
            child.mutate(mutationRate);
            offspring.add(new DominanceHelper(child));
        }

        offspring.addAll(population);

        ArrayList<ArrayList<DominanceHelper>> fronts = nonDominatedSorting(offspring);

        ArrayList<DominanceHelper> newPopulation = new ArrayList<>(population.size());
        int i = 0;

        while(newPopulation.size() + fronts.get(i).size() <= population.size()){
            crowdingDistanceAssignment(fronts.get(i));
            newPopulation.addAll(fronts.get(i));
            i++;
        }

        crowdingDistanceAssignment(fronts.get(i));

        fronts.get(i).sort(new CrowdingDistanceTotalComparator());
        newPopulation.addAll(fronts.get(i).subList(0, population.size() - newPopulation.size()));

        population = newPopulation;
        evaluatePopulation();
    }
    public Solution getBestSolution(){
        return population.getFirst();
    }

    public void run(){
        for (int i = 0; i < iterations; i++) {
            nextGeneration();
            System.out.println(population.getFirst().evaluateTotalTeacherFitness()
                    + " " + population.getFirst().evaluateTotalClassFitness());
        }
    }
}
