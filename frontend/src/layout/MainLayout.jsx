import React, {useState, useEffect} from 'react'
import Navbar from '../components/Navbar';
import { Outlet } from 'react-router-dom';
import AuthModal from '../components/AuthModal';
import { Toaster } from 'react-hot-toast';
import logo from '../assets/images/logo.png';
import { UserContext } from '../../context/userContext';
import { useContext } from 'react';

const MainLayout = () => {
  const { isAuthenticated, logout } = useContext(UserContext);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);

  const openAuthModal = () => {
    setIsAuthModalOpen(true);
  }

  return (
    <>
      <header className="flex justify-between items-center p-4">
        <div>
          <Navbar />
        </div>

        <div className='mt-2'>
        {isAuthenticated ? (
            <div className="flex items-center space-x-4">
              <img
                src={logo}
                alt="Profile"
                className="w-10 h-10 rounded-full cursor-pointer"
              />
              <button
                onClick={logout}
                className="bg-red-500 text-white py-1 px-3 rounded pointer-cursor"
              >
                Logout
              </button>
            </div>
          ) : (
            <button
              onClick={() => setIsAuthModalOpen(true)}
              className="bg-medium-custom text-white py-2 px-4 rounded cursor-pointer"
            >
              Log In / Sign Up
            </button>
          )}
        </div>
      </header>

      <div className='ml-58 flex-grow'>
        <Outlet context={{ openAuthModal }} /> 
      </div>

      {isAuthModalOpen && <AuthModal closeModal={() => setIsAuthModalOpen(false)} />}
      <Toaster position='top-right' toastOptions={{duration: 2000}}/>
    </>
    );
};

export default MainLayout