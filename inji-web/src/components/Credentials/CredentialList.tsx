import React from "react";
import {Credential} from "./Crendential";
import {useSelector} from "react-redux";
import {CredentialWellknownObject} from "../../types/data";
import {RootState} from "../../types/redux";
import {EmptyListContainer} from "../Common/EmptyListContainer";
import {useTranslation} from "react-i18next";
import {RequestStatus} from "../../hooks/useFetch";
import {SpinningLoader} from "../Common/SpinningLoader";
import {CredentialListProps} from "../../types/components";

export const CredentialList: React.FC<CredentialListProps> = ({state}) => {

    const credentials = useSelector((state: RootState) => state.credentials);
    const {t} = useTranslation("CredentialsPage");

    if (state === RequestStatus.LOADING) {
        return <SpinningLoader/>
    }

    if (state === RequestStatus.ERROR || !credentials?.credentials || (credentials?.credentials && credentials?.credentials.length === 0)) {
        return <EmptyListContainer content={t("emptyContainerContent")}/>
    }

    return <React.Fragment>
        <div data-testid="Credential-List-Container" className="container mx-auto mt-8">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {credentials?.credentials && credentials?.credentials.map((credential: CredentialWellknownObject, index: number) => (
                    <Credential credential={credential} index={index}/>
                ))}
            </div>
        </div>
    </React.Fragment>
}


