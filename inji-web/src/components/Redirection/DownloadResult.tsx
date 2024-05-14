import React from "react";
import {useNavigate} from "react-router-dom";
import {BsShieldFillCheck, BsShieldFillX} from "react-icons/bs";
import {DownloadResultProps} from "../../types/components";
import {RequestStatus} from "../../hooks/useFetch";
import {useTranslation} from "react-i18next";
import {SpinningLoader} from "../Common/SpinningLoader";


export const DownloadResult: React.FC<DownloadResultProps> = (props) => {
    const {t} = useTranslation("RedirectionPage")
    const navigate = useNavigate();
    return <React.Fragment>
        <div data-testid="DownloadResult-Outer-Container" className="flex flex-col justify-center items-center pt-32">
            {props.state === RequestStatus.DONE &&
                <div className="rounded-full p-2 shadow">
                    <div className="rounded-full p-8 bg-iw-shieldSuccessShadow ">
                        <BsShieldFillCheck
                            data-testid="DownloadResult-Success-ShieldIcon" size={40} color={'var(--iw-color-shieldSuccessIcon)'}/>
                    </div>
                </div>
            }
            {props.state === RequestStatus.ERROR &&
                <div className="rounded-full p-2 shadow">
                    <div className="rounded-full p-8 bg-iw-shieldErrorShadow">
                        <BsShieldFillX
                            data-testid="DownloadResult-Error-ShieldIcon" size={40} color={'var(--iw-color-shieldErrorIcon)'}/>
                    </div>
                </div>
            }
            {props.state === RequestStatus.LOADING && <SpinningLoader />}
            <div className="my-4 ">
                <p className="font-bold" data-testid="DownloadResult-Title">{props.title}</p>
            </div>
            <div className="mb-6 px-10 text-center" data-testid="DownloadResult-SubTitle">
                <p>{props.subTitle}</p>
            </div>
            {(props.state === RequestStatus.DONE || props.state === RequestStatus.ERROR ) &&
                <div>
                <button
                    data-testid="DownloadResult-Home-Button"
                    onClick={() => navigate("/")}
                    className="text-iw-primary font-bold py-2 px-4 rounded-lg border-2 border-iw-primary">
                    {t("navigateButton")}
                </button>
            </div>
            }
        </div>
    </React.Fragment>
}

