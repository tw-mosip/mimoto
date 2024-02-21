import React, { useEffect, useState } from 'react';
import PageTemplate from "../PageTemplate";
import Header from "./Header";
import CertificateList from "./CertificateList";
import _axios from 'axios';
import {getCredentialsSupportedUrl, getSearchIssuersUrl} from "../../utils/config";
import { useParams } from 'react-router-dom';

function Issuer() {
    const { issuerId } = useParams();
    const [credentialsList, setCredentialsList] = useState([]);
    const [defaultList, setDefaultList] = useState([]);
    const [issuerClientId, setIssuerClientId] = useState();
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState();


    useEffect(() => {
        _axios.get(getCredentialsSupportedUrl(issuerId))
        .then(response => {
            if (response?.data?.response?.supportedCredentials)
            setCredentialsList(response?.data?.response?.supportedCredentials);
            setDefaultList(response?.data?.response?.supportedCredentials);
            setLoading(false);
        })
        .catch(error => {
            console.error('Error fetching credentialTypes:', error);
            setErrorMessage(error);
            setLoading(false);
        });
    }, []);

    useEffect(() => {
        _axios.get(getSearchIssuersUrl(issuerId))
            .then((response) => {
                let issuers =  response.data.response.issuers;
                if (issuers.length !== 0) {
                    setIssuerClientId(issuers[0].client_id);
                }
            })
            .catch((error) => {
                console.error('Failed to search for the issuer', issuerId, error);
                setErrorMessage('Error loading issuer client id');
            })
    }, []);

    // TODO: show a loader while loading and error message in case of any errors
    return (
        <PageTemplate>
            <Header issuer={issuerId} credentialsList={credentialsList} updateCredentialsList={setCredentialsList} defaultList={defaultList}/>
            <CertificateList credentialList={credentialsList} issuerId={issuerId} clientId={issuerClientId}/>
        </PageTemplate>
    );
}

export default Issuer;
