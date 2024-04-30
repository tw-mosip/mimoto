import React from 'react';
import { render, screen } from '@testing-library/react';
import {SpinningLoader} from "../../../components/Common/SpinningLoader";



describe("Test Spinning Loader Container",() => {
    test('check the presence of the container', () => {
        render(<SpinningLoader />);
        const spinningElement = screen.getByTestId("SpinningLoader-Container");
        expect(spinningElement).toBeInTheDocument();
    });
})

