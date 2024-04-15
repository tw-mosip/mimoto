import * as React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import {Grid} from "@mui/material";

import {InjiNavbar, StyledGridItem, StyledLink, StyledToolbar} from "./styles";
import {useNavigate} from 'react-router-dom';
import logo from "../../assets/inji-logo.png";


function Navbar(props) {

    const navigate = useNavigate();
    return (
        <InjiNavbar data-testid='navbar'>
            <Container>
                <StyledToolbar disableGutters>
                    <Grid container style={{justifyItems: 'end'}}>
                        <StyledGridItem item xs={3} onClick={() => {navigate('/')}}>
                            <img src={logo} alt='logo' width='140px' height='70px'/>
                        </StyledGridItem>
                        <StyledGridItem item xs={9} style={{justifyContent: 'end'}}>
                            <Box>
                                <StyledLink href={"https://docs.mosip.io/inji"}>{"About Inji"}</StyledLink>
                            </Box>
                        </StyledGridItem>
                    </Grid>
                </StyledToolbar>
            </Container>
        </InjiNavbar>
    );
}
export default Navbar;
