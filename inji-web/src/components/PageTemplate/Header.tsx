import React from "react";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {LanguageSelector} from "../Common/LanguageSelector";
import {ThemeMode} from "../Common/ThemeMode";
import {NavBar} from "../Common/NavBar";

export const Header: React.FC = () => {

    const {t} = useTranslation("PageTemplate");
    const navigate = useNavigate();

    return (
        <header data-testid="Header-Container"
                className="fixed top-0 left-0 right-0 bg-light-background dark:bg-dark-background  py-4 shadow-sm">
            <div className="container mx-auto flex justify-between items-center px-4">
                <div onClick={() => navigate("/")}>
                    <img src={require("../../assets/InjiWebLogo.png")}
                         className={"h-13 w-28 cursor-pointer"}
                         data-testid="Header-InjiWeb-Logo"
                         alt="Inji Web Logo"/>
                </div>
                <nav>
                    <ul className="flex space-x-4 items-center">
                        <li data-testid="Header-Menu-Help">
                            <div onClick={() => navigate("/help")}
                                 className="text-light-title dark:text-dark-title font-bold cursor-pointer">{t("Header.help")}</div>
                        </li>
                        <li data-testid="Header-Menu-AboutInji"><a href="https://docs.mosip.io/inji/inji-web/overview"
                                                                   target="_blank"
                                                                   rel="noreferrer"
                                                                   className="text-light-title dark:text-dark-title font-bold">{t("Header.aboutInji")}</a>
                        </li>
                        <li data-testid="Header-Menu-LanguageSelector"><LanguageSelector/></li>
                    </ul>
                </nav>
            </div>
        </header>
    )

}
