import { useLoaderData} from 'react-router-dom';
import { FaPencil, FaRegHeart, FaHeart } from "react-icons/fa6";
import Ingredients from '../components/Ingredients';
import Instructions from '../components/Instructions';
import { format, formatDistanceToNow, isToday, isYesterday } from 'date-fns';
import { useContext, useState } from "react";
import { UserContext } from "../../context/userContext";
import { LuChefHat } from "react-icons/lu";

const RecipeDetailPage = () => {
  const { user } = useContext(UserContext);
  const recipe = useLoaderData();
  const createdDate = new Date(recipe.createdDate);
  let formattedDate;
  if (isToday(createdDate)) {
    formattedDate = format(createdDate, 'h:mm a');
  } else if (isYesterday(createdDate)) {
    formattedDate = 'Yesterday';
  } else {
    const daysAgo = formatDistanceToNow(createdDate, { addSuffix: true });
    formattedDate = daysAgo.includes('day') ? daysAgo : format(createdDate, 'MMMM dd, yyyy');
  }
  const [isHeartClicked, setIsHeartClicked] = useState(false);
  const handleHeartClick = (e) => {
    e.preventDefault();
    setIsHeartClicked(!isHeartClicked);
  }

  return (
    <>
      <div className="flex justify-center min-h-screen container pb-10 text-text-custom">
        <div className="w-3/4">
            <div className="p-6 rounded-lg shadow-md">
              <div className="text-medium-custom flex justify-between items-center mb-4">
                <h1 className="text-3xl font-bold font-title">
                  { recipe.title }
                </h1>

                {user && user.username === recipe.createdBy ? (
                  <button className="text-gray-500 hover:text-black">
                    <FaPencil className="inline mr-2" />
                    Edit this recipe
                  </button>
                ) : (
                  <div className="cursor-pointer" onClick={handleHeartClick}>
                      {isHeartClicked ? (
                          <FaHeart className='inline mr-2 text-2xl text-[#c1121f]'/>
                      ) : (
                          <FaRegHeart className='inline mr-2 text-2xl'/>
                      )}
                  </div>
                )}

              </div>
              <div className='mb-2'>
                <LuChefHat className="inline mr-1 mb-1" size={24}/>{ recipe.createdBy }
              </div>
              <p>{ recipe.description }</p>
              <div className='flex justify-end'>
                <p>{ formattedDate }</p>
              </div>
            </div>

            <Ingredients ingredients={recipe.ingredients}/>

            <Instructions instructions={recipe.instructions}/>

            <div className='p-6 rounded-lg shadow-md mt-6'>
              <h2 className='text-2xl font-bold font-title mb-2 text-medium-custom'>Reviews</h2>
              <p>Did you try this recipe? Share how it turned out and what you think!</p>
            </div>

            <div className='p-6 rounded-lg shadow-md mt-6'>
              <h2 className='text-2xl font-bold font-title text-medium-custom'>Ask a question</h2>
            </div>
        </div>
      </div>
    </>
  )
}

const recipeLoader = async  ({ params }) => {
  const res = await fetch(`/api/recipes/${params.recipeId}`);
  const data = await res.json();
  return data;
} 

export { RecipeDetailPage as default, recipeLoader }; 