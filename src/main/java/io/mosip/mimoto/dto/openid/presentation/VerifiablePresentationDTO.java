package io.mosip.mimoto.dto.openid.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.mosip.mimoto.dto.mimoto.VCCredentialProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@JsonPropertyOrder({"@context", "type", "verifiableCredential"})
public class VerifiablePresentationDTO {
    @JsonProperty("@context")
    private Object context;

    @NotEmpty
    private List<@NotBlank String> type;

    List<VCCredentialProperties> verifiableCredential;
}
