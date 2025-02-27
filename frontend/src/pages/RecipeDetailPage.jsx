import { useLoaderData} from 'react-router-dom';
import { FaArrowLeft } from 'react-icons/fa';
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

  const handleBackClick = (e) => {
      e.preventDefault();
      navigate(location.state?.from || '/');
  }

  return (
    <>
      <section>
        <div className="container pt-6 pl-6">
          <button
            onClick={handleBackClick}
            className="text-medium-custom flex items-center">
            <FaArrowLeft className='mr-2'/>
          </button>
        </div>
      </section>

      <div className="container pt-6 pb-10 px-6">
        <div className="grid grid-cols-1 md:grid-cols-70/30 w-full gap-6">
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

          {/* <aside>
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-bold mb-6">Manage Recipe</h3>
              <Link
                href="/add-job.html"
                className="bg-indigo-500 hover:bg-indigo-600 text-white text-center font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block"
              >
                Edit Recipe
              </Link>
              <button
                className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-full w-full focus:outline-none focus:shadow-outline mt-4 block"
              >
              Delete Recipe
              </button>
            </div>
          </aside> */}
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