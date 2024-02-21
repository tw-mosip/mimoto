import { createBrowserRouter } from "react-router-dom";
import Issuer from "./pages/IssuerPage";
import Home from "./pages/Home";
import Certificate from "./pages/Certificate";

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
        element: <Certificate/>,
    },
]);
