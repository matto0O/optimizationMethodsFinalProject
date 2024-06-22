package algos.nsga2;

import java.util.Comparator;

public class CrowdingDistanceTeacherObjectiveComparator implements Comparator<DominanceHelper> {

    @Override
    public int compare(DominanceHelper o1, DominanceHelper o2) {
        return Integer.compare(o1.evaluateTotalTeacherFitness(), o2.evaluateTotalTeacherFitness());
    }
}
