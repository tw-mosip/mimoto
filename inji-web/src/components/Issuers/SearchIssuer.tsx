import React from "react";
import {useFetch} from "../../hooks/useFetch";
import {useTranslation} from "react-i18next";
import {FaSearch} from 'react-icons/fa';
import {useDispatch} from "react-redux";
import {storeIssuers} from "../../redux/reducers/issuersReducer";
import {api, MethodType} from "../../utils/api";
import {IssuerObject} from "../../types/data";

export const SearchIssuer: React.FC = () => {

    const {fetchRequest} = useFetch();
    const {t} = useTranslation("HomePage");
    const dispatch = useDispatch();
    const filterIssuers = async (searchText: string) => {
        const response = await fetchRequest(api.searchIssuers(searchText), MethodType.GET, null);
        dispatch(storeIssuers(response?.response?.issuers.filter((issuer: IssuerObject) => issuer.credential_issuer === 'Sunbird')))
    }

    return <React.Fragment>
        <div data-testid="Search-Issuer-Container" className="mb-8 w-full flex pb-14">
            <div className="relative w-3/5 mx-auto flex justify-center items-center">
                <FaSearch data-testid="Search-Issuer-Icon"
                          className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 ml-2" size={20}/>
                <input
                    data-testid="Search-Issuer-Input"
                    type="text"
                    placeholder={t("Intro.searchText")}
                    onChange={event => filterIssuers(event.target.value)}
                    className="py-6 pl-16 pr-4 rounded-md w-full shadow-xl  focus:outline-none"
                />
            </div>
        </div>
    </React.Fragment>
}
