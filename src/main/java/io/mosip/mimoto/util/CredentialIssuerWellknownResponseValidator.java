package io.mosip.mimoto.util;

import io.mosip.mimoto.dto.mimoto.CredentialDefinitionResponseDto;
import io.mosip.mimoto.dto.mimoto.CredentialIssuerWellKnownResponse;
import io.mosip.mimoto.dto.mimoto.CredentialsSupportedResponse;
import io.mosip.mimoto.exception.ApiNotAccessibleException;
import io.mosip.mimoto.exception.InvalidWellknownResponseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;

public class CredentialIssuerWellknownResponseValidator {
    public void validate(CredentialIssuerWellKnownResponse response, Validator validator) throws ApiNotAccessibleException, InvalidWellknownResponseException {
        Set<ConstraintViolation<CredentialIssuerWellKnownResponse>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed:");
            for (ConstraintViolation<CredentialIssuerWellKnownResponse> violation : violations) {
                sb.append("\n").append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            }
            throw new InvalidWellknownResponseException(sb.toString());
        }

        for(Map.Entry<String, CredentialsSupportedResponse> entry: response.getCredentialConfigurationsSupported().entrySet()){
            CredentialsSupportedResponse supportedCredentialConfiguration = entry.getValue();
            //TODO: Extract the vc specific validations to separate classes
            if (supportedCredentialConfiguration.getFormat().equals("mso_mdoc")) {
                if (StringUtils.isBlank(supportedCredentialConfiguration.getDoctype())) {
                    throw new InvalidWellknownResponseException("Mandatory field 'doctype' missing");
                }
                if(supportedCredentialConfiguration.getClaims().isEmpty() || supportedCredentialConfiguration.getClaims()==null){
                    throw new InvalidWellknownResponseException("Mandatory field 'claims' missing");
                }
            }

            if (supportedCredentialConfiguration.getFormat().equals("ldp_vc")) {
                Set<ConstraintViolation<CredentialDefinitionResponseDto>> credentialDefinitionViolations = validator.validate(supportedCredentialConfiguration.getCredentialDefinition());
                if (!credentialDefinitionViolations.isEmpty()) {
                    StringBuilder sb = new StringBuilder("Validation failed:");
                    for (ConstraintViolation<CredentialDefinitionResponseDto> violation : credentialDefinitionViolations) {
                        sb.append("\n").append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
                    }
                    throw new InvalidWellknownResponseException(sb.toString());
                }
            }
        }
    }
}
