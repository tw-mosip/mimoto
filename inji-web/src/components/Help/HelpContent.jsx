import {useState} from "react";
import {HelpContentItem} from "./HelpContentItem";

const data = [

    {
        panelId: "panel1",
        panelHeading: "Who are issuers?",
        panelContents: ["Issuers are either governmental bodies or entities trusted by the government, responsible for providing verifiable credentials in PDF format to residents upon request."]
    },
    {
        panelId: "panel2",
        panelHeading: "What are verifiable credentials?",
        panelContents: ["Verifiable credentials are digital documents that help users to share information like identity or qualifications to the service provider which can be instantly verified. They're issued by trusted entities and can be cryptographically verified for authenticity. These credentials streamline identity verification processes."]
    },
    {
        panelId: "panel3",
        panelHeading: "How can I download a credential in Inji Web?",
        panelContents: [
            "Step 1: Search & choose an Issuer from the landing page and select a credential type.",
            "Step 2: In the authentication page, provide required details",
            "Step 3: PDF format of the verifiable credential will be downloaded into the system. "]
    },
    {
        panelId: "panel4",
        panelHeading: "What details I need to provide to download my credential?",
        panelContents: ["The credential issuer would have provided details like UIN/VID in case of MOSIP National ID or Policy number, Name and DoB for an Insurance card. This information has to be fed to the authentication system to enable download."]
    },
    {
        panelId: "panel5",
        panelHeading: "Where can I find my credential?",
        panelContents: ["The verifiable credential will be downloaded into the Downloads folder of your system."]
    },
    {
        panelId: "panel6",
        panelHeading: "What details are present in the PDF credential?",
        panelContents: ["Details collected as part of the registration process will be presented in the PDF. At present, for Insurance use case, one can find Policy details like Name, DoB, "]
    },
    {
        panelId: "panel7",
        panelHeading: "Why I am not finding the list of issuers?",
        panelContents: ["We’re sorry you stumbled upon an error. However, please check if the issuers' end point is available. If the end point health check is good, please check if the issuers list is configured in the mimoto-issuers config.json."]
    }
]

export const HelpContent  = () => {

    const [expanded, setExpanded] = useState('panel1');
    const handleChange = (panel) => (event, isExpanded) => {
            setExpanded(isExpanded ? panel : false);
        };

    return data.map(entry =>
    <div style={{display:"flex",borderRadius: "10px",minWidth: "200%"}}>
        <HelpContentItem panelId={entry.panelId}
                                              expanded={expanded}
                                              handleChange={handleChange}
                                              panelHeading={entry.panelHeading}
                                              panelContents={entry.panelContents} />
    </div>
    )
}
