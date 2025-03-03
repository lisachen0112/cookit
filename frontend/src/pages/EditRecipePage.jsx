import { useLocation, useParams, useNavigate } from "react-router-dom"
import { useEffect, useState } from "react"
import IngredientsForm from '../components/createRecipe/IngredientsForm';
import InstructionsForm from '../components/createRecipe/InstructionsForm';
import { toast } from 'react-hot-toast';
import { ROUTES } from '../constants/frontend';

const EditRecipePage = () => {
    const { recipeId } =  useParams();
    const location = useLocation();
    const [recipe, setRecipe] = useState(location.state?.recipe || null );

    const [title, setTitle] = useState(recipe?.title || '');
    const [description, setDescription] = useState(recipe?.description || '');
    const [ingredients, setIngredients] = useState(recipe?.ingredients ? recipe.ingredients.map(ing => ing.content) : ['']);
    const [instructions, setInstructions] = useState(recipe?.instructions || 
        [{ type: "TITLE", content: "", media: null }]);

    const navigate = useNavigate();

    useEffect(() => {
        if (!recipe) {
            console.log('fetching recipe');
            const fetchRecipe = async () => {
                try {
                const response = await fetch(`/api/recipes/${recipeId}`);
                const data = await response.json();
                setRecipe(data);
                } catch (error) {
                console.error("Error fetching recipe:", error);
                }
            };
            fetchRecipe();
        }
    }, [recipeId, recipe]);

    const submitForm = async (e) => {
        e.preventDefault();
        console.log(instructions);

        const updatedRecipe = new FormData();
        updatedRecipe.append('title', title);
        updatedRecipe.append('description', description);
        ingredients.forEach((ingredient, index) => {
            updatedRecipe.append(`ingredients[${index}]`, ingredient);
        });
        instructions.forEach((instruction, index) => {
            updatedRecipe.append(`instructions[${index}].orderIndex`, index);
            updatedRecipe.append(`instructions[${index}].type`, instruction.type);
            updatedRecipe.append(`instructions[${index}].content`, instruction.content);
            if (instruction.media) {
                updatedRecipe.append(`instructions[${index}].media`, instruction.media);
            }
        });
        await patchRecipe(updatedRecipe);
    }

    const patchRecipe = async (updatedRecipe) => {
        updatedRecipe.forEach((value, key) => {
            console.log(key, value);
        });
        try {
            const jwtToken = localStorage.getItem('token');
            const response = await fetch(`/api/recipes/${recipeId}`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`,
                },
                body: updatedRecipe,
            });
            if (!response.ok) {
                toast.error('Failed to update recipe');
                throw new Error('Failed to update recipe');
            }
            toast.success('Recipe updated successfully');
            return navigate(ROUTES.RECIPE_DETAILS.replace(':recipeId', recipeId), {
                state: { isCreatedOrEdited: true}
            });
        } catch (error) {
            console.error('Failed to update recipe', error);
            toast.error('Failed to update recipe');
        }
    }

    const deleteRecipe = async () => {
        console.log(`deleting recipe with id: ${recipeId}`);
        try {
            const jwtToken = localStorage.getItem('token');
            const response = await fetch(`/api/recipes/${recipeId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`,
                },
            });
            if (!response.ok) {
                throw new Error('Failed to delete recipe');
            }
            toast.success('Recipe deleted successfully');
            return navigate(ROUTES.USER_RECIPES);
        } catch (error) {
            console.error('Failed to delete recipe', error);
            toast.error('Failed to delete recipe');
        }
    }

    return (
        <div className="flex justify-center items-start h-full">
            <form onSubmit={submitForm} className="w-3/4">
            <h1 className="text-3xl text-center font-bold mb-6 text-medium-custom font-title">
                Edit a recipe
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
                Save changes
                </button>
            </div>
            <div className="mt-4">
                <button
                className="bg-red-custom text-white font-bold py-2 px-4 rounded-full w-full 
                focus:outline-none focus:shadow-outline cursor-pointer"
                type="button"
                onClick={deleteRecipe}
                >
                Delete recipe
                </button>
            </div>
            </form>
        </div>
    )
}

export default EditRecipePage