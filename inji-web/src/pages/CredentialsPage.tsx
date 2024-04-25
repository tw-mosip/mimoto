import React, {useEffect} from "react";
import {useParams} from "react-router-dom";
import {useFetch} from "../hooks/useFetch";
import {NavBar} from "../components/Common/NavBar";
import {CredentialList} from "../components/Credentials/CredentialList";
import {useDispatch} from "react-redux";
import {storeSelectedIssuer} from "../redux/reducers/issuersReducer";
import {storeCredentials} from "../redux/reducers/credentialsReducer";
import {api, MethodType} from "../utils/api";
import {HeaderTile} from "../components/Common/HeaderTile";
import {useTranslation} from "react-i18next";

export const CredentialsPage: React.FC = () => {

    const {fetchRequest} = useFetch();
    const params = useParams<CredentialParamProps>();
    const dispatch = useDispatch();
    const {t} = useTranslation("CredentialsPage");

    useEffect(() => {
        const fetchCall = async () => {
            let response = await fetchRequest(api.fetchSpecificIssuer(params.issuerId), MethodType.GET, null);
            dispatch(storeSelectedIssuer(response?.response));

            response = await fetchRequest(api.fetchCredentialTypes(params.issuerId), MethodType.GET, null);
            dispatch(storeCredentials(response?.response?.supportedCredentials));
        }
        fetchCall();
    }, [])


    return <React.Fragment>
        <div className="bg-white min-h-screen" data-testid="Credentials-Page-Container">
            <NavBar title={params.issuerId} search={true}/>
            <HeaderTile content={t("containerHeading")}/>
            <CredentialList/>
        </div>
    </React.Fragment>
}
