import React from "react";
import {Issuer} from "./Issuer";
import {useSelector} from "react-redux";
import {IssuerObject} from "../../types/data";
import {RootState} from "../../types/redux";
import {useTranslation} from "react-i18next";
import {EmptyListContainer} from "../Common/EmptyListContainer";
import {RequestStatus} from "../../hooks/useFetch";
import {SpinningLoader} from "../Common/SpinningLoader";

export const IssuersList: React.FC<IssuersListProps> = ({state}) => {
    const issuers = useSelector((state: RootState) => state.issuers);
    const {t} = useTranslation("HomePage");

    if (state === RequestStatus.LOADING) {
        return <SpinningLoader/>
    }

    if (state === RequestStatus.ERROR || !issuers?.issuers || (issuers?.issuers && issuers?.issuers?.length === 0)) {
        return <EmptyListContainer content={t("emptyContainerContent")}/>
    }

    return <React.Fragment>
        <div data-testid="Issuers-List-Container"
             className="container mx-auto my-auto mt-8 px-4 flex-1 flex flex-col">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {issuers.issuers.map((issuer: IssuerObject, index: number) =>
                    <Issuer issuer={issuer} index={index}/>)}
            </div>
        </div>
    </React.Fragment>
}

type IssuersListProps = {
    state: RequestStatus;
}
