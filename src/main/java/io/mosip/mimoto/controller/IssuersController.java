package io.mosip.mimoto.controller;

import io.mosip.mimoto.core.http.ResponseWrapper;
import io.mosip.mimoto.dto.IssuerConfigDTO;
import io.mosip.mimoto.dto.IssuerConfigMapDTO;
import io.mosip.mimoto.dto.IssuerMapDTO;
import io.mosip.mimoto.service.IssuersService;
import io.mosip.mimoto.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/issuers")
public class IssuersController {
    @Autowired
    IssuersService issuersService;

    @GetMapping()
    public ResponseEntity<Object> getAllIssuers() {
        ResponseWrapper<IssuerMapDTO> responseWrapper = new ResponseWrapper<>();
        //TODO: Modify id
        responseWrapper.setId("mosip.inji.properties");
        responseWrapper.setVersion("v1");
        responseWrapper.setResponsetime(DateUtils.getRequestTimeString());
        responseWrapper.setResponse(issuersService.getAllIssuers());

        return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
    }

    @GetMapping("/{issuer-id}")
    public ResponseEntity<Object> getIssuerConfig(@PathVariable("issuer-id") String issuerId) {
        ResponseWrapper<IssuerConfigDTO> responseWrapper = new ResponseWrapper<>();
        //TODO: Modify id
        responseWrapper.setId("mosip.inji.properties");
        responseWrapper.setVersion("v1");
        responseWrapper.setResponsetime(DateUtils.getRequestTimeString());


        responseWrapper.setResponse(issuersService.getIssuerConfig(issuerId));

        return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
    }

}
