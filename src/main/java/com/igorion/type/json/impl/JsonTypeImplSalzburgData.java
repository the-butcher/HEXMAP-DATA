package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplSalzburgData extends AJsonTypeImpl {

    @JsonProperty("gesamt")
    private String gesamt;

    @JsonProperty("genesen")
    private String genesen;

    @JsonProperty("verstorben")
    private String verstorben;

    @JsonProperty("aktiv")
    private String aktiv;

    public String getGesamt() {
        return this.gesamt;
    }

    public String getGenesen() {
        return this.genesen;
    }

    public String getVerstorben() {
        return this.verstorben;
    }

    public String getAktiv() {
        return this.aktiv;
    }

}
