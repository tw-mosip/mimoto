import React from "react";
import {Oval} from "react-loader-spinner";

export const SpinningLoader: React.FC = () => {
    return <React.Fragment>
        <div data-testid="SpinningLoader-Container" className={"flex justify-center items-center"}>
            <Oval
                visible={true}
                height="80"
                width="80"
                color="#EB6F2D"
                secondaryColor="#E6E6E6"
                ariaLabel="oval-loading"
                wrapperStyle={{}}
                wrapperClass=""
                strokeWidth={3}
            />
        </div>
    </React.Fragment>

}
