import React from 'react'
import logo from '../assets/images/logo.png';
import { NavLink } from 'react-router-dom';
import { MdOutlineExplore } from "react-icons/md";
import { CgAddR } from "react-icons/cg";
import { LuHeart } from "react-icons/lu";
import { LuBell } from "react-icons/lu";
import { LuChefHat } from "react-icons/lu";
import{ ROUTES }from '../constants/frontend';
import { UserContext } from '../../context/UserContext';
import { useContext } from 'react';

const Navbar = () => {
  const { isAuthenticated } = useContext(UserContext);
  const linkClass = ({ isActive }) => 
    isActive 
      ? 'rounded-md font-bold text-medium-custom' 
      : 'rounded-md';

  return (
    <nav className="text-text-custom fixed top-0 left-0 h-full w-53 flex 
    flex-col border-r border-gray-200">
      <div className="flex items-center justify-start h-20 pl-4">
        <NavLink className="flex items-center" to="/">
          <img className="h-15 w-auto" src={logo} alt="Cookit logo" />
          <span className="text-3xl ml-2 text-medium-custom font-title font-medium">
              Cookit
          </span>
        </NavLink>
      </div>
      <div className="flex flex-col mt-1 space-y-4 px-4">
        <NavLink to={ROUTES.HOME} className={linkClass}>
          <div className='hover:font-bold flex items-center'>
            <MdOutlineExplore className='inline ml-2 text-2xl mr-2'/>
            Explore
          </div>
        </NavLink>
        { isAuthenticated && 
        <>
          <NavLink to={ROUTES.CREATE_RECIPE} className={linkClass} >
            <div className='hover:font-bold flex items-center'>
            <CgAddR className='inline ml-2 text-2xl mr-2'/>
            Create
            </div>
          </NavLink>

          <NavLink to={ROUTES.USER_RECIPES} className={linkClass}>
            <div className='hover:font-bold flex items-center'>
              <LuChefHat className='inline ml-2 text-2xl mr-2'/>
              Your recipes
            </div>
          </NavLink>

          <NavLink to={ROUTES.FAVORITES} className={linkClass}>
            <div className='hover:font-bold flex items-center'>
              <LuHeart className='inline ml-2 text-2xl mr-2'/>
              Favorites
            </div>
          </NavLink>

          <NavLink to={ROUTES.NOTIFICATIONS} className={linkClass}>
            <div className='hover:font-bold flex items-center'>
              <LuBell className='inline ml-2 text-2xl mr-2'/>
              Notifications
            </div>
          </NavLink>
        </>
        }
      </div>
    </nav>
  )
}

export default Navbar