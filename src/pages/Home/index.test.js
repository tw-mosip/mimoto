import {render} from "@testing-library/react";
import IssuerList from "./IssuerList.js";

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

describe("Test home page", () => {
    test("Issuer List", () => {
        render(<IssuerList issuersList={[]}/>)
    })
})
