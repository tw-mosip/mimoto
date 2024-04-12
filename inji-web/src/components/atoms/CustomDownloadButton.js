import React from 'react';
import FileDownloadOutlinedIcon from '@mui/icons-material/FileDownloadOutlined';

export default function CustonDownloadButton () {
    return (
        <FileDownloadOutlinedIcon 
        style={{
            color: 'black',
            width: 20,
            height: 20,
            opacity: 1
        }}
        ></FileDownloadOutlinedIcon>
    );
}