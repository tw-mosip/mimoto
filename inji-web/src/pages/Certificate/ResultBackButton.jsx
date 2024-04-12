import {useNavigate} from "react-router-dom";
import {Button} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import React from "react";

export const ResultBackButton = ({issuerId, issuerDisplayName, clientId}) => {
    const navigate = useNavigate();
    return (
        <Button
            data-testid={issuerId}
            onClick={() => {navigate(`/issuers/${issuerId}`, {
                state: {
                    issuerDisplayName,
                    clientId
                }
            })}}
            style={{margin: '12px auto', color: 'black', border: '1px solid #E86E04', borderRadius: '12px', padding: '8px 12px'}}>
            <ArrowBackIcon style={{fontSize: '16px', marginRight: '12px'}}/>Back
        </Button>
    )
}
