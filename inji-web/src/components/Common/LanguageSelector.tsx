import React, {useState} from "react";
import {VscGlobe} from "react-icons/vsc";
import {switchLanguage} from "../../utils/i18n";
import {useDispatch, useSelector} from "react-redux";
import {storeLanguage} from "../../redux/reducers/commonReducer";
import {RootState} from "../../types/redux";

export const LanguageSelector: React.FC = () => {
    const dispatch = useDispatch();
    let language = useSelector((state: RootState) => state.common.language);
    language = language ? language : 'en';
    const [currentLanguage, setCurrentLanguage] = useState(language);

    const handleChange = (language: string) => {
        switchLanguage(language);
        dispatch(storeLanguage(language));
    }

    return <React.Fragment>
        <div
            data-testid="Language-Selector-Container"
            className="relative flex flex-row items-center font-bold bg-white rounded leading-tight">
            <VscGlobe
                data-testid="Language-Selector-Icon"
                size={30} color={"orange"}/>
            <select
                defaultValue={currentLanguage}
                id="language"
                className={"cursor-pointer"}
                data-testid="Language-Selector-Options"
                onChange={(event) => handleChange(event.target.value)}>
                <option value={"en"}>English</option>
                <option value={"ta"}>தமிழ்</option>
                <option value={"kn"}>ಕನ್ನಡ</option>
                <option value={"hi"}>हिंदी</option>
                <option value={"fr"}>Français</option>
            </select>
        </div>
    </React.Fragment>
}
