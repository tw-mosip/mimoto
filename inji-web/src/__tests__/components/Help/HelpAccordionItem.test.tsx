import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {HelpAccordionItem} from "../../../components/Help/HelpAccordionItem";

describe("Test Help Accordion Container",() => {
    test('check the presence of the container', () => {
        const openHandler = jest.fn();
        render(<HelpAccordionItem id={1} title={"HelpTitle"} content={["HelpContent"]} open={1} setOpen={openHandler} />);
        const helpElement = screen.getByTestId("Help-Item-Container");
        expect(helpElement).toBeInTheDocument();
    });

    test('if current help item is not open, it should not show description', () => {
        const openHandler = jest.fn();
        render(<HelpAccordionItem id={1} title={"HelpTitle"} content={["HelpContent"]} open={0} setOpen={openHandler} />);
        const helpElement = screen.getByTestId("Help-Item-Container");
        expect(helpElement.childElementCount).toBe(1);
    });
    test('if current help item is open, it should show description', () => {
        const openHandler = jest.fn();
        render(<HelpAccordionItem id={1} title={"HelpTitle"} content={["HelpContent"]} open={1} setOpen={openHandler} />);
        const helpElement = screen.getByTestId("Help-Item-Container");
        expect(helpElement.childElementCount).toBe(2);
    });

    test('if current help item is open, it should show description', () => {
        const openHandler = jest.fn();
        render(<HelpAccordionItem id={1} title={"HelpTitle"} content={["HelpContent"]} open={0} setOpen={openHandler} />);
        const helpElement = screen.getByTestId("Help-Item-Container");
        expect(helpElement.childElementCount).toBe(1);
        const buttonElement = screen.getByTestId("Help-Item-Title-Button");
        fireEvent.click(buttonElement);
        expect(openHandler).toHaveBeenCalled();
    });
})
