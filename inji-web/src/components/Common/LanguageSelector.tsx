import React, {useState} from "react";
import {VscGlobe} from "react-icons/vsc";
import {LanguagesSupported, switchLanguage} from "../../utils/i18n";
import {useDispatch, useSelector} from "react-redux";
import {storeLanguage} from "../../redux/reducers/commonReducer";
import {RootState} from "../../types/redux";
import {FaCheck} from "react-icons/fa6";
import {IoIosArrowDown} from "react-icons/io";
import {RiArrowDownSFill, RiArrowUpSFill} from "react-icons/ri";

export const LanguageSelector: React.FC = () => {
    const dispatch = useDispatch();
    let language = useSelector((state: RootState) => state.common.language);
    language = language ?? 'en';
    const [isOpen, setIsOpen] = useState(false);


    interface DropdownItem {
        label: string;
        value: string;
    }

    const handleChange = (item: DropdownItem) => {
        setIsOpen(false);
        switchLanguage(item.value);
        dispatch(storeLanguage(item.value));
    }

    return <div className={"flex flex-row justify-center items-center"}>
        <VscGlobe
            data-testid="Language-Selector-Icon"
            size={30} color={'orange'}/>
        <div className="relative inline-block ml-1">
            <button
                type="button"
                className="inline-flex items-center font-bold"
                onClick={() => setIsOpen(!isOpen)}>
                <p>{LanguagesSupported.find(lang => lang.value === language)?.label}</p>
                {isOpen ? <RiArrowUpSFill size={20} /> : <RiArrowDownSFill size={20} /> }
            </button>

            {isOpen && (
                <div
                    className="absolute w-40 right-0 mt-3 rounded-md shadow-lg bg-white overflow-hidden"
                    onClick={() => setIsOpen(false)}>
                    <ul className="py-1 divide-y divide-gray-200">
                        {LanguagesSupported.map((item) => (
                            <li key={item.value}
                                className={language === item.value ? "text-light-primary dark:text-dark-primary" : ""}>
                                <button
                                    type="button"
                                    className="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 flex items-center justify-between flex-row"
                                    onClick={() => handleChange(item)}>
                                    {item.label}
                                    {language === item.value && <FaCheck color={"orange"}/>}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    </div>
}
