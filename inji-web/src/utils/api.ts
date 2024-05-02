import {ApiRequest, CodeChallengeObject, IssuerObject} from "../types/data";
import i18n from "i18next";

export enum MethodType {
    GET,
    POST
}

export class api {

    static mimotoHost = "http://localhost:3010";
    // static mimotoHost = window.location.origin + "/v1/mimoto";

    static authorizationRedirectionUrl = window.location.origin + "/redirect";


    static fetchIssuers: ApiRequest = {
        url: () => "/issuers",
        methodType: MethodType.GET,
        headers: () => {
            return {
                "Content-Type": "application/json"
            }
        }
    }
    static searchIssuers: ApiRequest = {
        url: (searchText: string) => `/issuers?search=${searchText}`,
        methodType: MethodType.GET,
        headers: () => {
            return {
                "Content-Type": "application/json"
            }
        }
    }
    static fetchSpecificIssuer: ApiRequest = {
        url: (issuerId: string) => `/issuers/${issuerId}`,
        methodType: MethodType.GET,
        headers: () => {
            return {
                "Content-Type": "application/json"
            }
        }
    }
    static fetchCredentialTypes = {
        url: (issuerId: string) => `/issuers/${issuerId}/credentialTypes`,
        methodType: MethodType.GET,
        headers: () => {
            return {
                "Content-Type": "application/json"
            }
        }
    }

    static searchCredentialType: ApiRequest = {
        url: (issuerId: string, searchText: string) => `/issuers/${issuerId}/credentialTypes?search=${searchText}`,
        methodType: MethodType.GET,
        headers: () => {
            return {
                "Content-Type": "application/json"
            }
        }
    }
    static fetchToken: ApiRequest = {
        url: (issuer: string): string => `/get-token/${issuer}`,
        methodType: MethodType.POST,
        headers: () => {
            return {
                'accept': 'application/json',
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        }
    };
    static downloadVc = {
        url: (issuerId: string, credentialId: string) => `/issuers/${issuerId}/credentials/${credentialId}/download`,
        methodType: MethodType.GET,
        headers: (token: string) => {
            return {
                'Bearer': token,
                'Cache-Control': 'no-cache, no-store, must-revalidate'
            }
        }
    }

    static authorization = (currentIssuer: IssuerObject, state: string, code_challenge: CodeChallengeObject) => {
        return `${currentIssuer.authorization_endpoint}` +
            `?response_type=code&` +
            `client_id=${currentIssuer.client_id}&` +
            `scope=${currentIssuer.scopes_supported[0]}&` +
            `redirect_uri=${api.authorizationRedirectionUrl}&` +
            `state=${state}&` +
            `code_challenge=${code_challenge.codeChallenge}&` +
            `code_challenge_method=S256&`+
            `ui_locales=${i18n.language}`;
    }
}


