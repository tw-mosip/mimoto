import React from 'react';
import {render, screen} from '@testing-library/react';
import {Footer} from "../../../components/PageTemplate/Footer";
describe("Footer Container",() => {
    test('check the presence of the container', () => {
        render(<Footer />);
        const footerElement = screen.getByTestId("Footer-Container");
        expect(footerElement).toBeInTheDocument();
    });

    test('check the presence of the container', () => {
        render(<Footer />);
        const footerElementText = screen.getByTestId("Footer-Text");
        expect(footerElementText).toBeInTheDocument();
    });
    test('check the presence of the container', () => {
        render(<Footer />);
        const footerElementText = screen.getByTestId("Footer-Text");
        expect(footerElementText).toHaveTextContent("Footer.copyRight");
    });
})

