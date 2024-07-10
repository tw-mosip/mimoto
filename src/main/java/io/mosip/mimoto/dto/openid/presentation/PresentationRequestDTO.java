package io.mosip.mimoto.dto.openid.presentation;

import lombok.Data;

@Data
public class PresentationRequestDTO {

    String response_type;
    String resource;
    String presentation_definition;
    String client_id;
    String redirect_uri;

}
