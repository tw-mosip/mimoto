package io.mosip.mimoto.service;

import io.mosip.mimoto.dto.mimoto.*;
import io.mosip.mimoto.exception.ApiNotAccessibleException;

import java.io.IOException;

public interface AuthorizationServerService {
    AuthorizationServerWellKnownResponse getWellknown(String authorizationServerHostUrl) throws ApiNotAccessibleException, IOException;
}
