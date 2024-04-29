import React from 'react';
import { render, screen } from '@testing-library/react';
import {EmptyListContainer} from "../components/Common/EmptyListContainer";


test('renders learn react link', () => {
    render(<EmptyListContainer content={"Hello"} />);
    const linkElement = screen.getByText("Hello");
    expect(linkElement).toBeInTheDocument();
});
