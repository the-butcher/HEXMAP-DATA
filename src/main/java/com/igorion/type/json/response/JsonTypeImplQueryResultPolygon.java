package com.igorion.type.json.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igorion.type.json.JsonTypeImplPolygon;

/**
 * json mapping for an arcgis server layer response<br>
 *
 *
 * @author h.fleischer
 * @since 26.06.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTypeImplQueryResultPolygon extends AJsonTypeImplQueryResult<JsonTypeImplPolygon, JsonTypeImplFeaturePolygon> {

}
