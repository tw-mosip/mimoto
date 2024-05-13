import React from 'react';
import {render, screen} from '@testing-library/react';
import {DownloadResult} from "../../../components/Redirection/DownloadResult";
import {RequestStatus} from "../../../hooks/useFetch";

jest.mock('../../../components/Common/SpinningLoader', () => ({
    SpinningLoader: () => <div data-testid={"SpinningLoader-Container"}/>,
}))

const mockedUsedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => ({
        navigate: mockedUsedNavigate,
    }),
}))

describe("DownloadResult Container",() => {
    test('check the presence of the container', () => {
        render(<DownloadResult title={"Title"} subTitle={"SubTitle"} state={RequestStatus.DONE}/>);
        let redirectionElement = screen.getByTestId("DownloadResult-Outer-Container");
        expect(redirectionElement).toBeInTheDocument();
        redirectionElement = screen.getByTestId("DownloadResult-Title");
        expect(redirectionElement).toHaveTextContent("Title")
        redirectionElement = screen.getByTestId("DownloadResult-SubTitle");
        expect(redirectionElement).toHaveTextContent("SubTitle")
    });
})

describe("DownloadResult For Success (DONE) Scenario", () => {

    beforeEach(() => {
        render(<DownloadResult title={"Title"} subTitle={"SubTitle"} state={RequestStatus.DONE}/>);
    })

    test('check the presence of the title', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-Title");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the subTitle', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-SubTitle");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the success ShieldIcon', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-Success-ShieldIcon");
        expect(redirectionElement).toBeInTheDocument();
    });
    test('check the presence of the error ShieldIcon to be not in the document', () => {
        const redirectionElement = screen.queryByTestId("DownloadResult-Error-ShieldIcon");
        expect(redirectionElement).not.toBeInTheDocument();
    });
    test('check the presence of the Loader to be not in the document', () => {
        const redirectionElement = screen.queryByTestId("SpinningLoader-Container");
        expect(redirectionElement).not.toBeInTheDocument();
    });
})

describe("DownloadResult For Error (ERROR) Scenario", () => {

    beforeEach(() => {
        render(<DownloadResult title={"Title"} subTitle={"SubTitle"} state={RequestStatus.ERROR}/>);
    })

    test('check the presence of the title', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-Title");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the subTitle', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-SubTitle");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the success ShieldIcon to be in the document', () => {
        const redirectionElement = screen.queryByTestId("DownloadResult-Success-ShieldIcon");
        expect(redirectionElement).not.toBeInTheDocument();
    });
    test('check the presence of the error ShieldIcon to be in the document', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-Error-ShieldIcon");
        expect(redirectionElement).toBeInTheDocument();
    });
    test('check the presence of the Loader to be not in the document', () => {
        const redirectionElement = screen.queryByTestId("SpinningLoader-Container");
        expect(redirectionElement).not.toBeInTheDocument();
    });
})


describe("DownloadResult For Loading (LOADING) Scenario", () => {

    beforeEach(() => {
        render(<DownloadResult title={"Title"} subTitle={"SubTitle"} state={RequestStatus.LOADING}/>);
    })

    test('check the presence of the title', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-Title");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the subTitle', () => {
        const redirectionElement = screen.getByTestId("DownloadResult-SubTitle");
        expect(redirectionElement).toBeInTheDocument();
    });

    test('check the presence of the success ShieldIcon not to be in the document', () => {
        const redirectionElement = screen.queryByTestId("DownloadResult-Success-ShieldIcon");
        expect(redirectionElement).not.toBeInTheDocument();
    });
    test('check the presence of the error ShieldIcon to be in the document', () => {
        const redirectionElement = screen.queryByTestId("DownloadResult-Error-ShieldIcon");
        expect(redirectionElement).not.toBeInTheDocument();
    });
    test('check the presence of the Loader to be in the document', () => {
        const redirectionElement = screen.queryByTestId("SpinningLoader-Container");
        expect(redirectionElement).toBeInTheDocument();
    });
})
