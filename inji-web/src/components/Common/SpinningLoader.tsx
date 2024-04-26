import React from "react";
import {TailSpin} from "react-loader-spinner";

export const SpinningLoader: React.FC = () => {
    return <React.Fragment>
        <div className={"flex justify-center items-center"}>
            <TailSpin
                visible={true}
                height="80"
                width="80"
                color="#EB6F2D"
                ariaLabel="tail-spin-loading"
                radius="2"
                wrapperStyle={{}}
                wrapperClass=""
            />
        </div>
    </React.Fragment>

}
