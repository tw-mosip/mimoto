import React, { useEffect, useState } from 'react';
import {Grid, IconButton, Typography, Autocomplete, TextField, Paper, Button} from "@mui/material";
import styled from "@emotion/styled";
import Box from "@mui/material/Box";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import Image from '../../assets/Background.svg';
import SearchIcon from '@mui/icons-material/Search';
import _axios from 'axios';
import {FETCH_ISSUERS_URL, getSearchIssuersUrl} from '../../utils/config';
import { useNavigate } from 'react-router-dom';
import {removeUinAndESignetIssuers} from "../../utils/misc";

export const StyledHeader = styled(Box)`
    opacity: 1;
    height: 400px;
    width: 100%;
    display: flex; 
    justify-items: center; 
    align-content: center;
    align-items: center
`;

const StyledGrid = styled(Grid)`
    max-width: 1140px;
    margin: auto;
    display: flex;
    align-items: center;
    height: 100%;
    align-items: center;
    justify-content: center;
    align-content: center;
`;

const BackArrow = styled(ArrowBackIcon)`
    font-size: 22px;
`;

const IssuerTitle = styled(Typography)`
    font-size: 22px;
    font-weight: 600;
    font-family: Inter;
`;

const PageTitle = styled(Typography)`
    margin: 10px;
    text-align: center;
    font-weight: 500;
    font: normal normal 500 30px/16px Inter;
    letter-spacing: 0px;
    color: #04051D;
    opacity: 1;
`;

const PageSubTitle = styled(Typography)`
    margin: 10px;
    text-align: center;
    font: normal normal normal 18px/16px Inter;
    letter-spacing: 0px;
    color: #717171;
    opacity: 1;
`;

let abortController = new AbortController();

const getSelectedIssuerObject = (formattedOptions, name) => {
    let issuerDetails = formattedOptions.filter(option => option.label === name);
    return issuerDetails?.length === 1 ? issuerDetails[0] : {}
}

function SearchIssuers() {
    const navigate = useNavigate();
    const [formatedOptions, setFormatedOptions] = useState([]);
    const [loadingIssuers, setLoadingIssuers] = useState(false);

    function getReqValue (value) {
        if (value) {
            let reqValue = getSelectedIssuerObject(formatedOptions, value);
            navigate(`/issuers/${reqValue?.value}`, {state: {issuerDisplayName: reqValue?.title, clientId: reqValue?.clientId}} )
        }
    }

    function setFilterOptions(event) {
        let value = event?.target?.value;
        if (event?.type === 'click' ) {
            value = event?.target?.outerText
            getReqValue(value)
        }

        // Clear the previous state and abort the last request (search string has changed)
        setFormatedOptions([]);
        abortController.abort();
        abortController = new AbortController();

        // Do not show anything if the search box is empty
        if (!(!!value)) {
            setLoadingIssuers(false);
            return;
        }

        setLoadingIssuers(true);
        _axios.get(getSearchIssuersUrl(value), {
            signal: abortController.signal
        })
            .then(response => {
                if (response?.data?.response?.issuers) {
                    setFormatedOptions(response?.data?.response?.issuers
                        .map((option) => {
                            return {
                                label: option?.display[0].title,
                                value: option?.credential_issuer,
                                clientId: option?.client_id,
                                title: option?.display[0].title
                            }
                        })
                        // remove these 2 issuers
                        .filter(option => removeUinAndESignetIssuers(option.label))
                    );
                    setLoadingIssuers(false);
                }
            })
            .catch(error => {
                // The older requests are aborted as we type and the search string changes
                if (error.code === 'ERR_CANCELED') return;
                console.error('Error fetching issuers:', error);
                setLoadingIssuers(false);
            });
    }

    function onClickRedirect(event) {
        // event.preventDefault();
        console.log(event.type);
        let value = event?.target?.value;

        if( value && value?.length> 0) {
            const issuerDetails = getSelectedIssuerObject(formatedOptions, value);
            navigate(`/issuers/${value}`, {state: {issuerDisplayName: issuerDetails?.title, clientId: issuerDetails?.clientId}});
        }

    }
    return (
        // <StyledHeader>

                <Box                 
                style={{
                    background: `transparent url(${Image}) 0% 0% no-repeat padding-box`,
                    opacity: 1,
                    height: '430px',
                    width: '100%',
                    backgroundImageWidth: '100%',
                    backgroundSize: 'cover'
                    
                }}>
                    <StyledGrid container>
                        <Grid item xs={12}>
                            <Box style={{display: 'flex', justifyItems: 'center', alignContent: 'center', alignItems: 'center', justifyContent: 'center',margin:10}}>
                                <PageTitle >
                                    Downloading a credential is one-click away!
                                </PageTitle >
                            </Box>
                        </Grid>
                        <Grid item xs={12}>
                        <Box style={{display: 'flex', justifyItems: 'center', alignContent: 'center', alignItems: 'center', justifyContent: 'center', margin:10}}>
                                <PageSubTitle>
                                Please search for the issuer and in the next step, select credential to download.
                                </PageSubTitle>
                            </Box>
                        </Grid>
                        <Grid item xs={12} style={{maxWidth: 800, marginTop: 70}}>
                            <Autocomplete
                                loading={loadingIssuers}
                                options={formatedOptions}
                                freeSolo
                                getOptionLabel={option => option.label} // Access label from option object
                                onClick={onClickRedirect}
                                onInputChange={setFilterOptions}
                                autoComplete
                                renderInput={(params) => (
                                    <Paper
                                    component="form"
                                    sx={{ p: '2px 4px', display: 'flex', alignItems: 'center' }}>
                                    <IconButton type="button" sx={{ p: '10px' }} aria-label="search">
                                      <SearchIcon />
                                    </IconButton>

                                    <TextField
                                        {...params}
                                        // label="Search issuers"
                                        variant="standard"
                                        InputProps={{
                                            ...params.InputProps,
                                            type: 'search',
                                            disableUnderline: true,
                                            onKeyDown: (e) => {
                                                // Stop from redirecting after pressing enter
                                                if (e.key === 'Enter') {
                                                    e.preventDefault();
                                                }
                                            }
                                        }}
                                        autoHighlight={true}
                                    />
                                  </Paper>
                                    
                                )}
                            />
                        </Grid>
                </StyledGrid> 
            </Box>



        // </StyledHeader>
    );
}

export default SearchIssuers;
