import {useState} from "react";
import {api, MethodType} from "../utils/api";
import {ResponseTypeObject} from "../types/data";

export enum RequestStatus {
    LOADING,
    DONE,
    ERROR
}

export const useFetch = () => {
    const [state, setState] = useState<RequestStatus>(RequestStatus.LOADING);
    const [error, setError] = useState<string>("");

    const fetchRequest = async (uri: string, method: MethodType, header: any, body?: any) => {
        try {
            setState(RequestStatus.LOADING);
            let responseJson: (ResponseTypeObject) = {};
            const response = await fetch(`${api.mimotoHost}${uri}`, {
                method: MethodType[method],
                headers: header,
                body: body,
            });
            responseJson = response;
            if (response.ok && uri.indexOf("download") === -1) {
                responseJson = await response.json();
                setState(RequestStatus.DONE);
            }
            if (!response.ok) {
                setState(RequestStatus.ERROR);
            }
            return responseJson;
        } catch (e) {
            setState(RequestStatus.ERROR);
            setError("Error Happened");
        }
    };
    return {state, error, fetchRequest};
}



