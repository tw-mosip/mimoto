import axios from 'axios';

jest.mock('axios');
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'), // Use the actual implementation of react-router-dom
    useLocation: jest.fn().mockReturnValue({
        pathname: '/issuers/Issuer 1/certificate/1', // Mock pathname or other properties as needed
        search: '', // Mock search string
        hash: '', // Mock hash string
        state: null, // Mock state object
        key: 'mock-key', // Mock key string
    }),
    useNavigate: jest.fn().mockImplementation(() => {
        return (path) => console.log(`Navigating to: ${path}`);
    }),
    useParams: jest.fn().mockImplementation(() => {
        return {issuerId: "Issuer 1", certificateId: "1"}
    })
}));

describe("Test Certificate Download Page", () => {
    test("Success Component", () => {

    });
});
