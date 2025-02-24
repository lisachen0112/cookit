import React from 'react'
import logo from '../assets/images/logo.png';
import { NavLink } from 'react-router-dom';
import { MdOutlineExplore } from "react-icons/md";
import { CgAddR } from "react-icons/cg";
import { LuHeart } from "react-icons/lu";
import { LuBell } from "react-icons/lu";

const Navbar = () => {
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
          <span className="text-3xl ml-2 text-medium-custom font-title font-medium">Cookit</span>
        </NavLink>
      </div>
      <div className="flex flex-col mt-1 space-y-4 px-4">
        <div className='hover:font-bold'>
          <NavLink
            to="/"
            className={linkClass}
          >
            <MdOutlineExplore className='inline ml-2 text-2xl mr-2'/>
            Explore
          </NavLink>
        </div>

        <div className='hover:font-bold'>
          
          <NavLink
            to="/create"
            className={linkClass}
          >
            <CgAddR className='inline ml-2 text-2xl mr-2'/>
            Create
          </NavLink>
        </div>

        <div className='hover:font-bold'>
          
          <NavLink
            to="/favorites"
            className={linkClass}
          >
            <LuHeart className='inline ml-2 text-2xl mr-2'/>
            Favorites
          </NavLink>
        </div>


        <div className='hover:font-bold'>
         
          <NavLink
            to="/notifications"
            className={linkClass}
          >
            <LuBell className='inline ml-2 text-2xl mr-2'/>
            Notifications
          </NavLink>
        </div>
      </div>
    </nav>
  )
}

export default Navbar