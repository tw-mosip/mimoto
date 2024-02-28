import React from 'react';
import './LoadingScreen.css'; 
import { CircularProgress } from '@mui/material';

const LoadingScreen = () => {
    return (

      <div className="loading-screen">
        <CircularProgress style={{color: '#E86E04'}}/>
      </div>
    );
};

export default LoadingScreen;