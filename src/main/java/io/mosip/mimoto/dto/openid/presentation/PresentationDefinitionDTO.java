package io.mosip.mimoto.dto.openid.presentation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
public class PresentationDefinitionDTO {

    String id;
    List<InputDescriptorDTO> input_descriptors;
}
