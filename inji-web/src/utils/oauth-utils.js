import sha256 from 'crypto-js/sha256';
import Base64 from 'crypto-js/enc-base64';
import {ESIGNET_REDIRECT_URI, getFetchAccessTokenFromCodeApi} from "./config";
import axios from "axios";

export const generateCodeChallenge = () => {
    const verifier = generateRandomString()/*'test'*/;
    const hashedVerifier = sha256(verifier);
    const base64Verifier = Base64.stringify(hashedVerifier);
    return {
        codeChallenge: base64Verifier
            .replace(/=/g, '')
            .replace(/\+/g, '-')
            .replace(/\//g, '_'),
        codeVerifier: verifier
    };
}

export const generateRandomString = (length) => {
    if (!length) length = 43 // 43 bytes long for PKCE
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let randomString = '';
    for (let i = 0; i < 43; i++) {
        const randomIndex = Math.floor(Math.random() * charset.length);
        randomString += charset[randomIndex];
    }
    return randomString;
};


export const fetchAccessToken = async (issuer, code, clientId, codeVerifier) => {
    console.log('code before sending request: ', code);
    const config = {
        method: 'post',
        url: getFetchAccessTokenFromCodeApi(issuer),
        headers: {
            'accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        data: new URLSearchParams({
            'grant_type': 'authorization_code',
            'code': code,
            'client_id': clientId,
            'client_assertion_type': 'urn:ietf:params:oauth:client-assertion-type:jwt-bearer',
            'client_assertion': '', // You may need to add your client assertion here
            'redirect_uri': ESIGNET_REDIRECT_URI,
            'code_verifier': codeVerifier
        })
    };
    console.log("Request config map", config);
    // Make the Axios request
    return axios(config);
};
