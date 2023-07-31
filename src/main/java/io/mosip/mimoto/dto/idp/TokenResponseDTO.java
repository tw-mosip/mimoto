package io.mosip.mimoto.dto.idp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TokenResponseDTO {
    private String id_token;
    private String token_type;
    private String access_token;
    private int expires_in;
    private String scope;
}
