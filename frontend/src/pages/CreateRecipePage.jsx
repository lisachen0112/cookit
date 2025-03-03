import { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/frontend';
import IngredientsForm from '../components/createRecipe/IngredientsForm';
import InstructionsForm from '../components/createRecipe/InstructionsForm';
import { toast } from 'react-hot-toast';
import { UserContext } from "../../context/userContext";


const CreateRecipePage = () => {
    const { user } = useContext(UserContext);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [ingredients, setIngredients] = useState(['']);
    const [instructions, setInstructions] = useState([{ type: "TITLE", content: "", media: null }]);
    const navigate = useNavigate();


    const postRecipe = async (newRecipe) => {
        try {
            const jwtToken = localStorage.getItem('token');
            const response = await fetch('/api/recipes', {
                method: 'POST',
                headers: {
                'Authorization': `Bearer ${jwtToken}`,
                },
                body: newRecipe,
            });
            if (!response.ok) {
                toast.error('Failed to post recipe');
                throw new Error('Failed to post recipe');
            }
            toast.success('Recipe created successfully');
            const location = response.headers.get('Location');
            if (location) {
                return location;
            }
        } catch (error) {
          console.error('Failed to post recipe', error);
          toast.error('Failed to post recipe');
        }
    };

    const submitForm = async (e) => {
        e.preventDefault();

        const formData = new FormData()
        formData.append('title', title);
        formData.append('description', description);
        ingredients.forEach((ingredient, index) => {
            formData.append(`ingredients[${index}]`, ingredient);
        });
        
        instructions.forEach((instruction, index) => {
            formData.append(`instructions[${index}].orderIndex`, index);
            formData.append(`instructions[${index}].type`, instruction.type);
            formData.append(`instructions[${index}].content`, instruction.content);
            if (instruction.media) {
                formData.append(`instructions[${index}].media`, instruction.media);
            }
        });

        const newRecipeLocation = await postRecipe(formData);
        console.log(newRecipeLocation);
        return navigate(newRecipeLocation ? newRecipeLocation : ROUTES.USER_RECIPES, {
            state: { isCreatedOrEdited: true}
        });
    };

    return (
            <div className="flex justify-center items-start h-full">
                <form onSubmit={submitForm} className="w-3/4">
                <h1 className="text-3xl text-center font-bold mb-6 text-medium-custom font-title">
                    Add Recipe
                </h1>
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

                <IngredientsForm ingredients={ingredients} setIngredients={setIngredients} />
                <InstructionsForm instructions={instructions} setInstructions={setInstructions} />
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
    )
}

export default CreateRecipePage