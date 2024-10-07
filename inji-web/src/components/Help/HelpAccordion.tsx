import React, {useState} from "react";
import {HelpAccordionItem} from "./HelpAccordionItem";
import { HelpAccordionItemProps } from "../../types/components";
import {useTranslation} from "react-i18next";
import { title } from "process";

export const HelpAccordion: React.FC = () => {

    const [open, setOpen] = useState(0);
    const {t} = useTranslation("Help");

    const createContent = (key: string, descriptions: string[]) => {
        return descriptions.map((desc, index) => {
            if (key === "item21" || key ==="item22") {
                return { __html: desc };
            }
            return desc;
        });
    };

    const accordionItems = [
        {
            title: t("item1.title"),
            content: createContent("item1",[t("item1.description1")]),
        },
        {
            title: t("item2.title"),
            content: createContent("item2",[t("item2.description1")]),
        },
        {
            title: t("item3.title"),
            content: createContent("item3",[t("item3.description1"), t("item3.description2"), t("item3.description3")]),
        },
        {
            title: t("item4.title"),
            content: createContent("item4",[t("item4.description1")]),
        },
        {
            title: t("item5.title"),
            content: createContent("item5",[t("item5.description1")]),
        },
        {
            title: t("item6.title"),
            content: createContent("item6",[t("item6.description1")]),
        },
        {
            title: t("item7.title"),
            content: createContent("item7",[t("item7.description1"), t("item7.description2"), t("item7.description3"), t("item7.description4")]),
        },
        {
            title: t("item8.title"),
            content:createContent("item8",[t("item8.description1"), t("item8.description2"), t("item8.description3"), t("item8.description4"), t("item8.description5"), t("item8.description6")]),
        },
        {
            title: t("item9.title"),
            content:createContent("item9",[t("item9.description1"), t("item9.description2"), t("item9.description3")]),
        },
        {
            title: t("item10.title"),
            content:createContent("item10",[t("item10.description1")]),
        },
        {
            title: t("item11.title"),
            content:createContent("item11",[t("item11.description1"), t("item11.description2"), t("item11.description3"), t("item11.description4"), t("item11.description5")]),
        },
        {
            title: t("item12.title"),
            content:createContent("item12",[t("item12.description1"), t("item12.description2"), t("item12.description3")]),
        },
        {
            title: t("item13.title"),
            content:createContent("item13",[t("item13.description1"), t("item13.description2"), t("item13.description3")]),
        },
        {
            title: t("item14.title"),
            content:createContent("item14",[t("item14.description1"), t("item14.description2"), t("item14.description3")]),
        },
        {
            title: t("item15.title"),
            content:createContent("item15",[t("item15.description1"), t("item15.description2"), t("item15.description3")]),
        },
        {
            title: t("item16.title"),
            content:createContent("item16",[t("item16.description1"), t("item16.description2"), t("item16.description3")]),
        },
        {
            title: t("item17.title"),
            content:createContent("item17",[t("item17.description1"), t("item17.description2"), t("item17.description3")]),
        },
        {
            title: t("item18.title"),
            content:createContent("item18",[t("item18.description1"), t("item18.description2"), t("item18.description3")]),
        },
        {
            title: t("item19.title"),
            content:createContent("item19",[t("item19.description1"), t("item19.description2"), t("item19.description3")]),
        },
        {
            title: t("item20.title"),
            content:createContent("item20",[t("item20.description1")]),
        },
        {
            title: t("item21.title"),
            content:createContent("item21",[t("item21.description1")]),
        },
        {
            title: t("item22.title"),
            content:createContent("item22",[t("item22.description1")]),
        },
        {
            title: t("item23.title"),
            content:createContent("item23",[t("item23.description1"),t("item23.description2"),t("item23.description3"),t("item23.description4")]),
        }
    ];

    return (
        <React.Fragment>
            <div data-testid="Help-Accordion-Container">
                {accordionItems.map((item, index) => (
                    <HelpAccordionItem
                        id={index}
                        key={index}
                        title={item.title}
                        content={item.content}
                        open={open}
                        setOpen={setOpen}
                    />
                ))}
            </div>
        </React.Fragment>
    );
};
