import React from "react";
import {ToastContainer} from "react-toastify";

export const AppToaster: React.FC = () => {
    return <ToastContainer
        position="top-right"
        autoClose={1000}
        hideProgressBar
        newestOnTop
        closeOnClick
        rtl={false}
        icon={<React.Fragment/>}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        style={{width: '400px'}}
        theme="colored"
    />
}
