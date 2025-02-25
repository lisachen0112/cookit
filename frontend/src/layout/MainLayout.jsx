import React from 'react'
import Navbar from '../components/Navbar';
import { Outlet } from 'react-router-dom';

const MainLayout = () => {
  return (
    <>
        <Navbar />
        <div className='ml-58 flex-grow'>
          <Outlet />
        </div>
        
    </>
    );
};

export default MainLayout