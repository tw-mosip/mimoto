import React from "react";
import { Box, Grid } from "@mui/material";
import PageTemplate from "../PageTemplate";
import Image from '../../assets/Background.svg';
import IssuersList from "../Home/IssuerList";
import SearchIssuers from "../Home/SearchIssuers";
import { useEffect, useState } from "react";
import _axios from 'axios';
import { SampleIssuersData } from "./testData";
import {FETCH_ISSUERS_URL, MIMOTO_URL} from "../../utils/config";
import LoadingScreen from "../../utils/LoadingScreen";
import {removeUinAndESignetIssuers} from "../../utils/misc";

export default function Home(props) {

    const [issuersList, setIssuersList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState();

    // logic to fetch the list of IssuerPage on page load
    useEffect(() => {
        _axios.get(FETCH_ISSUERS_URL)
        .then(response => {
            if (response?.data?.response?.issuers) {
                setIssuersList(response?.data?.response?.issuers?.filter(issuer => removeUinAndESignetIssuers(issuer.display[0].name)));
            }
            setLoading(false);
        })
        .catch(error => {
            console.error('Error fetching issuers:', error);
            setLoading(false);
            setErrorMessage(error);
        });
    }, []);

    // TODO: show a loader while loading and error message in case of any errors
    return (
        <PageTemplate>
            <SearchIssuers/>
            {loading ? <LoadingScreen /> :
                <IssuersList issuersList={ issuersList}/>
            }
        </PageTemplate>
    )
}
