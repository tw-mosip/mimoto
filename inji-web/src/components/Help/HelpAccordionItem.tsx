import React from "react";
import {HelpAccordionItemProps} from "../../types/components";
import {IoIosArrowDown, IoIosArrowUp} from "react-icons/io";


export const HelpAccordionItem: React.FC<HelpAccordionItemProps> = (props) => {

    return <React.Fragment>
        <div className="border rounded-md mb-2 shadow-sm shadow-blue-100" data-testid="Help-Item-Container">
            <button
                data-testid="Help-Item-Title-Button"
                className="w-full p-4 text-left font-bold hover:bg-gray-50 focus:outline-none"
                onClick={() => props.setOpen(props.id)}
            >
                <div className={"flex flex-row"} data-testid="Help-Item-Title-Text">
                    {props.title}
                    <div className={"flex items-center ml-auto"}>
                        {
                            (props.id === props.open) ? <IoIosArrowUp size={20} data-testid="Help-Item-UpArrow"/> :
                                <IoIosArrowDown size={20} data-testid="Help-Item-DownArrow"/>
                        }
                    </div>
                </div>
            </button>
            {(props.id === props.open) && (
                <div className="p-4 bg-white border-t-2" data-testid="Help-Item-Content-Text">
                    {props.content.map(content => <p>{content}</p>)}

                </div>
            )}
        </div>
    </React.Fragment>
}

