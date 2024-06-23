package algos.sa;

import utils.Instance;
import utils.Solution;
import utils.SolutionHelper;

import java.util.List;

public class SASolver {
    Solution best;
    //List<Solution> paretoFront;// future test
    double stemp;
    double a,b;
    Instance instance;

    public SASolver(Instance instance, double starttemp, double a, double b){
        this.instance=instance;
        this.stemp=starttemp;
        this.a=a;
        this.b=b;
        best=new Solution(instance);
        best.random();
        best.calculateFitness();
        if(a<=0 && b==0){
            this.a=0.9;
            this.b=0;
        }
    }
    
    public Solution getBest(){
        return best;
    }
    public double getAverageFitness(){
        return SolutionHelper.getCombinedFitness(best);
    }
    public void nextGen(){
        double currtemp=stemp;
        while (currtemp>a){
            Solution next = best.clone();
            next.mutate(0.1);
            next.calculateFitness();
            double fitnext=SolutionHelper.getCombinedFitness(next);
            double fitbest=SolutionHelper.getCombinedFitness(best);
            if(fitnext<fitbest)
                best=next;
            else if(fitnext/fitbest < Math.random()+(currtemp/stemp)){
                best=next;
            }
            currtemp=currtemp*a-b;
        }
    }
}
