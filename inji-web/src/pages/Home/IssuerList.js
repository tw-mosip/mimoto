import React from 'react';
import GridComponent from "../../components/molecules/GridComponent";
import {Typography} from "@mui/material";
import Box from "@mui/material/Box";
import styled from "@emotion/styled";

import { useNavigate } from 'react-router-dom';
import CustonDownloadButton from "../../components/atoms/CustomDownloadButton.js";


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

const getCardsData = (issuersList, navigate) => {
    return issuersList.map(issuer => {
        return {
            imageUrl: issuer.display[0].logo.url,
            title: issuer.display[0].name,
            icon: null,
            onClick: () => {
                navigate(`/issuers/${issuer.credential_issuer}`, {state: {issuerDisplayName: issuer.display[0].name, clientId: issuer.client_id}})

            },
            clickable: true
        }
    });
}

function IssuersList({issuersList}) {
    const navigate = useNavigate();
    const cards = getCardsData(issuersList, navigate);
    return (
        <IssuersBox>
            <Title variant='h6'>
                List of Issuers
            </Title>
            <GridComponent cards={cards}/>
        </IssuersBox>
    );
}

export default IssuersList;
