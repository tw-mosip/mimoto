import {CredentialWellknownObject, IssuerObject, ResponseTypeObject} from "./data";
import React from "react";

export type ItemBoxProps = {
    index: number;
    url: string;
    title: string;
    description?: string;
    onClick: () => void;
}
export type NavBarProps = {
    title: string;
    search: boolean;
    fetchRequest: () => ResponseTypeObject;
}
export type CredentialProps = {
    credential: CredentialWellknownObject;
    index: number;
}
export type HelpAccordionItemProps = {
    id: number;
    title: string;
    content: string[];
    open: number;
    setOpen: React.Dispatch<React.SetStateAction<number>>;
}
export type IssuerProps = {
    index: number;
    issuer: IssuerObject;
}
export type DownloadResultProps = {
    success: boolean;
    title: string;
    subTitle: string;
}

export type EmptyListContainerProps = {
    content: string;
}

export type HeaderTileProps = {
    content: string;
}
export type SearchIssuerProps = {
    fetchRequest: () => ResponseTypeObject
}
