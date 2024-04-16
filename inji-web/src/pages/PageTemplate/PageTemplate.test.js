import React from 'react';
import { render, screen } from '@testing-library/react';
import axios from 'axios';
import Footer from "../../components/PageTemplate/Footer.jsx";
import Navbar from "../../components/PageTemplate/Navbar.jsx";
import PageTemplate from "./PageTemplate.jsx";

jest.mock('axios');
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
    useLocation: jest.fn().mockReturnValue({
        pathname: '/mock-path', // Mock pathname or other properties as needed
        search: '', // Mock search string
        hash: '', // Mock hash string
        state: null, // Mock state object
        key: 'mock-key', // Mock key string
    }),
    useNavigate: jest.fn().mockImplementation(() => {
        return (path) => console.log(`Navigating to: ${path}`);
    }),
}));

jest.mock('../../assets/inji-logo.png', () => ({
    ...jest.requireActual('../../assets/inji-logo.png'),
    imageSrc: '../../assets/inji-logo.png.js', // Mock the src attribute to return a string value
}));

describe("Test Page Template", () => {

    test("Test Navbar", () => {
        render(<Navbar/>);
        const navbar = screen.getByTestId("navbar");
        expect(navbar).toBeInTheDocument();
    })

    test("Test Footer", () => {
        render(<Footer/>);
        const footer = screen.getByText("© 2024 Inji. All rights reserved.");
        expect(footer).toBeInTheDocument();
    });

    test("Page Template", () => {
        render(<PageTemplate/>);
        const navbar = screen.getByTestId("navbar");
        expect(navbar).toBeInTheDocument();

        const footer = screen.getByText("© 2024 Inji. All rights reserved.");
        expect(footer).toBeInTheDocument();

        const pageTemplate = screen.getByTestId("page-template");
        expect(pageTemplate).toBeInTheDocument();
    });
});
