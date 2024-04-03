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

function SearchIssuers({options, setFilteredIssuerList}) {
    const navigate = useNavigate();
    const [formatedOptions, setFormatedOptions] = useState([]);
    const [defaultOptions, setDefaultOptions] = useState(options);
    const [loadingIssuers, setLoadingIssuers] = useState(false);

    useEffect(() => {
        _axios.get(FETCH_ISSUERS_URL)
        .then(response => {
            if (response?.data?.response?.issuers) {
                setDefaultOptions(response?.data?.response?.issuers);
                setFormatedOptions(response?.data?.response?.issuers.map((option) => {
                    return {
                        label: option?.display[0].name ,
                        value: option?.credential_issuer,
                        clientId: option?.client_id
                    }
                }));
            } 
        })
        .catch(error => {
            console.error('Error fetching issuers:', error);
        });

    }, []);


    function getReqValue (value) {
        if (value) {
            let reqValue = formatedOptions.filter(i => i.label === value);
            navigate(`/issuers/${reqValue[0]?.value}`, {state: {issuerDisplayName: value, clientId: reqValue[0]?.clientId}} )
        }
    }

    function setFilterOptions(event) {

        let value = event?.target?.value;

        if (event?.type === 'click' ) {
            value = event?.target?.outerText
            getReqValue(value)
        }

        setLoadingIssuers(true);
        setFormatedOptions([]);
        _axios.get((!!value) ? getSearchIssuersUrl(value) : FETCH_ISSUERS_URL)
            .then(response => {
                if (response?.data?.response?.issuers) {
                    // setFilteredIssuerList(response?.data?.response?.issuers.filter(issuer => issuer?.display[0].name.toLowerCase().includes(value.toLowerCase())));
                    setDefaultOptions(response?.data?.response?.issuers);
                    setFormatedOptions(response?.data?.response?.issuers.map((option) => {
                        return {
                            label: option?.display[0].name ,
                            value: option?.credential_issuer,
                            clientId: option?.client_id
                        }
                    }));
                }
            })
            .catch(error => {
                console.error('Error fetching issuers:', error);
            })
            .finally(() => {
                setLoadingIssuers(false);
            });
    }

    function onClickRedirect(event) {
        // event.preventDefault();
        console.log(event.type);
        let value = event?.target?.value;

        if( value && value?.length> 0) {
        navigate(`/issuers/${value}`)
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
