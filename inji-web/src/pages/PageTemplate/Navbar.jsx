import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import {Grid, Link, Typography} from "@mui/material";

import {DownloadButton, InjiNavbar, StyledGridItem, StyledLink, StyledToolbar} from "./styles";
import { useNavigate } from 'react-router-dom';
import logo from "../../assets/inji-logo.png";

const links = ['Link 1', 'Link 2', 'Link 3'];

function Navbar(props) {

    const navigate = useNavigate();
    return (
        <InjiNavbar data-testid='navbar'>
            <Container>
                <StyledToolbar disableGutters>
                    <Grid container style={{justifyItems: 'end'}}>
                        <StyledGridItem item xs={3} onClick={() => {navigate('/')}}>
                            <img src={logo} alt='logo' width='116px' height='28px'/>
                        </StyledGridItem>
                        <StyledGridItem item xs={9} style={{justifyContent: 'end'}}>
                            <Box>
                                {
                                    links.map((link) => (
                                        <StyledLink key={link}>{link}</StyledLink>
                                    ))
                                }
                                <Tooltip title="Download INJI">
                                    <DownloadButton>
                                        <Typography variant='body1' style={{font: "normal normal 600 14px/16px Inter", textTransform: "none"}}>
                                            Download Inji
                                        </Typography>
                                    </DownloadButton>
                                </Tooltip>
                            </Box>
                        </StyledGridItem>
                    </Grid>
                </StyledToolbar>
            </Container>
        </InjiNavbar>
    );
}
export default Navbar;
