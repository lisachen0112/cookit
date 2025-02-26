import { useState } from 'react';
import { FaPlus } from "react-icons/fa6";
import { FaMinus } from "react-icons/fa";
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/frontend';

const CreateRecipePage = ({ postNewRecipeRequest }) => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [ingredients, setIngredients] = useState(['']);
    const [instructions, setInstructions] = useState(['']);
    const navigate = useNavigate();

    const handleIngredientChange = (index, event) => {
        const newIngredients = [...ingredients];
        newIngredients[index] = event.target.value;
        setIngredients(newIngredients);
    };

    const handleAddIngredient = () => {
        setIngredients([...ingredients, '']);
    };

    const handleRemoveIngredient = (index) => {
        const newIngredients = [...ingredients];
        newIngredients.splice(index, 1);
        setIngredients(newIngredients);
    };

    const submitForm = (e) => {
        e.preventDefault();
        const newRecipe = {
            title,
            description,
            ingredients
        }
        postNewRecipeRequest(newRecipe);
        return navigate(ROUTES.USER_RECIPES);
    };

    return (
        <section>
            <div className="bg-white px-6 py-8 mb-4 m-4 md:m-0">
                <form onSubmit={submitForm}>
                <h2 className="text-3xl text-center font-semibold mb-6">Add Recipe</h2>
                <div className="mb-4">
                    <label 
                        htmlFor='title'
                        className="block text-gray-700 font-bold mb-2">
                        Title
                    </label>
                    <input
                        type="text"
                        id="title"
                        name="title"
                        className="border rounded w-full py-2 px-3 mb-2"
                        placeholder="eg. Original Nonna Ragù sauce"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>

                <div className="mb-4">
                    <label
                        htmlFor="description"
                        className="block text-gray-700 font-bold mb-2">
                        Description
                    </label>
                    <textarea
                        id="description"
                        name="description"
                        className="border rounded w-full py-2 px-3"
                        rows="4"
                        placeholder="eg. A traditional Italian meat sauce that's been passed down through generations."
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    ></textarea>
                </div>

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

                <div>
                    <button
                    className="bg-medium-custom text-white font-bold py-2 px-4 rounded-full w-full 
                    focus:outline-none focus:shadow-outline cursor-pointer"
                    type="submit"
                    >
                    Create Recipe
                    </button>
                </div>
                </form>
            </div>
        </section>
    )
}

export default CreateRecipePage