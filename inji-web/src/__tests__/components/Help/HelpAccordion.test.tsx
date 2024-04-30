import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {HelpAccordion} from "../../../components/Help/HelpAccordion";

describe("Test Help Accordion Container",() => {
    test('check the presence of the container', () => {
        render(<HelpAccordion />);
        const helpElement = screen.getByTestId("Help-Accordion-Container");
        expect(helpElement).toBeInTheDocument();
    });

    test('check the presence of the container', () => {
        render(<HelpAccordion />);
        const helpItemElement = screen.getAllByTestId("Help-Item-Container");
        expect(helpItemElement.length).toBe(7)
    });
    test('check first item should be expanded', () => {
        render(<HelpAccordion />);
        const helpItemElement = screen.getAllByTestId("Help-Item-Title-Text");
        expect(helpItemElement.length).toBe(7)
    });
    test('check first item should be expanded', () => {
        render(<HelpAccordion />);
        const helpItemElement = screen.getAllByTestId("Help-Item-Content-Text");
        expect(helpItemElement.length).toBe(1)
    });
})

describe("Test Help Accordion Working",() => {
    test('The Description should open when we press on the title', () => {
        render(<HelpAccordion />);
        const helpItemElement = screen.getAllByTestId("Help-Item-Container")[1];
        const button = screen.getAllByTestId("Help-Item-Title-Button")[1];
        expect(helpItemElement.childElementCount).toBe(1)
        fireEvent.click(button);
        expect(helpItemElement.childElementCount).toBe(2)
    });

    test('only one description should be open at a time, rest should close', () => {
        render(<HelpAccordion />);
        const helpItemElement = screen.getAllByTestId("Help-Item-Container")[1];
        const button = screen.getAllByTestId("Help-Item-Title-Button")[1];
        expect(helpItemElement.childElementCount).toBe(1)
        fireEvent.click(button);
        expect(helpItemElement.childElementCount).toBe(2)
        const overallDescElemet = screen.getAllByTestId("Help-Item-Content-Text");
        expect(overallDescElemet.length).toBe(1)
    });
})
