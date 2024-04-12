import React from 'react';
import { render, screen } from '@testing-library/react';
import Header from "./Header";

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
    useParams: jest.fn().mockReturnValue({
        issuerId: 'issuerid-1', // Mock pathname or other properties as needed
        certificateId: 'certificateid-1', // Mock search string
    }),
    useNavigate: jest.fn().mockImplementation(() => {
        return (path) => console.log(`Navigating to: ${path}`);
    })
}));

test("Certificate header section", () => {
    render(
        <Header/>
    );
    expect(screen.getByText('issuerid-1')).toBeInTheDocument();
});
