import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {FaSearch} from 'react-icons/fa';
import {useDispatch} from "react-redux";
import {storeIssuers} from "../../redux/reducers/issuersReducer";
import {api} from "../../utils/api";
import {ApiRequest, IssuerObject} from "../../types/data";
import {IoCloseCircleSharp} from "react-icons/io5";
import {SearchIssuerProps} from "../../types/components";

export const SearchIssuer: React.FC<SearchIssuerProps> = (props) => {

    const {t} = useTranslation("HomePage");
    const dispatch = useDispatch();
    const [searchText, setSearchText] = useState("");
    const filterIssuers = async (searchText: string) => {
        setSearchText(searchText);
        const apiRequest: ApiRequest = api.searchIssuers;
        const response = await props.fetchRequest(
            apiRequest.url(searchText),
            apiRequest.methodType,
            apiRequest.headers()
        );
        dispatch(storeIssuers(response?.response?.issuers.filter((issuer: IssuerObject) => issuer.protocol !== 'OTP')))
    }

    return <React.Fragment>
        <div className={"flex justify-center items-center w-full"}>
            <div data-testid="Search-Issuer-Container" className="w-8/12 flex justify-center items-center bg-iw-background shadow-md shadow-iw-shadow">
                <FaSearch data-testid="Search-Issuer-Search-Icon"
                          color={'var(--iw-color-searchIcon)'}
                          size={20}/>
                <input
                    data-testid="Search-Issuer-Input"
                    type="text"
                    value={searchText}
                    placeholder={t("Intro.searchText")}
                    onChange={event => filterIssuers(event.target.value)}
                    className="py-6 ps-10 pe-4 w-11/12 flex text-iw-searchTitle focus:outline-none"
                />
                {searchText.length > 0 && <IoCloseCircleSharp data-testid="Search-Issuer-Clear-Icon"
                                                              onClick={() => filterIssuers("")}
                                                              color={'var(--iw-color-closeIcon)'}
                                                              size={20}/>}
            </div>
        </div>
    </React.Fragment>
}

