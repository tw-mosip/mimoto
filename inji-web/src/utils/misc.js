import axios from "axios";
import {getVcDownloadAPI} from "./config";
import {CustomError} from "../errors/CustomError";

export const getCertificatesAutoCompleteOptions = (credentialsList) => credentialsList.map(cred => {
    return {label: cred.display[0].name, value: cred.display[0].name}
});

export const getUrlParamsMap = (searchString) => {
    let searchParams = searchString?.replace("?", "")
        .split("&");
    if (!searchParams || searchParams.length === 0) {
        return {};
    }
    let searchParamsMap = {};
    searchParams.forEach(param => {
        let keyValue = param.split('=');
        // TODO: handle the case if there is no value after '='
        searchParamsMap[keyValue[0]] = keyValue[1];
    });
    return searchParamsMap;
}

export const getFileName = (contentDispositionHeader) => {
    if (!contentDispositionHeader) return null;
    // sample header value => Content-Disposition: 'attachment; filename="x"' and we need "x"
    const filenameMatch = contentDispositionHeader.match(/filename=(.*?)(;|$)/);
    if (filenameMatch && filenameMatch.length > 1) {
        return filenameMatch[1];
    }
    return null;
};

export const downloadCredentials = async (issuerId, certificateId, token) => {
    let response;
    try {
        response = await axios.get(getVcDownloadAPI(issuerId, certificateId), {
            headers: {/*
            'Authorization': 'Bearer ' + token,*/
                'Bearer': token,
                'Cache-Control': 'no-cache, no-store, must-revalidate'
            },
            responseType: 'blob' // Set the response type to 'arraybuffer' to receive binary data
        });
    } catch (exception) {
        response = exception.response;
    }

    const blob = new Blob([response.data], { type: response.headers['content-type'] });

    if (response.status === 500) {
        let responseObject = await blob.text();
        throw new CustomError(responseObject, {error: responseObject});
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
};

// Not present in this list
export const removeUinAndESignetIssuers = (name) => ["Download MOSIP Credentials via OTP", "Download MOSIP Credentials"].indexOf(name) === -1;
