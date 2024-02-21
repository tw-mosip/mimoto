import * as React from 'react';
import AspectRatio from '@mui/joy/AspectRatio';
import {Card} from '@mui/joy';
import CardContent from '@mui/joy/CardContent';
import {IconButton} from '@mui/material';


export default function InteractiveCard({ id, title, imageURL, actionIcon, onClick, clickable }) {
    return (
        <Card
            data-testid={id}
            onClick={() => clickable && onClick()}
            variant="outlined"
            orientation="horizontal"
            sx={{
                '&:hover': {boxShadow: 'md', borderColor: 'neutral.outlinedHoverBorder'},
                cursor: clickable ? 'pointer' : 'auto',
                background: '#FFFFFF 0% 0% no-repeat padding-box',
                boxShadow: '0px 3px 6px #18479329',
                borderRadius: '6px',
                opacity: 1,
                margin: 'auto',
                border: "none"
            }}
        >
            <AspectRatio ratio="1" sx={{minWidth: 50, height: 50, marginRight: "10px"}}>
                <img
                    sx={{minWidth: 50, height: 50}}
                    src={imageURL}
                    loading="lazy"
                    alt={title}
                />
            </AspectRatio>
            <CardContent style={{
                minWidth: '170px',
                alignItems: "start",
                "justifyContent": "center",
                font: "normal normal 600 15px/16px Inter"
            }}>
                {title}
            </CardContent>
            {actionIcon && <CardContent style={{width: '20px', alignItems: "end", "justifyContent": "center"}}>
                <IconButton onClick={onClick}>
                    {actionIcon}
                </IconButton>
            </CardContent>}
        </Card>
    );
}