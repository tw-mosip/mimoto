import {useLocation, useParams} from "react-router-dom";
import {CircularProgress, Grid, Modal, Typography} from "@mui/material";
import React from "react";
import ErrorIcon from "@mui/icons-material/Error";
import DoneIcon from "@mui/icons-material/Done";
import {ResultBackButton} from "./ResultBackButton";


const ErrorComponent = () => {
    return (<ErrorIcon style={{fontSize: '40px', color: 'red', margin: '12px auto'}}/>);
};

const SuccessComponent = () => {
    return (
        <DoneIcon style={{fontSize: '40px', color: 'green', margin: '12px auto'}}/>
    );
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
export const DisplayComponent = ({message, inProgress, clientId}) => {
    const {issuerId} = useParams();
    const issuerDisplayName = useLocation().state?.issuerDisplayName;
    switch (message) {
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
                        <ResultBackButton issuerId={issuerId} issuerDisplayName={issuerDisplayName} clientId={clientId}/>
                    </Grid>
                </>
            );
        case 'Invalid user credentials':
        case 'Failed to verify the user credentials':
        case 'Failed to download the credentials':
        default:
            return (<>
                <Grid item xs={12}>
                    <ErrorComponent/>
                </Grid>
                <Grid item xs={12}>
                    <Typography variant='h6' style={{margin: '12px auto'}}>{message}</Typography>
                </Grid>
                <Grid item xs={12}>
                    <ResultBackButton issuerId={issuerId} issuerDisplayName={issuerDisplayName} clientId={clientId}/>
                </Grid>
            </>);

    }
}
