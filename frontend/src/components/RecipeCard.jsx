import React from 'react'
import { useState } from 'react';
import { FaPencil, FaRegHeart, FaHeart } from "react-icons/fa6";
import { Link, useLocation } from 'react-router-dom';

const RecipeCard = ({ recipe }) => {
    const location = useLocation();
    const [isHeartClicked, setIsHeartClicked] = useState(false);
    const isLongDescription = recipe.description.length > 100;
    // const isLongDescription = true;
    let description = recipe.description;
    if (isLongDescription) {
        description = description.substring(0, 100) + '...';
    }

    const handleHeartClick = (e) => {
        e.preventDefault();
        setIsHeartClicked(!isHeartClicked);
    }

  return (
    <Link 
        to={`/recipes/${recipe.recipeId}`} 
        state= {{ from: location.pathname}}
        className="bg-white rounded-xl shadow-lg relative border border-gray-100 flex flex-col 
    hover:bg-light-custom cursor-pointer text-text-custom">
        <div className="p-4 flex-grow">
            <div className="mb-6">
                <h3 className="text-xl font-bold font-title text-text-dark-custom">
                    {recipe.title}
                </h3>
            </div>
            <div className="mb-5">
                {description}
            </div>
        </div>
        <div className="border border-gray-100 mb-2"></div>
        <div className='pl-3 pt-3 pr-3'>
            <div className="flex flex-col lg:flex-row justify-between mb-4">
                <div className="mb-3">
                    <FaPencil className="inline mr-2"/>
                    {recipe.createdBy}
                </div>
                <div className="cursor-pointer" onClick={handleHeartClick}>
                    {isHeartClicked ? (
                        <FaHeart className='inline mr-2 text-2xl text-[#c1121f]'/>
                    ) : (
                        <FaRegHeart className='inline mr-2 text-2xl'/>
                    )}
                </div>
            </div>
        </div>
    </Link>
  );
};

export default RecipeCard