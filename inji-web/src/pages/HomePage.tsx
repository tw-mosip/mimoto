import React, {useEffect} from "react";
import {useFetch} from "../hooks/useFetch";
import {IntroBox} from "../components/Common/IntroBox";
import {SearchIssuer} from "../components/Issuers/SearchIssuer";
import {IssuersList} from "../components/Issuers/IssuersList";
import {useDispatch} from "react-redux";
import {storeIssuers} from "../redux/reducers/issuersReducer";
import {api, MethodType} from "../utils/api";
import {IssuerObject} from "../types/data";
import {HeaderTile} from "../components/Common/HeaderTile";
import {useTranslation} from "react-i18next";

export const HomePage: React.FC = () => {

    const {fetchRequest} = useFetch();
    const dispatch = useDispatch();
    const {t} = useTranslation("HomePage");

    useEffect(() => {
        const fetchCall = async () => {
            const response = await fetchRequest(api.fetchIssuers(), MethodType.GET, null);
            const issuers = response?.response?.issuers.filter((issuer: IssuerObject) => issuer.credential_issuer === "Sunbird")
            dispatch(storeIssuers(issuers));
        }
        fetchCall();
    }, [])

    return <div data-testid="Home-Page-Container">
        <div className="container mx-auto mt-8 px-4 flex-1 flex flex-col">
            <IntroBox/>
            <SearchIssuer/>
        </div>
        <HeaderTile content={t("containerHeading")}/>
        <IssuersList/>
    </div>

}
