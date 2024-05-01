import {BrowserRouter, Route, Routes} from "react-router-dom";
import React from "react";
import {HomePage} from "./pages/HomePage";
import {Header} from "./components/PageTemplate/Header";
import {Footer} from "./components/PageTemplate/Footer";
import {HelpPage} from "./pages/HelpPage";
import {CredentialsPage} from "./pages/CredentialsPage";
import {RedirectionPage} from "./pages/RedirectionPage";

export const AppRouter = () => {
    const app_theme = ""; //can be "purple_theme" or "" ( for default )
    const wrapElement = (element: JSX.Element) => {
        return <React.Fragment>
            <div className={`min-h-screen bg bg-iw-background font-base ${app_theme}`}>
                <Header/>
                <div className={"top-20 h-5/6 mt-20 flex-grow"}>
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


