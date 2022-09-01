package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplMortalityDataItem extends AJsonTypeImpl {

    @JsonProperty("mortVal")
    private Double mortVal; // nullable

    @JsonProperty("mortAvg")
    private double mortAvg;

    @JsonProperty("ci95Lower")
    private double ci95Lower;

    @JsonProperty("ci95Upper")
    private double ci95Upper;

    @JsonProperty("ci68Lower")
    private double ci68Lower;

    @JsonProperty("ci68Upper")
    private double ci68Upper;

    @JsonProperty("incidence")
    private double incidence;

    public void setMortVal(double mortVal) {
        this.mortVal = mortVal;
    }

    public void setMortAvg(double mortAvg) {
        this.mortAvg = mortAvg;
    }

    public void setCi95Lower(double ci95Lower) {
        this.ci95Lower = ci95Lower;
    }

    public void setCi95Upper(double ci95Upper) {
        this.ci95Upper = ci95Upper;
    }

    public void setCi68Lower(double ci68Lower) {
        this.ci68Lower = ci68Lower;
    }

    public void setCi68Upper(double ci68Upper) {
        this.ci68Upper = ci68Upper;
    }

    public void setIncidence(double incidence) {
        this.incidence = incidence;
    }

}
