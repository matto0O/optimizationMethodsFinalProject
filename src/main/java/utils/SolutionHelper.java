package utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;

public class SolutionHelper implements Comparator<Solution>{

    static float teacherMult, studentMult;

    static public void savetofile(PrintStream out, Solution s, int i, double avg){//todo add time?
        out.println(i+","+ avg +","+getCombinedFitness(s)+","+s.evaluateTotalClassFitness()+","+s.evaluateTotalTeacherFitness());
    }

    public static float getCombinedFitness(Solution s){
        return teacherMult*s.getFitnesses()[0]+studentMult*s.getFitnesses()[1];
        //return teacherMult*s.evaluateTotalTeacherFitness()+studentMult*s.evaluateTotalClassFitness();
    }

    public static void setFitnessMult(float teacherMultf, float studentMultf) {
        teacherMult = teacherMultf;
        studentMult = studentMultf;
    }

    @Override
    public int compare(Solution o1,Solution o2){
        return Float.compare(getCombinedFitness(o2),getCombinedFitness(o1));
    }

    public int compare5d(Solution o1, Solution o2) {

        int[] s1 = o1.getFitnesses();
        int[] s2 = o2.getFitnesses();

        var f1 = Integer.compare(s1[0], s2[0]);
        if(f1!=0)return f1;
        var f2 = Integer.compare(s1[1], s2[1]);
        if(f2!=0)return f2;

        /*
        //technically illegal if weights are applied,
        // a>b and b>c is not a>c
        boolean s1Better = true;
        boolean s2Better = true;

        for (int i = 0; i < s1.length; i++) {
            if (s1[i] > s2[i]) {
                s1Better = false;
            }
            if (s1[i] < s2[i]) {
                s2Better = false;
            }
        }

        if (s1Better) {
            return -1;
        }
        if (s2Better) {
            return 1;
        }
        //end of questionable part
        //we have 3d pareto front (or 5, but the other two need to be 0)
        */

        int sum1 = 0;
        int sum2 = 0;

        for (int i = 0; i < s1.length; i++) {
            sum1 += s1[i];
            sum2 += s2[i];
        }

        return Integer.compare(sum1, sum2);
    }
}
