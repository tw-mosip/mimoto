import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {Header} from "../../../components/PageTemplate/Header";
import {wrapUnderRouter} from "../../../utils/mockUtils";
import {useNavigate} from "react-router-dom";

jest.mock('../../../components/Common/LanguageSelector', () => ({
    LanguageSelector: () => <></>,
}))

describe("Header Container",() => {
    jest.mock('react-router-dom', () => ({
        ...jest.requireActual('react-router-dom'),
        useNavigate: jest.fn().mockImplementation(() => {
            return (path:string) => console.log(`Navigating to: ${path}`);
        }),
    }));

    test('check the presence of the container', () => {
        render(wrapUnderRouter(<Header />));
        const footerElement = screen.getByTestId("Header-Container");
        expect(footerElement).toBeInTheDocument();
    });

    test('check the presence of the injweb logo', () => {
        render(wrapUnderRouter(<Header />));
        const injiWebLogo = screen.getByTestId("Header-InjiWeb-Logo-Container");
        console.log("injiWebLogo:", injiWebLogo);
        expect(injiWebLogo).toBeInTheDocument();
    });
    test('check the presence of the help', () => {
        render(wrapUnderRouter(<Header />));
        const footerElementText = screen.getByTestId("Header-Menu-Help");
        expect(footerElementText).toBeInTheDocument()
        expect(footerElementText).toHaveTextContent("Header.help");
    });
    test('check the presence of the about inji ', () => {
        render(wrapUnderRouter(<Header />));
        const footerElementText = screen.getByTestId("Header-Menu-AboutInji");
        expect(footerElementText).toBeInTheDocument()
        expect(footerElementText).toHaveTextContent("Header.aboutInji");
    });
    test('check the presence of the language Selector', () => {
        render(wrapUnderRouter(<Header />));
        const footerElementText = screen.getByTestId("Header-Menu-LanguageSelector");
        expect(footerElementText).toBeInTheDocument()
    });
})

