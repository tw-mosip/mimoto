import React from 'react';
import axios from 'axios';
import {render, screen} from "@testing-library/react";
import Certificate from "./index";

jest.mock('../../assets/inji-logo.png', () => ({
    ...jest.requireActual('../../assets/inji-logo.png'),
    imageSrc: '../../assets/inji-logo.png.js', // Mock the src attribute to return a string value
}));
jest.mock('axios');
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
    useLocation: jest.fn().mockReturnValue({
        pathname: '/issuers/Issuer 1/certificate/1', // Mock pathname or other properties as needed
        search: '', // Mock search string
        hash: '', // Mock hash string
        state: null, // Mock state object
        key: 'mock-key', // Mock key string
    }),
    useNavigate: jest.fn().mockImplementation(() => {
        return (path) => console.log(`Navigating to: ${path}`);
    }),
    useParams: jest.fn().mockImplementation(() => {
        return {issuerId: "Issuer 1", certificateId: "1"}
    })
}));

describe("Test Certificate Download Page", () => {
    test("User verification failed", async () => {
        render(<Certificate/>)
        expect(await screen.findByText('Invalid user credentials')).toBeVisible();
    });

    //TODO: mock of window.location.search is not working
    /*test("User verification failed", async () => {
        const search = '?code=sample-code';
        await window.history.pushState({}, '', search);
        axios.post.mockImplementation(() => Promise.resolve({ status: 200, data: {
                access_token: 'sample_access_token'
            } }));
        axios.get.mockImplementation(() => Promise.resolve({status: 500, data: {
                errors: [
                    {
                        errorMessage: 'Failed to download the credentials'
                    }
                ]
            } }));
        render(<Certificate/>)
        expect(await screen.findByText('Failed to download the credentials')).toBeVisible();
    });*/
});
