import { Box, Grid } from "@mui/material";
import PageTemplate from "../PageTemplate";
import Image from '../../assets/Background.svg';
import IssuersList from "../Home/IssuerList";
import SearchIssuers from "../Home/SearchIssuers";
import { useEffect, useState } from "react";
import _axios from 'axios';
import { IssuersData } from "./testData";
import {FETCH_ISSUERS_URL, MIMOTO_URL} from "../../utils/config";

export default function Home(props) {

    const [issuersList, setIssuersList] = useState([]);
    const [filteredList, setFilteredList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState();


    // logic to fetch the list of IssuerPage on page load

    useEffect(() => {
        _axios.get(FETCH_ISSUERS_URL)
        .then(response => {
            if (response?.data?.response?.issuers) {
                setIssuersList(response?.data?.response?.issuers);
                setFilteredList(response?.data?.response?.issuers);
            }
            setLoading(false);
        })
        .catch(error => {
            console.error('Error fetching issuers:', error);
            setLoading(false);
            setErrorMessage(error);
        });
    }, []); 

    useEffect(() => {
        console.log(filteredList);
    }, [filteredList]);

    // TODO: show a loader while loading and error message in case of any errors
    return (
        <PageTemplate>
            <SearchIssuers options={issuersList} setFilteredIssuerList={setFilteredList}/>
            {/* <IssuersList issuersList={filteredList.length === 0 ? issuersList: filteredList}/> */}
            <IssuersList issuersList={ issuersList}/>
        </PageTemplate>
    )
}
