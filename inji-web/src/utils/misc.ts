import sha256 from 'crypto-js/sha256';
import Base64 from 'crypto-js/enc-base64';

export const generateCodeChallenge = (verifier = generateRandomString()) => {
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

export const generateRandomString = (length = 43) => {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let randomString = '';
    for (let i = 0; i < 43; i++) {
        const randomIndex = Math.floor(Math.random() * charset.length);
        randomString += charset[randomIndex];
    }
    return randomString;
};

export const isObjectEmpty = (object: any) => {
    return object === null || object === undefined || Object.keys(object).length === 0;
}

export const getFileName = (contentDispositionHeader: any) => {
    if (!contentDispositionHeader) return null;
    const filenameMatch = contentDispositionHeader.match(/filename=(.*?)(;|$)/);
    if (filenameMatch && filenameMatch.length > 1) {
        return filenameMatch[1];
    }
    return null;
};

export const downloadCredentialPDF = async (response: any, certificateId: string) => {

    // const blob: Blob = new Blob([response], {type: 'application/pdf'});

    let fileName = `${certificateId}.pdf`;
    // Create a temporary URL for the Blob
    const url = window.URL.createObjectURL(response);

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
