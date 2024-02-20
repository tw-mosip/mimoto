import React, {useEffect} from 'react';
import Navbar from "./Navbar";
import Footer from "./Footer";
import { useLocation, useNavigate } from "react-router-dom";
import {getUrlParamsMap} from "../../utils/misc";
import {DATA_KEY_IN_LOCAL_STORAGE} from "../../utils/config";

function PageTemplate({children}) {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        if (location.pathname !== "/") return;
        let searchParamsMap = getUrlParamsMap(location.search);
        if (Object.keys(searchParamsMap).indexOf("code") !== -1) {
            let sessionState = searchParamsMap["state"];
            let vcRedirectionDetails = JSON.parse(localStorage.getItem(DATA_KEY_IN_LOCAL_STORAGE) || "{}");
            if (!sessionState || !vcRedirectionDetails || sessionState !== vcRedirectionDetails["state"]) {
                console.error('invalid state');
                navigate("/");
                return;
            }
            if (vcRedirectionDetails) {
                navigate(`issuers/${vcRedirectionDetails.issuerId}/certificate/${vcRedirectionDetails.certificateId}` + location.search);
            }
        }
    }, []);
    return (
        <div data-testid='page-template' style={{width: '100%', paddingBottom: "40px"}}>
            <Navbar/>
            {children}
            <Footer/>
        </div>
    );
}

export default PageTemplate;
