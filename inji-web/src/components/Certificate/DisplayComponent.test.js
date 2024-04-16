import React from 'react';
import { render, screen } from '@testing-library/react';
import {DisplayComponent} from "./DisplayComponent";

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
    useParams: jest.fn().mockReturnValue({
        issuerId: 'issuerid-1'
    }),
    useLocation: jest.fn().mockReturnValue({
        state: {
            issuerDisplayName: 'issuer-display-name'
        }
    }),
    useNavigate: jest.fn().mockImplementation(() => {
        return (path) => console.log(`Navigating to: ${path}`);
    })
}));

describe('Test Download Status Display Component', () => {
    test('Verifying credentials', () => {
        render(<DisplayComponent clientId={'clientid-1'} message={'Verifying credentials'} inProgress={true}/>);
        expect(screen.getByText('Verifying credentials')).toBeInTheDocument();
    });
    test('Downloading credentials', () => {
        render(<DisplayComponent clientId={'clientid-1'} message={'Downloading credentials'} inProgress={true}/>);
        expect(screen.getByText('Downloading credentials')).toBeInTheDocument();
    });
    test('Download complete', () => {
        render(<DisplayComponent clientId={'clientid-1'} message={'Download complete'} inProgress={true}/>);
        expect(screen.getByText('Download complete')).toBeInTheDocument();
    });
    test('Downloading credentials', () => {
        render(<DisplayComponent clientId={'clientid-1'} message={'Downloading credentials'} inProgress={true}/>);
        expect(screen.getByText('Downloading credentials')).toBeInTheDocument();
    });
    test('Invalid user credentials', () => {
        render(<DisplayComponent clientId={'clientid-1'} message={'Invalid user credentials'} inProgress={true}/>);
        expect(screen.getByText('Invalid user credentials')).toBeInTheDocument();
    });
});
