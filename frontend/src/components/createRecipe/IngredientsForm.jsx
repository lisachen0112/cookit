import React from 'react'
import { FaPlus } from "react-icons/fa6";
import { FaMinus } from "react-icons/fa";

const IngredientsForm = ({ ingredients, setIngredients }) => {

    const handleAddIngredient = () => {
        setIngredients([...ingredients, '']);
    };

    const handleRemoveIngredient = (index) => {
        const newIngredients = [...ingredients];
        newIngredients.splice(index, 1);
        setIngredients(newIngredients);
    };

    const handleIngredientChange = (index, event) => {
        const newIngredients = [...ingredients];
        newIngredients[index] = event.target.value;
        setIngredients(newIngredients);
    };

    return (
        <div className='mb-4'>
            <label 
                htmlFor='ingredients'
                className='block text-gray-700 font-bold mb-2'>
                Ingredients
            </label>
            {ingredients.map((ingredient, index) => (
                    <div key={index} className="flex items-center mb-2">
                        <input
                            type='text'
                            className='border rounded w-full py-2 px-3'
                            placeholder='250g flour'
                            value={ingredient}
                            onChange={(e) => handleIngredientChange(index, e)}
                        />
                        <button
                            type="button"
                            className="ml-2 text-red-600 font-bold py-2 px-4 
                            focus:outline-none focus:shadow-outline cursor-pointer"
                            onClick={() => handleRemoveIngredient(index)}
                        >
                            <FaMinus />
                        </button>
                    </div>
                ))}
            <button
                type="button"
                className="font-bold py-2 px-4 rounded-full focus:outline-none 
                focus:shadow-outline"
                onClick={handleAddIngredient}
            >
                <FaPlus className='inline'/>
                <span className="ml-2">Add Ingredient</span>
            </button>
        </div>
    )
}

export default IngredientsForm