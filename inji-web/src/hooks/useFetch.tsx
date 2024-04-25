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

    const fetchRequest = async (uri: string, method: MethodType, body?: any) => {
        try {
            let responseJson: (ResponseTypeObject) = {};
            const response = await fetch(`${api.mimotoHost}${uri}`, {
                method: MethodType[method],
                headers: {
                    "Content-Type": "application/json",
                },
                body: body && JSON.stringify(body),
            });

            if (response.ok) {
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



