package net.apinoita.sextant.util;

public class ModMeasuringUtil {
    public static float calculateMeasurement(float firstAngle, float secondAngle) {
        float measurement = Math.abs(secondAngle - firstAngle);
        if (measurement > 180F){
            return 360F - measurement;
        }
        return measurement;
    }

    // 0/360(south) 270(east) 180(north) 90(west)
    public static float convertAngleTo360format(float angle){
        angle =  angle- (((float) Math.floor((double) Math.abs(angle)/360F))*360F);
        if (angle < 0){
            angle =  angle + (((float) Math.floor((double) Math.abs(angle)/360F))*360F);
            return 360F + angle;
        }
        angle =  angle- (((float) Math.floor((double) Math.abs(angle)/360F))*360F);
        return angle;
    }
}
