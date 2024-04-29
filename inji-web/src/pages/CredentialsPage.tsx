import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {RequestStatus, useFetch} from "../hooks/useFetch";
import {NavBar} from "../components/Common/NavBar";
import {CredentialList} from "../components/Credentials/CredentialList";
import {useDispatch} from "react-redux";
import {storeSelectedIssuer} from "../redux/reducers/issuersReducer";
import {storeCredentials} from "../redux/reducers/credentialsReducer";
import {api} from "../utils/api";
import {HeaderTile} from "../components/Common/HeaderTile";
import {useTranslation} from "react-i18next";
import {toast} from "react-toastify";

import {ApiRequest} from "../types/data";

export const CredentialsPage: React.FC = () => {

    const {state, fetchRequest} = useFetch();
    const params = useParams<CredentialParamProps>();
    const dispatch = useDispatch();
    const {t} = useTranslation("CredentialsPage");

    useEffect(() => {
        const fetchCall = async () => {
            let apiRequest: ApiRequest = api.fetchSpecificIssuer;
            let response = await fetchRequest(
                apiRequest.url(params.issuerId ?? ""),
                apiRequest.methodType,
                apiRequest.headers()
            );
            dispatch(storeSelectedIssuer(response?.response));

            apiRequest = api.fetchCredentialTypes;
            response = await fetchRequest(
                apiRequest.url(params.issuerId ?? ""),
                apiRequest.methodType,
                apiRequest.headers()
            );
            dispatch(storeCredentials(response?.response?.supportedCredentials));
        }
        fetchCall();
    }, [])

    if (state === RequestStatus.ERROR) {
        toast.error(t("errorContent"));
    }

    return <React.Fragment>
        <div className="bg-light-background dark:bg-dark-background  min-h-screen"
             data-testid="Credentials-Page-Container">
            <NavBar title={params.issuerId ?? ""} search={true} fetchRequest={fetchRequest}/>
            <HeaderTile content={t("containerHeading")}/>
            <CredentialList state={state}/>
        </div>
    </React.Fragment>
}

