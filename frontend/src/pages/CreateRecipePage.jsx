import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../constants/frontend';
import IngredientsForm from '../components/createRecipe/IngredientsForm';
import InstructionsForm from '../components/createRecipe/InstructionsForm';
import { toast } from 'react-hot-toast';

const CreateRecipePage = () => {
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [ingredients, setIngredients] = useState(['']);
    const [instructions, setInstructions] = useState([{ type: "TITLE", content: "" }]);
    const navigate = useNavigate();


    const postRecipe = async (newRecipe) => {
        console.log(newRecipe);
        try {
          const jwtToken = localStorage.getItem('token');
          const response = await fetch('/api/recipes', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${jwtToken}`,
            },
            body: JSON.stringify(newRecipe),
          });
        } catch (error) {
          console.error('Failed to post recipe', error);
          toast.error('Failed to post recipe');
        }
    };

    const uploadImage = async (file) => {
        const jwtToken = localStorage.getItem('token');
        const formData = new FormData();
        formData.append("file", file);
        
        const response = await fetch("/api/media/upload/instructions", {
            headers: {
                'Authorization': `Bearer ${jwtToken}`,
            },
            method: "POST",
            body: formData,
        });
    
        if (!response.ok) {
            throw new Error("Image upload failed");
        }
        const imageUrl = await response.text();
        return imageUrl;
    };

    const submitForm = async (e) => {
        e.preventDefault();
        const updatedInstructions = await Promise.all(
            instructions.map(async (instruction, index) => {
                if (instruction.type === "IMAGE" && instruction.content) {
                    try {
                        const imageUrl = await uploadImage(instruction.content);
                        return { ...instruction, content: imageUrl, orderIndex: index };
                    } catch (error) {
                        console.error("Failed to upload image", error);
                        toast.error("Failed to upload image");
                        return { ...instruction, orderIndex: index }; 
                    }
                }
                return { ...instruction, orderIndex: index }; 
            })
        );
        const newRecipe = {
            title,
            description,
            ingredients,
            instructions: updatedInstructions,
        }
        postRecipe(newRecipe);
        return navigate(ROUTES.USER_RECIPES);
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