package algos.nsga2;

import utils.Solution;

import java.util.ArrayList;
import java.util.List;

public class DominanceHelper extends Solution {
    List<DominanceHelper> dominated;
    int rank;
    int dominatedByCount;
    double crowdingDistance;

    public DominanceHelper(Solution solution) {
        super(solution);
        this.dominated = new ArrayList<>();
        this.dominatedByCount = 0;
        this.rank = -1;
        this.crowdingDistance = -1.0;
    }

    public static List<DominanceHelper> getDominanceHelperList(ArrayList<? extends Solution> population) {
        List<DominanceHelper> dominanceHelperList = new ArrayList<>();
        for (Solution solution : population) {
            dominanceHelperList.add(new DominanceHelper(solution));
        }
        return dominanceHelperList;
    }

    public boolean dominates(DominanceHelper b) {
        return (evaluateTotalTeacherFitness() >= b.evaluateTotalTeacherFitness()
                && evaluateTotalClassFitness() >= b.evaluateTotalClassFitness())
                && (evaluateTotalTeacherFitness() > b.evaluateTotalTeacherFitness()
                || evaluateTotalClassFitness() > b.evaluateTotalClassFitness());
    }
}
