package io.mosip.mimoto.dto.openid.presentation;

import lombok.Data;
import java.util.List;

@Data
public class PresentationSubmissionDTO {
    String id;
    String definition_id;
    List<SubmissionDescriptorDTO> descriptor_map;
}
