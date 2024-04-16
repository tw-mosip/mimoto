import React from 'react';
import {Grid, IconButton, Typography, Autocomplete, TextField} from "@mui/material";
import styled from "@emotion/styled";
import Box from "@mui/material/Box";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useNavigate, useParams } from 'react-router-dom';

export const StyledHeader = styled(Box)`
    background-color: #F2FBFF;
    height: 120px;
    width: 100%;
`;

const StyledGrid = styled(Grid)`
    max-width: 1140px;
    margin: auto;
    display: flex;
    align-items: center;
    height: 100%;
`;

const BackArrow = styled(ArrowBackIcon)`
    font-size: 22px;
    stroke-width: 4px;
`;

const CertificateTitle = styled(Typography)`
    font-size: 22px;
    font-weight: 600;
    font-family: Inter;
`;

function Header({title}) {
    const navigate = useNavigate();
    const { issuerId, certificateId } = useParams();

    return (
        <StyledHeader>
            <StyledGrid container>
                <Grid item xs={6}>
                    <Box style={{display: 'flex', justifyItems: 'center', alignContent: 'center', alignItems: 'center'}}>
                        <IconButton style={{marginRight: '18px'}} onClick={() => {navigate(`/issuers/${issuerId}`)}}>
                            <BackArrow />
                        </IconButton>
                        <CertificateTitle>
                            {title ? title : issuerId}
                        </CertificateTitle>
                    </Box>
                </Grid>
            </StyledGrid>
        </StyledHeader>
    );
}

export default Header;
