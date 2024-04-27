import {BrowserRouter, Route, Routes} from "react-router-dom";
import React from "react";
import {HomePage} from "./pages/HomePage";
import {Header} from "./components/PageTemplate/Header";
import {Footer} from "./components/PageTemplate/Footer";
import {HelpPage} from "./pages/HelpPage";
import {CredentialsPage} from "./pages/CredentialsPage";
import {RedirectionPage} from "./pages/RedirectionPage";
import {useSelector} from "react-redux";
import {RootState} from "./types/redux";

export const AppRouter = () => {

    const theme = useSelector((state: RootState) => state.common.theme);
    const wrapElement = (element: JSX.Element) => {
        return <React.Fragment>
            <div className={`root ${theme}`}>
                <Header/>
                <div className={"root-body"}>
                    {element}
                </div>
                <Footer/>
            </div>
        </React.Fragment>
    }

    return (<BrowserRouter>
        <Routes>
            <Route path="/" element={wrapElement(<HomePage/>)}/>
            <Route path="/issuers/:issuerId" element={wrapElement(<CredentialsPage/>)}/>
            <Route path="/help" element={wrapElement(<HelpPage/>)}/>
            <Route path="/redirect" element={wrapElement(<RedirectionPage/>)}/>
        </Routes>
    </BrowserRouter>)
}


