import React from "react";
import {useFetch} from "../../hooks/useFetch";
import {useNavigate, useParams} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {IoArrowBack} from "react-icons/io5";
import {FaSearch} from "react-icons/fa";
import {useDispatch} from "react-redux";
import {storeCredentials} from "../../redux/reducers/credentialsReducer";
import {api, MethodType} from "../../utils/api";
import {NavBarProps} from "../../types/components";

export const NavBar: React.FC<NavBarProps> = (props) => {


    const {fetchRequest} = useFetch();
    const params = useParams<CredentialParamProps>();
    const navigate = useNavigate();
    const {t} = useTranslation("CredentialsPage");
    const dispatch = useDispatch();

    const filterCredential = async (searchText: string) => {
        const response = await fetchRequest(api.searchCredentialType(params.issuerId, searchText), MethodType.GET, null);
        dispatch(storeCredentials(response?.response?.supportedCredentials))
    }

    return <React.Fragment>
        <div data-testid="NavBar-Outer-Container" className="bg-blue-50 text-black p-4 py-10">
            <nav data-testid="NavBar-Inner-Container" className="flex justify-between mx-auto container items-center">
                <div className="flex items-center">
                    <IoArrowBack data-testid="NavBar-Back-Arrow" size={24} onClick={() => navigate("/")}/>
                    <span data-testid="NavBar-Text"
                          className="text-2xl font-semibold pl-2">{props.title}</span>
                </div>

                {props.search &&
                    <div className="flex py-1 items-center" data-testid="NavBar-Search-Container">
                        <div className="relative w-96 mx-auto flex justify-center items-center">
                            <FaSearch
                                data-testid="NavBar-Search-Icon"
                                className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 ml-2"
                                size={20}/>
                            <input
                                data-testid="NavBar-Search-Input"
                                type="text"
                                placeholder={t("searchText")}
                                onChange={event => filterCredential(event.target.value)}
                                className="py-6 pl-16 pr-4 rounded-md w-full shadow-xl  focus:outline-none" // Adjusted padding to accommodate the icon
                            />
                        </div>
                    </div>
                }
            </nav>
        </div>
    </React.Fragment>
}
