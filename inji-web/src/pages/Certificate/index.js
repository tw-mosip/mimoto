import React, {useEffect, useState} from 'react';
import PageTemplate from "../PageTemplate";
import Box from "@mui/material/Box";
import {Grid, Button} from "@mui/material";
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import Header from "./Header";
import {fetchAccessToken} from "../../utils/oauth-utils";
import {downloadCredentials} from "../../utils/misc";
import {DATA_KEY_IN_LOCAL_STORAGE} from "../../utils/config";
import {CustomError} from "../../errors/CustomError";
import {DisplayComponent} from "./DisplayComponent";

const useCodeVerifierAndClientId = () => {
    const [details, setDetails] = useState(JSON.parse(localStorage.getItem(DATA_KEY_IN_LOCAL_STORAGE) || "{}"));
    localStorage.removeItem(DATA_KEY_IN_LOCAL_STORAGE);
    let codeVerifier = details['codeVerifier'];
    let clientId = details['clientId'];
    return {clientId, codeVerifier};
}

const getDownloadErrorMessage = (error) => {
    try {
        if (error instanceof CustomError) {
            let responseErrorObject = JSON.parse(error.details.error);
            if (responseErrorObject?.errors)
                return responseErrorObject.errors[0].errorMessage;
        }
    } catch (exception) {}
    return 'Failed to download the credentials';
};

function Certificate(props) {
    const [progress, setProgress] = useState(true);
    const [message, setMessage] = useState('Verifying credentials');
    const location = useLocation();
    const { issuerId, certificateId } = useParams();
    let {clientId, codeVerifier} = useCodeVerifierAndClientId();

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
                    <DisplayComponent message={message} inProgress={progress} clientId={clientId}/>
                </Grid>
            </Box>
        </PageTemplate>
    );
}

export default Certificate;
