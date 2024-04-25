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

export const getFileName = (contentDispositionHeader: any) => {
    if (!contentDispositionHeader) return null;
    // sample header value => Content-Disposition: 'attachment; filename="x"' and we need "x"
    const filenameMatch = contentDispositionHeader.match(/filename=(.*?)(;|$)/);
    if (filenameMatch && filenameMatch.length > 1) {
        return filenameMatch[1];
    }
    return null;
};
