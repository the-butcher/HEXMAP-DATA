package com.igorion.type.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.type.json.impl.AJsonTypeImplGeometry;

/**
 * json mapping for an arcgis server feature attributes object<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class JsonTypeImplPolyline extends AJsonTypeImplGeometry {

    @JsonProperty("paths")
    List<List<List<Double>>> paths = new ArrayList<>();

    @Override
    public List<List<Double>> getAllCoordinates() {
        List<List<Double>> allCoordinates = new ArrayList<>();
        getPaths().forEach(allCoordinates::addAll);
        return allCoordinates;
    }

    public void setPaths(List<List<List<Double>>> paths) {
        this.paths = paths;
    }

    public List<List<List<Double>>> getPaths() {
        return this.paths;
    }

    public double getMValue(JsonTypeImplPoint point, int ringIndex, double nDotThreshold) {

        List<List<Double>> path = this.paths.get(ringIndex);

        double xPrev;
        double yPrev;
        double xCurr;
        double yCurr;
        double xDifS;
        double yDifS;
        double distS;
        double xDifP;
        double yDifP;
        double nDotP; //the "normal" dot (to get the distance from this segment)
        double pDotP; //the "parallel" dot (distance along the segment)
        double distT = 0;

        double uDiff;
        double vDiff;

        for (int i = 1; i < path.size(); i++) {

            xPrev = path.get(i - 1).get(0);
            yPrev = path.get(i - 1).get(1);
            xCurr = path.get(i).get(0);
            yCurr = path.get(i).get(1);

            xDifS = xCurr - xPrev; //the x-length of the current segment
            yDifS = yCurr - yPrev; //the y-length of the current segment
            xDifP = point.getX() - xPrev; //the x-length from segment origin to point
            yDifP = point.getY() - yPrev; //the y-length from segment origin to point

            distS = Math.sqrt(xDifS * xDifS + yDifS * yDifS); //length of segment
            xDifS /= distS;
            yDifS /= distS;

            //the normal distance of the point to this segment
            nDotP = Math.abs(xDifP * -yDifS + yDifP * xDifS);
            if (nDotP <= nDotThreshold) {

                //the "parallel" distance of the point along this segment
                pDotP = xDifP * xDifS + yDifP * yDifS;
                uDiff = Math.abs(pDotP);
                vDiff = Math.abs(distS - pDotP);
                if (pDotP >= 0 && pDotP <= distS) {
                    return distT + pDotP;
                } else if (uDiff <= nDotThreshold) {
                    return distT;
                } else if (vDiff <= nDotThreshold) {
                    return distT + distS;
                }

            }

            //not satifying distance-threshold or not within segment bounds
            distT += distS;

        }
        System.out.println("missed segment " + point.x + "/" + point.y);
        return -1;

    }

    @Override
    public String toString() {
        return String.format("%s [paths: %s]", getClass().getSimpleName(), getPaths());
    }

}
