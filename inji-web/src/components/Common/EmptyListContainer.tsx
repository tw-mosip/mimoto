import React from "react";
import {EmptyListContainerProps} from "../../types/components";

export const EmptyListContainer: React.FC<EmptyListContainerProps> = ({content}) => {
    return <React.Fragment>
        <div data-testid="EmptyList-Outer-Container"
             className="flex justify-center items-center w-full mx-auto flex-col">
            <div data-testid="EmptyList-Inner-Container" className="container mx-auto mt-8 px-4 flex-1 flex flex-col">
                <p data-testid="EmptyList-Text" className="text-center">{content}</p>
            </div>
        </div>
    </React.Fragment>
}
