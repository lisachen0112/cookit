import React from 'react'
import { Link } from 'react-router-dom'
import { FaTriangleExclamation } from "react-icons/fa6";

const NotFoundPage = () => {
  return (
    <section className="text-center flex flex-col justify-center items-center h-96">
      <FaTriangleExclamation className="text-yellow-400 text-6xl mb-4" />
      <h1 className="text-6xl font-bold mb-4 text-text-dark-custom">404 Not Found</h1>
      <p className="text-xl mb-5">This page does not exist</p>
      <Link
        to="/"
        className="text-white bg-[#3E5879] hover:bg-medium-custom rounded-md px-3 py-2 mt-4"
      >
        Go Back
      </Link>
    </section>
  )
}

export default NotFoundPage