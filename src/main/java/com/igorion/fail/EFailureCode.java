package com.igorion.fail;

public enum EFailureCode {

    BAD_REQUEST(400),
    INTERNAL_ERROR(500),
    INVALID_GATEWAY(502),

    ILLEGAL_VALUE(4001),
    OPTIONAL_VALUE_EXPECTED_BUT_MISSING(4002),

    COLLECT_FAILURE(5001),
    PARSE_FAILURE(5002),
    REFERENCE_FAILURE(5003),
    PROTOCOL_TYPE_FAILURE(5004),
    PBF_ENCODE_FAILURE(5005),

    AUTHENTICATION_FAILURE(5100),
    MISCONFIGURED(5101),
    INVALID_RESPONSE(5102),
    FTP_FAILURE(5103),

    THRESHOLD_VIOLATION(5200),
    INVALID_STATE(5201),
    FILE_WRITE_FAILURE(5202);

    private int code;

    private EFailureCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
