/* ESIGNET CONFIG */
export const ESIGNET_UI_URL = process.env.REACT_APP_ESIGNET_UI_URL || "http://192.168.2.186:3001";
export const ESIGNET_REDIRECT_URI = process.env.REACT_APP_ESIGNET_UI_URL || "http://localhost:81";

export const getESignetRedirectURL = (scope, clientId, codeChallenge, state) => {
    return `${ESIGNET_UI_URL}/authorize` +
        `?response_type=code&` +
        `client_id=${clientId}&` +
        `scope=${scope}&` +
        `redirect_uri=${ESIGNET_REDIRECT_URI}&` +
        `state=${state}&` +
        `code_challenge=${codeChallenge}&` +
        `code_challenge_method=S256`;
}


/* MIMOTO CONFIG */
export const MIMOTO_URL = process.env.REACT_APP_MIMOTO_URL || "/mimoto";
export const FETCH_ISSUERS_URL = `${MIMOTO_URL}/residentmobileapp/v2/issuers`;
export const getSearchIssuersUrl = (issuer) => `${MIMOTO_URL}/residentmobileapp/v2/issuers?search=${issuer}`;
export const getCredentialsSupportedUrl = (issuerId) => `${MIMOTO_URL}/residentmobileapp/v2/issuers/${issuerId}/credentials-supported`;

export const FETCH_ACCESS_TOKEN_FROM_CODE_API = `${MIMOTO_URL}/residentmobileapp/v2/issuers/get-token/issuer`;
export const getVcDownloadAPI = (issuerId, credentialId) => `${MIMOTO_URL}/residentmobileapp/v2/issuers/${issuerId}/credentials/${credentialId}/download`;

/* MISC */
export const DATA_KEY_IN_LOCAL_STORAGE = "vcDownloadDetails";
