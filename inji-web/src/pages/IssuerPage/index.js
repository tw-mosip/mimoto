import React, { useEffect, useState } from 'react';
import PageTemplate from "../PageTemplate";
import Header from "./Header";
import CertificateList from "./CertificateList";
import _axios from 'axios';
import {getCredentialsSupportedUrl, getSearchIssuersUrl} from "../../utils/config";
import {useParams, useLocation, useNavigate} from 'react-router-dom';
import LoadingScreen from '../../utils/LoadingScreen';

function Issuer() {
    const { issuerId, displayName } = useParams();
    const [credentialsList, setCredentialsList] = useState([]);
    const [defaultList, setDefaultList] = useState([]);
    const [authEndpoint, setAuthEndpoint] = useState();

    const location = useLocation();
    const [issuerClientId, setIssuerClientId] = useState(location.state ? location.state['clientId'] : undefined);
    const issuerDisplayName = location.state?.issuerDisplayName;
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState();

    const navigate = useNavigate();

    useEffect(() => {
        _axios.get(getCredentialsSupportedUrl(issuerId))
        .then(response => {
            setCredentialsList(response?.data?.response?.supportedCredentials);
            setDefaultList(response?.data?.response?.supportedCredentials);
            setAuthEndpoint(response?.data?.response?.authorization_endpoint);
            setLoading(false);
        })
        .catch(error => {
            console.error('Error fetching credentialTypes:', error);
            setErrorMessage(error);
            setLoading(false);
        });
    }, []);

    useEffect(() => {
        // make an api call only when the state is not available
        if (issuerClientId && issuerDisplayName) return;
        if (!issuerClientId && !issuerDisplayName) navigate("/issuers");// We need Display name to fetch the client id
        _axios.get(getSearchIssuersUrl(issuerDisplayName))
            .then((response) => {
                let issuers =  response.data.response.issuers;
                if (issuers.length !== 0) {
                    setIssuerClientId(issuers[0].client_id);
                }
            })
            .catch((error) => {
                console.error('Failed to search for the issuer', issuerId, error);
                setErrorMessage('Error loading issuer details!');
            })
    }, []);

    // TODO: show a loader while loading and error message in case of any errors
    return (
        <PageTemplate>
            <Header issuerDisplayName={issuerDisplayName ? issuerDisplayName : issuerId} credentialsList={credentialsList}
                    loading={loading} updateCredentialsList={setCredentialsList} defaultList={defaultList}/>
            {loading 
                ? <LoadingScreen />
                : <CertificateList credentialList={credentialsList} issuerId={issuerId}
                                   authEndpoint={authEndpoint}
                                   issuerDisplayName={issuerDisplayName} clientId={issuerClientId}/>
            }
        </PageTemplate>
    );
}

export default Issuer;
