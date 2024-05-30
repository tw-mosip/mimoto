import React, {useEffect, useState} from 'react';
import {getActiveSession, removeActiveSession} from "../utils/sessions";
import {useLocation} from "react-router-dom";
import {NavBar} from "../components/Common/NavBar";
import {RequestStatus, useFetch} from "../hooks/useFetch";
import {DownloadResult} from "../components/Redirection/DownloadResult";
import {api} from "../utils/api";
import {ApiRequest, DisplayArrayObject, SessionObject} from "../types/data";
import {useTranslation} from "react-i18next";
import {downloadCredentialPDF, getBody} from "../utils/misc";
import {getObjectForCurrentLanguage} from "../utils/i18n";
import {storeSelectedIssuer} from "../redux/reducers/issuersReducer";
import {useDispatch} from "react-redux";
import {storeCredentials, storeFilteredCredentials} from "../redux/reducers/credentialsReducer";

export const RedirectionPage: React.FC = () => {

    const {error, state, fetchRequest} = useFetch();
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const dispatch = useDispatch();
    const redirectedSessionId = searchParams.get("state");
    const activeSessionInfo: any = getActiveSession(redirectedSessionId);
    const {t} = useTranslation("RedirectionPage");
    const [session, setSession] = useState<SessionObject | null>(activeSessionInfo);
    const displayObject = getObjectForCurrentLanguage(session?.selectedIssuer?.display ?? []);

    useEffect(() => {
        const fetchToken = async () => {
            if (Object.keys(activeSessionInfo).length > 0) {
                const code = searchParams.get("code") ?? "";
                const urlState = searchParams.get("state") ?? "";
                const clientId = activeSessionInfo?.selectedIssuer.client_id;
                const codeVerifier = activeSessionInfo?.codeVerifier;
                const issuerId = activeSessionInfo?.selectedIssuer.credential_issuer ?? "";
                const certificateId = activeSessionInfo?.certificateId;

                const bodyJson = {
                    'grant_type': 'authorization_code',
                    'code': code,
                    'client_id': clientId,
                    'client_assertion_type': 'urn:ietf:params:oauth:client-assertion-type:jwt-bearer',
                    'client_assertion': '',
                    'redirect_uri': api.authorizationRedirectionUrl,
                    'code_verifier': codeVerifier
                }

                let apiRequest: ApiRequest = api.fetchSpecificIssuer;
                let issuerConfig = await fetchRequest(
                    apiRequest.url(issuerId ?? ""),
                    apiRequest.methodType,
                    apiRequest.headers()
                );
                dispatch(storeSelectedIssuer(issuerConfig?.response));


                apiRequest = api.fetchCredentialTypes2;
                let response = await fetchRequest(
                    apiRequest.url(issuerId ?? ""),
                    apiRequest.methodType,
                    apiRequest.headers()
                );
                dispatch(storeFilteredCredentials(response?.response?.supportedCredentials));
                dispatch(storeCredentials(response?.response?.supportedCredentials));


                const requestBody = new URLSearchParams(bodyJson);
                apiRequest = api.fetchToken;
                response = await fetchRequest(
                    apiRequest.url(issuerId),
                    apiRequest.methodType,
                    apiRequest.headers(),
                    requestBody
                );

                const credentialRequestBody = await getBody(response?.access_token, clientId, issuerConfig.response.credential_audience, certificateId);

                apiRequest = api.downloadVc;
                response = await fetchRequest(
                    apiRequest.url(issuerId, certificateId),
                    apiRequest.methodType,
                    apiRequest.headers(response?.access_token),
                    credentialRequestBody
                );
                if (state !== RequestStatus.ERROR) {
                    await downloadCredentialPDF(response, certificateId);
                }
                if (urlState != null) {
                    removeActiveSession(urlState);
                }
            } else {
                setSession(null);
            }
        }
        fetchToken();

    }, [])

    const loadStatusOfRedirection = () => {
        if (!session) {
            return <DownloadResult title={t("error.invalidSession.title")}
                                   subTitle={t("error.invalidSession.subTitle")}
                                   state={RequestStatus.ERROR}/>
        }
        if (state === RequestStatus.LOADING) {
            return <DownloadResult title={t("loading.title")}
                            subTitle={t("loading.subTitle")}
                            state={RequestStatus.LOADING}/>
        }
        if (state === RequestStatus.ERROR && error) {
            return <DownloadResult title={t("error.generic.title")}
                                   subTitle={t("error.generic.subTitle")}
                                   state={RequestStatus.ERROR}/>
        }
        return <DownloadResult title={t("success.title")}
                               subTitle={t("success.subTitle")}
                               state={RequestStatus.DONE}/>
    }

    return <div data-testid="Redirection-Page-Container">
        <NavBar title={displayObject?.name ?? ""} search={false} link={`/issuers/${activeSessionInfo?.selectedIssuer?.credential_issuer}`}/>
        {loadStatusOfRedirection()}
    </div>
}
