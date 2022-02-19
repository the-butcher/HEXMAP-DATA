package com.igorion.http;

import java.util.Arrays;

/**
 * enumeration of known and handled authentication methods<br>
 *
 * @author h.fleischer
 * @since 14.03.2020
 *
 */
public enum EAuthenticateScheme {

    ANONYMOUS("ANONYMOUS_PLACEHOLDER", false, false), //no authentication requested
    BASIC("basic", true, false), //standard basic authentication
    NTLM("ntlm", true, true), //standard NTML
    NEGOTIATE("negotiate", true, true), //NEGOTIATE, which we will treat like NTML
    UNKWNOWN("UNKNOWN_PLACEHOLDER", true, false);

    private final String schemeName;
    private final boolean isRequiresAuthentication;
    private final boolean isIntegratedWindowsAuthentication;

    private EAuthenticateScheme(final String schemeName, final boolean isRequiresAuthentication, final boolean isIntegratedWindowsAuthentication) {
        this.schemeName = schemeName;
        this.isRequiresAuthentication = isRequiresAuthentication;
        this.isIntegratedWindowsAuthentication = isIntegratedWindowsAuthentication;
    }

    public boolean isRequiresAuthentication() {
        return this.isRequiresAuthentication;
    }

    public boolean isIntegratedWindowsAuthentication() {
        return this.isIntegratedWindowsAuthentication;
    }

    public String getSchemeName() {
        return this.schemeName;
    }

    protected boolean equalsOrIsBeginOfValue(String authenticationHeader) {
        String lowerAuthenticationHeader = authenticationHeader.trim().toLowerCase();
        return lowerAuthenticationHeader.startsWith(this.schemeName);
    }

    /**
     * find an {@link EAuthenticateScheme} constant that can be matched to the given authentication header value
     *
     * @param authenticationHeader
     * @return
     */
    public static EAuthenticateScheme fromAuthenticationHeader(String authenticationHeader) {
        return Arrays.stream(EAuthenticateScheme.values()).filter(scheme -> scheme.equalsOrIsBeginOfValue(authenticationHeader)).findFirst().orElse(EAuthenticateScheme.UNKWNOWN);
    }

}
