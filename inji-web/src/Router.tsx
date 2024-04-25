import {BrowserRouter, Route, Routes} from "react-router-dom";
import React from "react";
import {HomePage} from "./pages/HomePage";
import {Header} from "./components/PageTemplate/Header";
import {Footer} from "./components/PageTemplate/Footer";
import {HelpPage} from "./pages/HelpPage";
import {CredentialsPage} from "./pages/CredentialsPage";
import {RedirectionPage} from "./pages/RedirectionPage";

export const AppRouter = () => {
    return (<BrowserRouter>
        <Routes>
            <Route path="/" element={wrapElement(<HomePage/>)}/>
            <Route path="/issuers/:issuerId" element={wrapElement(<CredentialsPage/>)}/>
            <Route path="/help" element={wrapElement(<HelpPage/>)}/>
            <Route path="/redirect" element={wrapElement(<RedirectionPage/>)}/>
        </Routes>
    </BrowserRouter>)
}

const wrapElement = (element: JSX.Element) => {
    return <React.Fragment>
        <div className={"root"}>
            <Header/>
            <div className={"root-body"}>
                {element}
            </div>
            <Footer/>
        </div>
    </React.Fragment>
}
