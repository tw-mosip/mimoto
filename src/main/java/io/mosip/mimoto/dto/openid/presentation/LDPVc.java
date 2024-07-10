package io.mosip.mimoto.dto.openid.presentation;

import lombok.Data;

import java.util.List;

@Data
public class LDPVc {
    List<String> proofTypes;
}
