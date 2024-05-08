import {storage} from "./storage";
import {SessionObject} from "../types/data";

export const addNewSession = (session: SessionObject) => {
    let activeSessions = getAllActiveSession();
    activeSessions = activeSessions.concat([session]);
    storage.setItem(storage.SESSION_INFO, JSON.stringify(activeSessions));
}

export const getAllActiveSession: () => SessionObject[] = () => {
    const activeSessions = storage.getItem(storage.SESSION_INFO);
    if (activeSessions) {
        return JSON.parse(activeSessions);
    }
    return [];
}

export const getActiveSession = (state: string | null) => {
    const activeSession = getAllActiveSession().filter(session => session.state === state);
    return activeSession.length > 0 ? activeSession[0] : {};
}

export const removeActiveSession = (state: string) => {
    const remainingSessions = getAllActiveSession().filter(session => session.state !== state);
    storage.setItem(storage.SESSION_INFO, JSON.stringify(remainingSessions));
}

