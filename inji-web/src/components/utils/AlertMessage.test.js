import React from 'react';
import { render, screen } from '@testing-library/react';
import AlertMessage from "./AlertMessage";

test("Test Alert Message", () => {
    render(
        <AlertMessage message={'Test Alert Message'} severity={'success'} open={true} handleClose={() => {}}/>
    );
    expect(screen.getByText('Test Alert Message')).toBeInTheDocument();
});
