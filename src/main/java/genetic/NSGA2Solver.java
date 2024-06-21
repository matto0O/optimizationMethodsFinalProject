package genetic;

import utils.Instance;
import utils.Solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NSGA2Solver extends GeneticBase{
    public NSGA2Solver(Instance instance, int populationSize, double mutationRate, double crossRate) {
        super(instance, populationSize, mutationRate, crossRate);
    }

    private boolean dominates(Solution a, Solution b){
        return (
                        a.evaluateTotalTeacherFitness() >= b.evaluateTotalTeacherFitness()
                        && a.evaluateTotalClassFitness() >= b.evaluateTotalClassFitness()
                ) && (
                        a.evaluateTotalTeacherFitness() > b.evaluateTotalTeacherFitness()
                        || a.evaluateTotalClassFitness() > b.evaluateTotalClassFitness()
                );
    }

    public void nonDominatedSorting(){
//        ArrayList<Solution> front = new ArrayList<>();
//        HashMap<Solution, HashSet<Solution>> dominated = new HashMap<>();
//        for (Solution p: population){
//            HashSet<Solution> sp = new HashSet<>();
//            int np = 0;
//            for (Solution q: population){
//                if (dominates(p,q)){
//                    sp.add(q);
//                } else if (dominates(q,p)){
//                    np++;
//                }
//            }
//            dominated.put(p,sp);
//            if (np == 0)
//                front.add(p);
//        }
//        int i = 1;
//        while (!front.isEmpty()){
//           HashSet<Solution> Q = new HashSet<>();
//           for (Solution p: front){
//               for (Solution q: population){
//
//               }
//           }
//        }
    }

    public void crowdingDistanceAssignment(){
        // todo
    }

    public void nextGeneration(){
        nonDominatedSorting();
        crowdingDistanceAssignment();
        // todo
    }
}
