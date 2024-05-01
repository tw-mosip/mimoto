import {storage} from "../../utils/storage";
import {CommonReducerActionType} from "../../types/redux";

const initialState = {
    language: storage.getItem(storage.SELECTED_LANGUAGE) ? storage.getItem(storage.SELECTED_LANGUAGE) : 'en',
}

const CommonReducerAction: CommonReducerActionType = {
    STORE_LANGUAGE: 'STORE_LANGUAGE',
}

export const commonReducer = (state = initialState, actions: any) => {
    switch (actions.type) {
        case CommonReducerAction.STORE_LANGUAGE: {
            return {
                ...state,
                language: actions.language
            }
        }
        default :
            return state;
    }
}

export const storeLanguage = (language: string) => {
    return {
        type: CommonReducerAction.STORE_LANGUAGE,
        language: language
    }
}


