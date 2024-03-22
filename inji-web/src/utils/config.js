/* ESIGNET CONFIG */
// nginx config redirects the request to esignet running in the same namespace
export const ESIGNET_UI_URL = process.env.REACT_APP_ESIGNET_UI_URL || "";
// Since it is to be redirected to the same application again
export const ESIGNET_REDIRECT_URI = window.location.origin;

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
export const MIMOTO_URL = process.env.REACT_APP_MIMOTO_URL || "/v1/mimoto";
export const FETCH_ISSUERS_URL = `${MIMOTO_URL}/issuers`;
export const getSearchIssuersUrl = (issuer) => `${MIMOTO_URL}/issuers?search=${issuer}`;
export const getCredentialsSupportedUrl = (issuerId) => `${MIMOTO_URL}/issuers/${issuerId}/credentials-supported`;
export const getFetchAccessTokenFromCodeApi = (issuer) => `${MIMOTO_URL}/get-token/${issuer}`;
export const getVcDownloadAPI = (issuerId, credentialId) => `${MIMOTO_URL}/issuers/${issuerId}/credentials/${credentialId}/download`;

/* MISC */
export const DATA_KEY_IN_LOCAL_STORAGE = "vcDownloadDetails";
