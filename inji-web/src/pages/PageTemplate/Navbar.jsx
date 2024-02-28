import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import {Grid, Link, Typography} from "@mui/material";

import {DownloadButton, InjiNavbar, StyledGridItem, StyledLink, StyledToolbar} from "./styles";
import { useNavigate } from 'react-router-dom';
import logo from "../../assets/inji-logo.png";
import DownloadAppStore from '../../assets/DownloadAppStore.svg';
import DownloadFromGooglePlay from '../../assets/DownloadFromPlay.svg';

const links = ["About MOSIP"];

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
                        <StyledGridItem item xs={5.5} style={{justifyContent: 'end'}}>
                            <Box>
                                {
                                    links.map((link) => (
                                        <StyledLink key={link}>{link}</StyledLink>
                                    ))
                                }
                                {/* <Tooltip title="Download INJI">
                                    <DownloadButton>
                                        <Typography variant='body1' style={{font: "normal normal 600 14px/16px Inter", textTransform: "none"}}>
                                            Download Inji
                                        </Typography>
                                    </DownloadButton>
                                </Tooltip> */}
                            </Box>
                        </StyledGridItem>
                        <StyledGridItem item xs={3.5} style={{justifyContent: 'end'}}>
                                <Tooltip title="Download INJI">
                                    <img src={DownloadAppStore} width='155px' height='50px' style={{margin:'10px'}}/>
                                </Tooltip>
                                <Tooltip title="Download INJI">
                                    <img src={DownloadFromGooglePlay} width='155px' height='50px' style={{margin:'10px'}}/>
                                </Tooltip>
                        </StyledGridItem>
                    </Grid>
                </StyledToolbar>
            </Container>
        </InjiNavbar>
    );
}
export default Navbar;
