package io.mosip.mimoto.dto.openid;

import lombok.Data;

import java.util.List;

@Data
public class VerifierDTO {
    String client_id;
    List<String> redirect_uri;
}
