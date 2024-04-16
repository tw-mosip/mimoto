import Header from "../../components/Certificate/Header";
import Box from "@mui/material/Box";
import {Grid} from "@mui/material";
import PageTemplate from "../PageTemplate/PageTemplate";
import React from "react";
import {HelpContent} from "../../components/Help/HelpContent";

export const HelpPage = () => {
    return (
        <PageTemplate>
            <Header title={"Help"}/>
            <Box style={{minHeight:'100vh', width:'600px', margin: '40px auto'}}>
                <Grid container style={{ display: 'grid', justifyContent: 'center', justifyItems: 'center'}}>
                    <HelpContent />
                </Grid>
            </Box>
        </PageTemplate>
    );

}
