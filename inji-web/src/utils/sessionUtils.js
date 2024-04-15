export const DATA_KEY_IN_LOCAL_STORAGE = "VC_DOWNLOAD";
export const addNewSession = (session) => {
    let activeSessions = getAllActiveSession();
    activeSessions = activeSessions.concat([session]);
    localStorage.setItem(DATA_KEY_IN_LOCAL_STORAGE, JSON.stringify(activeSessions));
}

export const getAllActiveSession = () => {
    const activeSessions = localStorage.getItem(DATA_KEY_IN_LOCAL_STORAGE);
    if(activeSessions){
        return JSON.parse(activeSessions);
    }
    return [];
}

export const getActiveSession = (state) => {
    const activeSession = getAllActiveSession().filter(session => session.state === state);
    return activeSession.length>0 ? activeSession[0] : {};
}

export const removeActiveSession = (state) => {
    const remainingSessions = getAllActiveSession().filter(session => session.state !== state);
    localStorage.setItem(DATA_KEY_IN_LOCAL_STORAGE, JSON.stringify(remainingSessions));
}
