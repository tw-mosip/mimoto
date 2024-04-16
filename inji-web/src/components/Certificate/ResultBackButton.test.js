import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import {ResultBackButton} from "./ResultBackButton";
import {useNavigate} from "react-router-dom";

jest.mock('react-router-dom', () => {
    // ensure only instance of navigate function is created to be able to test this in the next section
    const navigateMock = jest.fn();
    return ({
        ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
        useNavigate: () => navigateMock
    });
});

test("Test Result Back Button", () => {
    const issuerId = 'issuerid-1';
    const issuerDisplayName = 'display name';
    const clientId = 'clientid';

    const navigateMock = useNavigate();

    render(<ResultBackButton issuerId={issuerId} issuerDisplayName={issuerDisplayName} clientId={clientId} onClick={navigateMock}/>)
    const resultBackButton = screen.getByTestId('issuerid-1');
    expect(resultBackButton).toBeInTheDocument();

    resultBackButton.click();
    expect(navigateMock).toHaveBeenCalledWith(`/issuers/${issuerId}`, {
        state: {
            issuerDisplayName,
            clientId
        }
    })
});
