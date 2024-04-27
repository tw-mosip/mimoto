import React from "react";
import {MdDarkMode, MdLightMode} from "react-icons/md";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../types/redux";
import {storeTheme} from "../../redux/reducers/commonReducer";
import {storage} from "../../utils/storage";

export const Theme = {
    LIGHT: 'light',
    DARK: 'dark'
}
export const ThemeMode: React.FC = () => {
    const dispatch = useDispatch();
    const theme = useSelector((state: RootState) => state.common.theme);
    const handleThemeChange = () => {
        const newTheme = theme === Theme.LIGHT ? Theme.DARK : Theme.LIGHT;
        dispatch(storeTheme(newTheme));
        storage.setItem(storage.SELECTED_THEME, newTheme);
    }

    return <div onClick={handleThemeChange}>
        <div className={"p-2 w-20 h-10 rounded-full border-1 border-2 flex justify-center items-center"}>
            <div className={"w-8 h-8 rounded-full flex items-center justify-center"}>
                <MdLightMode size={25} color={theme === Theme.DARK ? "" : "orange"}/>
            </div>
            <div className={"w-8 h-8 rounded-full flex items-center justify-center"}>
                <MdDarkMode size={25} color={theme === Theme.DARK ? "blue" : ""}/>
            </div>
        </div>
    </div>
}
