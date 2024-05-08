import React,{act} from 'react';
import { render, screen }  from '@testing-library/react';
import {EmptyListContainer} from "../../../components/Common/EmptyListContainer";


describe("Test Empty List Container",() => {
    test('check the presence of the container', () => {
        render(<EmptyListContainer content={"No Issuers Found"} />);
        const emptyElement = screen.getByTestId("EmptyList-Outer-Container");
        expect(emptyElement).toBeInTheDocument();
    });
    test('check if content is rendered properly', () => {
        render(<EmptyListContainer content={"No Issuers Found"} />);
        const emptyElement = screen.getByTestId("EmptyList-Outer-Container");
        expect(emptyElement).toHaveTextContent("No Issuers Found")
    });
})

