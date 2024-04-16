import React from 'react';
import {render, screen} from "@testing-library/react";
import IssuerList from "./IssuerList";
import {SampleIssuersData} from "./testData";

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
describe('Testing issuer list component', () => {
    test('Check rendering', () => {
        render(<IssuerList issuersList={SampleIssuersData.response.issuers}/>)
        expect(screen.getByText('Download Sunbird Credentials')).toBeInTheDocument()
    })
});
