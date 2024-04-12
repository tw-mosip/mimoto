import React from 'react';
import { render, screen } from '@testing-library/react';
import Logo from "../../assets/inji-logo.png";
import GridComponent from "./GridComponent";

jest.mock('../../assets/inji-logo.png', () => ({
    ...jest.requireActual('../../assets/inji-logo.png'),
    imageSrc: '../../__mocks__/inji-logo.png', // Mock the src attribute to return a string value
}));

test("Test Grid Component", () => {
    let cards = [{
        clickable: true,
        title: "Card 1",
        onClick: () => {
            console.log("Clicked on card");
        },
        actionIcon: null,
        imageUrl: Logo,
        id: "card1"
    }]
    render(
        <GridComponent cards={cards}/>
    );
    // Card functionality is tested in Card test
    // Check whether card is rendered
    const cardElement = screen.getByText(cards[0].title);
    expect(cardElement).toBeInTheDocument();
});
