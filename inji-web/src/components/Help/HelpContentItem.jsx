import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import Typography from "@mui/material/Typography";
import AccordionDetails from "@mui/material/AccordionDetails";
import {HorizontalLine} from "../atoms/HorizontalLine";

export const HelpContentItem = ({panelId, expanded, handleChange, panelHeading, panelContents}) => {
    return <Accordion expanded={expanded === panelId} onChange={handleChange(panelId)} style={{width: "200%", marginBottom: "20px"}}>
        <AccordionSummary
            expandIcon={<ArrowDropDownIcon />}
            aria-controls={`${panelId}-content`}
            id={`${panelId}-header`}>
            <Typography style={{fontWeight: 'bold', padding: '5px' }}>
                {panelHeading}
                {expanded === panelId && <HorizontalLine />}
            </Typography>
        </AccordionSummary>
        <AccordionDetails>
            {panelContents.map(panelContent => <Typography style={{ padding: '5px' }}>
                {panelContent}
            </Typography>)}

        </AccordionDetails>
    </Accordion>
}
