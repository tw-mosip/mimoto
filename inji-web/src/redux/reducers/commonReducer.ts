import {storage} from "../../utils/storage";
import {CommonReducerActionType} from "../../types/redux";
import {Theme} from "../../components/Common/ThemeMode";

const initialState = {
    language: storage.getItem(storage.SELECTED_LANGUAGE) ? storage.getItem(storage.SELECTED_LANGUAGE) : 'en',
    theme: storage.getItem(storage.SELECTED_THEME) ? storage.getItem(storage.SELECTED_THEME) : Theme.LIGHT
}

const CommonReducerAction: CommonReducerActionType = {
    STORE_LANGUAGE: 'STORE_LANGUAGE',
    STORE_THEME: 'STORE_THEME'
}

export const commonReducer = (state = initialState, actions: any) => {
    switch (actions.type) {
        case CommonReducerAction.STORE_LANGUAGE: {
            return {
                ...state,
                language: actions.language
            }
        }
        case CommonReducerAction.STORE_THEME: {
            return {
                ...state,
                theme: actions.theme
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

export const storeTheme = (theme: string) => {
    return {
        type: CommonReducerAction.STORE_THEME,
        theme: theme
    }
}


