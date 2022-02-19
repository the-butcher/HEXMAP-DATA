package com.igorion.type.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.igorion.type.json.impl.AJsonTypeImpl;

/**
 * json mapping for an arcgis server layer field<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplField extends AJsonTypeImpl {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    //skipped: "alias", "domain"

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return String.format("%s [name: %s, type: %s]", getClass().getSimpleName(), getName(), getType());
    }

}
