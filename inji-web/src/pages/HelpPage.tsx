import React from "react";
import {HelpAccordion} from "../components/Help/HelpAccordion";
import {NavBar} from "../components/Common/NavBar";
import {useTranslation} from "react-i18next";

export const HelpPage: React.FC = () => {
    const {t} = useTranslation("HelpPage")
    return <React.Fragment>
        <div className={"bg-iw-background"} data-testid="Help-Page-Container">
            <NavBar title={t("title")} search={false} link={"/"}/>
            <div className="container mx-auto mt-8 ">
                <HelpAccordion/>
            </div>
        </div>
    </React.Fragment>
}
