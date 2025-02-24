import React from 'react'
import logo from '../assets/images/logo.png';
import { MdOutlineExplore } from "react-icons/md";
import { CgAddR } from "react-icons/cg";
import { LuHeart } from "react-icons/lu";
import { LuBell } from "react-icons/lu";

const Navbar = () => {
  return (
    <nav className="text-text-custom fixed top-0 left-0 h-full w-53 flex 
    flex-col border-r border-gray-300">
      <div className="flex items-center justify-start h-20 pl-4">
        <a className="flex items-center" href="/index.html">
          <img className="h-15 w-auto" src={logo} alt="Cookit logo" />
          <span className="text-2xl font-bold ml-2 text-medium-custom">Cookit</span>
        </a>
      </div>
      <div className="flex flex-col mt-1 space-y-4 px-4">
        <div>
          <MdOutlineExplore className='inline ml-2 text-2xl'/>
          <a
            href="/index.html"
            className="rounded-md px-3 py-2"
          >
            Explore
          </a>
        </div>

        <div>
          <CgAddR className='inline ml-2 text-2xl'/>
          <a
            href="/jobs.html"
            className="rounded-md px-3 py-2"
          >
            Create
          </a>
        </div>

        <div>
          <LuHeart className='inline ml-2 text-2xl'/>
          <a
            href="/add-job.html"
            className="rounded-md px-3 py-2"
          >
            Favorites
          </a>
        </div>


        <div>
          <LuBell className='inline ml-2 text-2xl'/>
          <a
            href="/add-job.html"
            className="rounded-md px-3 py-2"
          >
            Notifications
          </a>
        </div>
      </div>
    </nav>
  )
}

export default Navbar