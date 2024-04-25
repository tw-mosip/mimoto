import {getFileName} from "./misc";
import {CodeChallengeObject, IssuerObject, ResponseTypeObject} from "../types/data";

export enum MethodType {
    GET,
    POST
}

export class api {

    //static mimotoHost = "/v1/mimoto";
    static mimotoHost = window.location.origin + "/v1/mimoto";

    static authorizationRedirectionUrl = window.location.origin + "/redirect";
    static fetchIssuers = () => "/issuers"
    static searchIssuers = (searchText: string) => `/issuers?search=${searchText}`
    static fetchSpecificIssuer = (issuerId: string) => `/issuers/${issuerId}`
    static fetchCredentialTypes = (issuerId: string) => `/issuers/${issuerId}/credentialTypes`
    static searchCredentialType = (issuerId: string, searchText: string) => `/issuers/${issuerId}/credentialTypes?search=${searchText}`
    static authorization = (currentIssuer: IssuerObject, state: string, code_challenge: CodeChallengeObject) => {
        return `${currentIssuer.authorization_endpoint}` +
            `?response_type=code&` +
            `client_id=${currentIssuer.client_id}&` +
            `scope=${currentIssuer.scopes_supported[0]}&` +
            `redirect_uri=${api.authorizationRedirectionUrl}&` +
            `state=${state}&` +
            `code_challenge=${code_challenge.codeChallenge}&` +
            `code_challenge_method=S256`;
    }
    static fetchToken = (issuer: string) => `/get-token/${issuer}`;
    static vcDownload = (issuerId: string, credentialId: string) => `${api.mimotoHost}/${issuerId}/credentials/${credentialId}/download`;
    static fetchRequest = async (uri: string, method: MethodType, body?: any) => {
        let responseJson: (ResponseTypeObject) = {};
        const response = await fetch(`${api.mimotoHost}${uri}`, {
            method: MethodType[method],
            headers: {
                "Content-Type": "application/json",
            },
            body: body && JSON.stringify(body),
        });
        if (!response.ok) {
            throw new Error(`Exception Occurred while invoking ${uri}`);
        }
        responseJson = await response.json();
        return responseJson;
    };

    static invokeDownloadCredential = async (issuerId: string, certificateId: string, token: string) => {
        let response;
        try {
            response = await fetch(api.vcDownload(issuerId, certificateId) + `?token=${token}`, {
                method: "GET",
                headers: {
                    'Bearer': token,
                    'Cache-Control': 'no-cache, no-store, must-revalidate'
                },
                responseType: 'blob' // Set the response type to 'arraybuffer' to receive binary data
            });
        } catch (exception: any) {
            response = exception.response;
        }

        const blob = new Blob([response.data], {type: response.headers['content-type']});

        if (response.status === 500) {
            let responseObject = await blob.text();
            throw new Error(responseObject, {error: responseObject});
        }
        let fileName = getFileName(response.headers['content-disposition']) ?? `${certificateId}.pdf`;

        // Create a temporary URL for the Blob
        const url = window.URL.createObjectURL(blob);

        // Create a temporary link element
        const link = document.createElement('a');
        link.href = url;

        // Set the filename for download
        link.setAttribute('download', fileName);
        link.setAttribute('target', '_blank'); // Open the link in a new tab or window

        // Trigger a click event to download the file
        document.body.appendChild(link);
        link.click();

        // Clean up
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    }
}


