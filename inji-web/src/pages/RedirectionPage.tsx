import React, {useEffect, useState} from 'react';
import {getActiveSession, removeActiveSession} from "../utils/sessions";
import {useLocation} from "react-router-dom";
import {NavBar} from "../components/Common/NavBar";
import {useFetch} from "../hooks/useFetch";
import {DownloadResult} from "../components/Redirection/DownloadResult";
import {api, MethodType} from "../utils/api";
import {SessionObject} from "../types/data";
import {useTranslation} from "react-i18next";

export const RedirectionPage: React.FC = () => {

    const {error, fetchRequest} = useFetch();
    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const redirectedSessionId = searchParams.get("state");
    const activeSessionInfo: SessionObject | {} = getActiveSession(redirectedSessionId);
    const {t} = useTranslation("RedirectionPage");
    const [session, setSession] = useState<SessionObject>(null);

    useEffect(() => {
        const fetchToken = async () => {

            if (Object.keys(activeSessionInfo).length > 0) {
                const code = searchParams.get("code");
                const urlState = searchParams.get("state");
                const clientId = activeSessionInfo?.clientId;
                const codeVerifier = activeSessionInfo?.codeVerifier;
                const issuerId = activeSessionInfo?.issuerId;
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
                const requestBody = new URLSearchParams(bodyJson);

                const response = await fetchRequest(api.fetchToken(issuerId) + `?code=${code}&clientId=${clientId}&codeVerifier=${codeVerifier}`, MethodType.POST, requestBody);
                await api.invokeDownloadCredential(issuerId, certificateId, response?.access_token)
                if (urlState != null) {
                    removeActiveSession(urlState);
                }
            } else {
                setSession(null);
            }
        }
        fetchToken();

    }, [])


    return <div data-testid="Redirection-Page-Container">
        <NavBar title={activeSessionInfo?.issuerId} search={false}/>
        {(!error && session) &&
            <DownloadResult title={t("success.title")} subTitle={t("success.subTitle")} success={true}/>}
        {!session &&
            <DownloadResult title={t("error.invalidSession.title")} subTitle={t("error.invalidSession.subTitle")}
                            success={false}/>}
        {error &&
            <DownloadResult title={t("error.generic.title")} subTitle={t("error.generic.subTitle")} success={false}/>}

    </div>
}
