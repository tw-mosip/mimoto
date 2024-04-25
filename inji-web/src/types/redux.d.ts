import {reduxStore} from "../redux/reduxStore";

export type RootState = ReturnType<typeof reduxStore.getState>

export type CredentialsReducerActionType = {
    STORE_CREDENTIAL: string
}
export type CommonReducerActionType = {
    STORE_LANGUAGE: string
}
export type IssuersReducerActionType = {
    STORE_SELECTED_ISSUER: string;
    STORE_ISSUERS: string;
}
