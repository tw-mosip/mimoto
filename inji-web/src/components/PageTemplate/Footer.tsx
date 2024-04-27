import React from "react";
import {useTranslation} from "react-i18next";

export const Footer: React.FC = () => {

    const {t} = useTranslation("PageTemplate")

    return <footer
        data-testid="Footer-Container"
        className="fixed bottom-0 left-0 right-0 py-4 text-center transform rotate-180 shadow-sm shadow-light-shadow dark:shadow-dark-shadow">
        <div className="container mx-auto">
            <p data-testid="Footer-Text" className="text-black transform rotate-180">{t("Footer.copyRight")}</p>
        </div>
    </footer>;
}
