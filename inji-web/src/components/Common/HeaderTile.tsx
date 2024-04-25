import React from "react";
import {HeaderTileProps} from "../../types/components";

export const HeaderTile: React.FC<HeaderTileProps> = ({content}) => {
    return <React.Fragment>
        <div data-testid="HeaderTile-Text" className={"font-bold mt-8 text-xl flex mx-28"}>{content}</div>
    </React.Fragment>
}
