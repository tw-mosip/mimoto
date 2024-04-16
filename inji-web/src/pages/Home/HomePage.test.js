import React from "react";
import {render, screen} from "@testing-library/react";
import { act } from "react-dom/test-utils";
import {SampleIssuersData} from "../../components/Home/testData";
import * as axios from "axios";
import Home from "./HomePage";

// Mock out all top level functions, such as get, put, delete and post:
jest.mock("axios");

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

describe("Test home page", () => {
    test("Home page rendering", async () => {
        axios.get.mockImplementation(() => Promise.resolve({ data: SampleIssuersData }));
        render(<Home/>);
        expect(await screen.findByText("List of Issuers")).toBeVisible();
        expect(await screen.findByText('Download Sunbird Credentials')).toBeInTheDocument()
    })
})
