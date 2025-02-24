import React from 'react'
import { useState } from 'react';
import { FaPencil } from "react-icons/fa6";

const Recipe = ({ recipe }) => {
    const [showFullDescription, setShowFullDescription] = useState(false);
    const isLongDescription = recipe.description.length > 100;
    // const isLongDescription = true;
    let description = recipe.description;
    if (!showFullDescription && isLongDescription) {
        description = description.substring(0, 100) + '...';
    }

  return (
    <div className="bg-white rounded-xl shadow-lg relative border border-gray-100 flex flex-col">
        <div className="p-4 flex-grow">
            <div className="mb-6">
                <h3 className="text-xl font-bold text-text-dark-custom">
                    {recipe.title}
                </h3>
            </div>
            <div className="mb-5 text-text-custom">
                {description}
            </div>
            {isLongDescription && (
                <button 
                    onClick={() => setShowFullDescription((prevState) => !prevState)} 
                    className="text-light-custom mb-5 hover:text-dark-custom"
                >
                    { showFullDescription ?  'Less' : 'More'}
                </button>
            )}
        </div>
        <div className="border border-gray-100 mb-2"></div>
        <div className='pl-3 pt-3 pr-3'>
            <div className="flex flex-col lg:flex-row justify-between mb-4">
                <div className="mb-3 text-text-custom">
                    <FaPencil className="inline mr-2 text-text-custom"/>
                    {recipe.createdBy}
                </div>
                <a
                    href={`/recipes/${recipe.recipeId}`}
                    className="h-[36px] bg-light-custom hover:bg-medium-custom text-white px-4 py-2 rounded-lg text-center text-sm"
                >
                Read More
                </a>
        </div>
        </div>
    </div>
  );
};

export default Recipe