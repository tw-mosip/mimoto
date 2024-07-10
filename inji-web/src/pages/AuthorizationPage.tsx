import React, {useEffect} from "react";
import {SpinningLoader} from "../components/Common/SpinningLoader";
import {ApiRequest} from "../types/data";
import {api} from "../utils/api";
import {useFetch} from "../hooks/useFetch";
export const AuthorizationPage:React.FC = () => {

    const {fetchRequest} = useFetch();
    useEffect( () => {
        const queryParams = new URLSearchParams(window.location.search)
        const responseType = queryParams.get("response_type") + "";
        const resource = queryParams.get("resource") + "";
        const clientId = queryParams.get("client_id") + "";
        const redirectUri = queryParams.get("redirect_uri") + "";
        const presentationDefinition = queryParams.get("presentation_definition") + "";

        const apiRequest: ApiRequest = api.presentationAuthorization;
        fetchRequest(
            apiRequest.url(responseType, resource, clientId, redirectUri, presentationDefinition),
            apiRequest.methodType,
            apiRequest.headers()
        );
    },[])

    return <SpinningLoader />
}
