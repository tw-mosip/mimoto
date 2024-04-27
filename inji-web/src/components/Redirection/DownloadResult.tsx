import React from "react";
import {useNavigate} from "react-router-dom";
import {BsShieldFillCheck, BsShieldFillX} from "react-icons/bs";
import {DownloadResultProps} from "../../types/components";


export const DownloadResult: React.FC<DownloadResultProps> = (props) => {
    const navigate = useNavigate();
    return <React.Fragment>
        <div data-testid="DownloadResult-Outer-Container" className="flex flex-col justify-center items-center pt-32">
            <div className="rounded-full p-2 shadow">
                {props.success ?
                    <div className="rounded-full p-8 bg-light-shieldSuccessShadow dark:bg-dark-shieldSuccessShadow ">
                        <BsShieldFillCheck
                            data-testid="DownloadResult-Success-SheildIcon" size={40} color={"green"}/>
                    </div> :
                    <div className="rounded-full p-8 bg-light-shieldErrorShadow dark:bg-dark-shieldErrorShadow">
                        <BsShieldFillX
                            data-testid="DownloadResult-Error-SheildIcon" size={40} color={"red"}/></div>}
            </div>
            <div className="mt-4 ">
                <p className="font-bold" data-testid="DownloadResult-Title">{props.title}</p>
            </div>
            <div className="mb-6" data-testid="DownloadResult-SubTitle">
                <p>{props.subTitle}</p>
            </div>
            <div>
                <button
                    data-testid="DownloadResult-Home-Button"
                    onClick={() => navigate("/")}
                    className="text-light-primary dark:text-dark-primary font-bold py-2 px-4 rounded-lg border-2 border-light-primary dark:border-dark-primary">
                    Go to Home
                </button>
            </div>
        </div>
    </React.Fragment>
}

