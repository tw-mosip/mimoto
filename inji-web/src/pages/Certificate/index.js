import React, {useEffect, useState} from 'react';
import PageTemplate from "../PageTemplate";
import Box from "@mui/material/Box";
import {CircularProgress, Grid, Modal, Typography, Button} from "@mui/material";
import ErrorIcon from '@mui/icons-material/Error';
import DoneIcon from '@mui/icons-material/Done';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import Header from "./Header";
import {fetchAccessToken} from "../../utils/oauth-utils";
import {downloadCredentials} from "../../utils/misc";
import {DATA_KEY_IN_LOCAL_STORAGE} from "../../utils/config";

const getCodeVerifierAndClientId = () => {
    let details = JSON.parse(localStorage.getItem(DATA_KEY_IN_LOCAL_STORAGE) || "{}");
    let codeVerifier = details['codeVerifier'];
    let clientId = details['clientId'];
    localStorage.removeItem(DATA_KEY_IN_LOCAL_STORAGE);
    return {clientId, codeVerifier};
}

const getDownloadErrorMessage = (error) => {
    if (!error?.response?.data?.errors) return 'Failed to download the credentials';
    return error.response.data.errors[0].errorMessage;
};

const ErrorComponent = () => {
    return (<ErrorIcon style={{fontSize: '40px', color: 'red', margin: '12px auto'}}/>);
};

const SuccessComponent = () => {
    return (
        <DoneIcon style={{fontSize: '40px', color: 'green', margin: '12px auto'}}/>
    );
}

const ResultBackButton = ({issuerId, issuerDisplayName}) => {
    const navigate = useNavigate();
    return (
        <Button
            onClick={() => {navigate(`/issuers/${issuerId}`, {
                state: {
                    issuerDisplayName
                }
            })}}
            style={{margin: '12px auto', color: 'black', border: '1px solid #E86E04', borderRadius: '12px', padding: '8px 12px'}}>
            <ArrowBackIcon style={{fontSize: '16px', marginRight: '12px'}}/>Back
        </Button>
    )
}

/*
    cases
    code not received
    code received
         get access token failed -
         get access token success
             download fails
             download success
*/

const DisplayComponent = ({message, inProgress}) => {
    const {issuerId} = useParams();
    const issuerDisplayName = useLocation().state?.issuerDisplayName;
    switch (message) {
        case 'Invalid user credentials':
        case 'Failed to verify the user credentials':
        case 'Failed to download the credentials':
            return (<>
                <Grid item xs={12}>
                    <ErrorComponent/>
                </Grid>
                <Grid item xs={12}>
                    <Typography variant='h6' style={{margin: '12px auto'}}>{message}</Typography>
                </Grid>
                <Grid item xs={12}>
                    <ResultBackButton issuerId={issuerId} issuerDisplayName={issuerDisplayName}/>
                </Grid>
            </>);
        case 'Verifying credentials':
        case 'Downloading credentials':
            return (
                <Modal open={inProgress}>
                    <Grid container style={{width: "35%", borderRadius: "12px",
                        margin: "30vh auto", display: 'grid', justifyContent: 'center', justifyItems: 'center', background: "white"}}>
                        <Grid item xs={12}>
                            <CircularProgress style={{fontSize: '24px', margin: '12px auto'}}/>
                        </Grid>
                        <Grid item xs={12}>
                            <Typography variant='h6' style={{margin: '12px auto'}}>{message}</Typography>
                        </Grid>
                    </Grid>
                </Modal>
            );
        case 'Download complete':
            return (
                <>
                    <Grid item xs={12}>
                        <SuccessComponent/>
                    </Grid>
                    <Grid item xs={12}>
                        <Typography variant='h6' style={{margin: '12px auto'}}>{message}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <ResultBackButton issuerId={issuerId} issuerDisplayName={issuerDisplayName}/>
                    </Grid>
                </>
            );
    }
}

function Certificate(props) {
    const [progress, setProgress] = useState(true);
    const [message, setMessage] = useState('Verifying credentials');
    const location = useLocation();
    const { issuerId, certificateId } = useParams();

    useEffect(() => {
        const searchParams = location.search?.replace("?", "")
            .split('&');
        const searchParamsMap = {};
        searchParams.forEach(param => {
            let keyValue = param.split("=");
            searchParamsMap[keyValue[0]] = keyValue[1];
        });
        if (Object.keys(searchParamsMap).indexOf("code") === -1) {
            setMessage('Invalid user credentials');
            setProgress(false);
            return;
        }
        let code = searchParamsMap["code"];
        setMessage('Verifying credentials');
        let {clientId, codeVerifier} = getCodeVerifierAndClientId();

        fetchAccessToken(issuerId, code, clientId, codeVerifier).then(response => {
            let token = response.data.access_token;
            setMessage('Downloading credentials');
            downloadCredentials(issuerId, certificateId, token)
                .then(response => {
                    setMessage('Download complete');
                    setProgress(false);
                })
                .catch(error => {
                    console.error("Error occurred while downloading the credential. Error message: ", error);
                    setMessage(getDownloadErrorMessage(error));
                    setProgress(false);
                });
        })
            .catch(error => {
                setProgress(false);
                setMessage('Failed to verify the user credentials');
            });
    }, []);

    return (
        <PageTemplate>
            <Header/>
            <Box style={{minHeight:'100vh', width:'90%', margin: '40px auto'}}>
                <Grid container style={{ display: 'grid', justifyContent: 'center', justifyItems: 'center'}}>
                    <DisplayComponent message={message} inProgress={progress}/>
                </Grid>
            </Box>
        </PageTemplate>
    );
}

export default Certificate;
