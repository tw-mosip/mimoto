import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import {Credential} from "../../../components/Credentials/Crendential";
import {CredentialWellknownObject, DisplayArrayObject, LogoObject} from "../../../types/data";
import {Provider} from "react-redux";

const getCredentialObject = (): CredentialWellknownObject => {
    return {
        format: "ldp_vc",
        id: "id",
        scope: "mosip_ldp_vc",
        display: {
            name: "Name",
            language: "en",
            locale: "en",
            logo: {
                url: "https://url.com",
                alt_text: "alt text of the url"
            },
            title: "Title",
            description: "Description",
        }
    }
}

describe.skip("Test Credentials Item Layout",() => {
    test('check the presence of the container', () => {
        const clickHandler = jest.fn();
        const credential:CredentialWellknownObject = getCredentialObject();
        // render(
        //     <Provider store={{credentials: credential}}>
        //         <Credential credential={credential} index={1} />
        //     </Provider>
        // );
        const itemBoxElement = screen.getByTestId("ItemBox-Outer-Container");
        expect(itemBoxElement).toBeInTheDocument();
    });
    test('check if content is rendered properly', () => {
        const clickHandler = jest.fn();
        const credential:CredentialWellknownObject = getCredentialObject();
        // render(<Credential credential={credential} index={1} />);
        const itemBoxElement = screen.getByTestId("ItemBox-Outer-Container");
        expect(itemBoxElement).toHaveTextContent("TitleOfItemBox")
    });
})

