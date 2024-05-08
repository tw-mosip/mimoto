import {CredentialsReducerActionType} from "../../types/redux";

const initialState = {
    credentials: []
}

const CredentialsReducerAction: CredentialsReducerActionType = {
    STORE_CREDENTIAL: 'STORE_CREDENTIAL'
}

export const credentialsReducer = (state = initialState, action: any) => {
    switch (action.type) {
        case CredentialsReducerAction.STORE_CREDENTIAL :
            return {
                ...state,
                credentials: action.credentials
            }
        default :
            return state;
    }
}

export const storeCredentials = (credentials: any) => {
    return {
        type: CredentialsReducerAction.STORE_CREDENTIAL,
        credentials: credentials
    }
}
