package algos.nsga2;

import java.util.Comparator;

public class CrowdingDistanceTotalComparator implements Comparator<DominanceHelper> {
    @Override
    public int compare(DominanceHelper o1, DominanceHelper o2) {
        return Double.compare(o2.crowdingDistance, o1.crowdingDistance);
    }
}
