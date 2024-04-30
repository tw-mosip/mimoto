export const mockUseTranslation = () => {
    return jest.mock('react-i18next', () => ({
        useTranslation: () => ({
            t: (key:string) => key,
        }),
    }));
}

export const mockUseNavigate = () => {
    return jest.mock('react-router-dom', () => ({
        ...jest.requireActual('react-router-dom'),
        useNavigate: jest.fn(),
    }));
}
