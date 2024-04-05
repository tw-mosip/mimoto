import React from 'react';
import {Alert, Snackbar} from "@mui/material";

const AlertMessage = ({open, message, severity, handleClose}) => {
    return (
        <Snackbar
            open={open}
            autoHideDuration={10000}
            onClose={handleClose}
            message={message}
            anchorOrigin={{vertical: "top", horizontal: "right"}}
        >
            <Alert
                onClose={handleClose}
                severity={severity}
                variant="filled"
                sx={{ width: '100%' }}
                style={{
                    borderRadius: '10px',
                    padding: '16px 18px',
                    fontSize: '18px'
                }}
            >
                {message}
            </Alert>
        </Snackbar>
    );
}

export default AlertMessage;
