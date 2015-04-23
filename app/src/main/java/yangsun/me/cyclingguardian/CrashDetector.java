package yangsun.me.cyclingguardian;

import java.util.ArrayList;

/**
 * Created by yangsun on 23/04/15.
 */
public class CrashDetector {
    ArrayList<Double> mAccelerationList;
    boolean isHighImpactDetected;
    double mPeaceThreshold,mImpactThreshold;
    int totalCountAfterImpact=0;
    int peaceCountAfterImpact = 0;



    public CrashDetector(double peaceThreshold, double impactThreshold)
    {
        mAccelerationList = new ArrayList<>();
        isHighImpactDetected = false;
        mPeaceThreshold = peaceThreshold;
        mImpactThreshold = impactThreshold;

    }

    public boolean addAccerationToList(double acc)
    {
        mAccelerationList.add(acc);
        if(acc > mImpactThreshold)
        {
            isHighImpactDetected = true;
            totalCountAfterImpact = 0;
            peaceCountAfterImpact = 0;
        }
        else if(acc <mPeaceThreshold)
        {
            if(isHighImpactDetected == true)
            {
                if (totalCountAfterImpact > 15)
                {
                    if(peaceCountAfterImpact> 10)
                    {
                        isHighImpactDetected = false;
                        totalCountAfterImpact = 0;
                        peaceCountAfterImpact = 0;
                        return true;
                    }
                    else
                    {
                        //NOT A IMPACT
                        isHighImpactDetected = false;
                        totalCountAfterImpact = 0;
                        peaceCountAfterImpact = 0;
                        return false;

                    }
                }else
                {
                    totalCountAfterImpact++;
                    peaceCountAfterImpact++;
                }
            }
        }
        else
        {
            if(isHighImpactDetected)
            {
                if (totalCountAfterImpact > 15)
                {
                    if(peaceCountAfterImpact> 10) {
                        isHighImpactDetected = false;
                        totalCountAfterImpact = 0;
                        peaceCountAfterImpact = 0;
                        return true;
                    }
                    else
                    {
                        //NOT A IMPACT
                        isHighImpactDetected = false;
                        totalCountAfterImpact = 0;
                        peaceCountAfterImpact = 0;
                        return false;
                    }


                }else
                {
                    totalCountAfterImpact++;
                }

            }
        }
        return false;
    }



}
