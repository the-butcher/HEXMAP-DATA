package com.igorion.type.json.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igorion.type.json.IJsonType;

/**
 * json mapping for an arcgis server token response<br>
 * some properties have been added (username, referer, arcgis-intance) for documentation. keep in mind that only fields annotated are exepected to actuall be contained in a response<br>
 *
 * <pre>
 * {
 *   "token": "MwS6HsVztpkYqk4yXY2xmfDDMkJM3OoCPYkXQydBy8htLbnLpI8sDidQ7npXvdcUs46bVCb-rEZumPrWeHzUS4Hp4lTKKlwlwViCnx6Yq1r1JPUj6Q-dxCQB_C3PuxOfriN5svXvbn2HUVHafOtrrbNbBTz-W-dnaxC3Yy_EhsE.",,
 *   "expires": 1552050321501,
 *   "ssl": false
 * }
 * </pre>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AJsonTypeImpl implements IJsonType {

}
