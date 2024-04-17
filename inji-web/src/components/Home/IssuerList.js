import React from 'react';
import GridComponent from "../molecules/GridComponent";
import {Typography} from "@mui/material";
import Box from "@mui/material/Box";
import styled from "@emotion/styled";

import { useNavigate } from 'react-router-dom';
import Paper from "@mui/material/Paper";


const IssuersBox = styled(Box)`
    margin: 30px auto;
    max-width: 1140px;
    background: transparent linear-gradient(180deg, #FFFFFF 0%, #FAFDFF 100%) 0% 0% no-repeat padding-box;
    opacity: 1;
`;

const Title = styled(Typography)`
    font: normal normal 600 20px/16px Inter;
    margin-bottom: 10px;
`;

const CenteredTypography = styled(Typography)`
  text-align-last: center;
`

const getCardsData = (issuersList, navigate) => {
    return issuersList.map(issuer => {
        return {
            imageUrl: issuer.display[0].logo.url,
            title: issuer.display[0].title,
            icon: null,
            onClick: () => {
                navigate(`/issuers/${issuer.credential_issuer}`, {state: {issuerDisplayName: issuer.display[0].title, clientId: issuer.client_id}})

            },
            clickable: true
        }
    });
}

const PaperStyled = styled(Paper)`
    margin: 30px auto;
  padding: 40px;
    max-width: 1140px;
  
`;

function IssuersList({issuersList}) {
    const navigate = useNavigate();
    const cards = getCardsData(issuersList, navigate);
    return (
        <IssuersBox>
            <Title variant='h6'>
                List of Issuers
            </Title>
            {(issuersList.length === 0) ? <PaperStyled >
                <CenteredTypography>{"No issuers found. Please try again later."}</CenteredTypography>
            </PaperStyled> :<GridComponent cards={cards}/> }

        </IssuersBox>
    );
}

export default IssuersList;
