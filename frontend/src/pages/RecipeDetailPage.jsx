import { useLoaderData} from 'react-router-dom';
import { FaPencil, FaRegHeart, FaHeart } from "react-icons/fa6";
import { Link, useLocation, useNavigate } from 'react-router-dom';
import Ingredients from '../components/Ingredients';
import Instructions from '../components/Instructions';
import { format, formatDistanceToNow, isToday, isYesterday } from 'date-fns';

const RecipeDetailPage = () => {
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

  const location = useLocation();
  const navigate = useNavigate();

  return (
    <>
      <div className="flex justify-center min-h-screen container pb-10">
        <div className="w-3/4">
          <main>
            <div className="p-6 rounded-lg shadow-md">
              <div className="text-medium-custom flex justify-between items-center mb-4">
                <h1 className="text-3xl font-bold">
                  { recipe.title }
                </h1>
                <div>
                  <FaPencil className="inline mr-2" />
                  { recipe.createdBy }
                </div>
              </div>
              <p>{ recipe.description }</p>
              <div className='flex justify-end'>
                <p>{ formattedDate }</p>
              </div>
            
            </div>
            <Ingredients ingredients={recipe.ingredients}/>
            <Instructions instructions={recipe.instructions}/>
            <div className='p-6 rounded-lg shadow-md mt-6'>
              <h2 className='text-2xl font-bold'>Reviews</h2>
              <p>Did you try this recipe? Share how it turned out and what you think!</p>
            </div>

            <div className='p-6 rounded-lg shadow-md mt-6'>
              <h2 className='text-2xl font-bold'>Ask a question</h2>
            </div>
          </main>
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