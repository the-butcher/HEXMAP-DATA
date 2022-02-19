package com.igorion.type.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.igorion.type.json.JsonTypeImplPolygon;
import com.igorion.type.json.impl.AJsonTypeImplFeature;

/**
 * json mapping for an arcgis server feature<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class JsonTypeImplFeaturePolygon extends AJsonTypeImplFeature<JsonTypeImplPolygon> {

    @Override
    public Class<JsonTypeImplPolygon> getGeometryType() {
        return JsonTypeImplPolygon.class;
    }

}
