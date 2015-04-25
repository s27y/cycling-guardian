package yangsun.me.cyclingguardian;

/**
 * Created by yangsun on 23/04/15.
 */
public class CrashDetector {
    public static final int MAX_PEACE_COUNT_AFTER_SUSPICIOUS_FALL = 10;
    public static final int TOTAL_COUNT_AFTER_SUSPICIOUS_FAIL = 15;
    boolean isSuspiciousFallDetected;
    double mPeaceThreshold,mImpactThreshold;
    int totalCountAfterSuspiciousFall =0;
    int peaceCountAfterImpact = 0;


    public CrashDetector(double peaceThreshold, double impactThreshold)
    {
        isSuspiciousFallDetected = false;
        mPeaceThreshold = peaceThreshold;
        mImpactThreshold = impactThreshold;

    }

    public boolean addAccerationToList(double acc)
    {
        if(acc > mImpactThreshold)
        {
            isSuspiciousFallDetected = true;
            totalCountAfterSuspiciousFall = 0;
            peaceCountAfterImpact = 0;
        }
        else {

            if(acc <mPeaceThreshold)
            {
                if(isSuspiciousFallDetected == true)
                {
                    if (this.totalCountAfterSuspiciousFall >TOTAL_COUNT_AFTER_SUSPICIOUS_FAIL)
                    {
                        if(peaceCountAfterImpact> MAX_PEACE_COUNT_AFTER_SUSPICIOUS_FALL)
                        {
                            isSuspiciousFallDetected = false;
                            this.totalCountAfterSuspiciousFall = 0;
                            peaceCountAfterImpact = 0;
                            return true;
                        }
                        else
                        {
                            //NOT A IMPACT
                            isSuspiciousFallDetected = false;
                            this.totalCountAfterSuspiciousFall = 0;
                            peaceCountAfterImpact = 0;
                            return false;

                        }
                    }else
                    {
                        this.totalCountAfterSuspiciousFall++;
                        peaceCountAfterImpact++;
                    }
                }
            }
            else
            {
                if(isSuspiciousFallDetected)
                {
                    if (this.totalCountAfterSuspiciousFall > TOTAL_COUNT_AFTER_SUSPICIOUS_FAIL)
                    {
                        if(peaceCountAfterImpact> MAX_PEACE_COUNT_AFTER_SUSPICIOUS_FALL) {
                            isSuspiciousFallDetected = false;
                            this.totalCountAfterSuspiciousFall = 0;
                            peaceCountAfterImpact = 0;
                            return true;
                        }
                        else
                        {
                            //NOT A IMPACT
                            isSuspiciousFallDetected = false;
                            this.totalCountAfterSuspiciousFall = 0;
                            peaceCountAfterImpact = 0;
                            return false;
                        }


                    }else
                    {
                        this.totalCountAfterSuspiciousFall++;
                    }

                }
            }
        }
        return false;
    }



}
