package io.mosip.mimoto.dto.mimoto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletBindingRequestDTO {

    @Schema(description = "Transaction Id of Binding OTP")
    private String requestTime;
    @Schema(description = "Transaction Id of Binding OTP")
    private WalletBindingInnerReq request;

}
