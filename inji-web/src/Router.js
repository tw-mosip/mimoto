import { createBrowserRouter } from "react-router-dom";
import Issuer from "./pages/IssuerPage/IssuersPage";
import Home from "./pages/Home/HomePage";
import Certificate from "./pages/Certificate/CertificatePage";
import {HelpPage} from "./pages/Help/HelpPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <Home />,
    },
    {
        path: "/issuers",
        element: <Home />,
    },
    {
        path: "/issuers/:issuerId",
        element: <Issuer/>,
    },
    {
        path: "/issuers/:issuerId/certificate/:certificateId",
        element: <Certificate />,
    },
    {
        path: "/help",
        element: <HelpPage />,
    },
]);
