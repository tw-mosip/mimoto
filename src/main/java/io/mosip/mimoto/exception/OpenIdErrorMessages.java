package io.mosip.mimoto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenIdErrorMessages {

    BAD_REQUEST("invalid_request", "Invalid request-some incorrect parameters in the request"),
    INVALID_RESOURCE("resource_not_found", "The requested resource doesn’t exist."),
    INTERNAL_SERVER_ERROR("internal_server_error", "We are unable to process request now");

    private final String errorMessage;
    private final String errorCode;
}
