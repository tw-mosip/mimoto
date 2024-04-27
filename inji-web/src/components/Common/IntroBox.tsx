import React from "react";
import {useTranslation} from "react-i18next";

export const IntroBox: React.FC = () => {
    const {t} = useTranslation("HomePage");
    return <React.Fragment>
        <div data-testid="IntroBox-Container" className="text-center mb-8 pt-20 pb-20">
            <h2 data-testid="IntroBox-Text"
                className="text-2xl font-bold text-light-title dark:text-dark-title">{t("Intro.title")}</h2>
            <p data-testid="IntroBox-SubText"
               className="mt-2 text-light-subTitle dark:text-dark-subTitle">{t("Intro.subTitle")}</p>
        </div>
    </React.Fragment>
}
