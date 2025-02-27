import React from 'react'
import {
  Route,
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider
} from 'react-router-dom';
import MainLayout from './layout/MainLayout';
import RecipeDetailPage, { recipeLoader } from './pages/RecipeDetailPage';
import NotFoundPage from './pages/NotFoundPage';
import CreateRecipePage from './pages/CreateRecipePage';
import RecipeListPage from './pages/RecipeListPage';
import { UserContextProvider } from '../context/userContext';

const App = () => {

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
    }
  }

  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<MainLayout />}>
        <Route index element={<RecipeListPage />} />
        <Route path="/my-recipes" element={<RecipeListPage />}/>
        <Route path="/favorites" element={<RecipeListPage />}/>
        
        <Route path="/recipes/:recipeId" element={<RecipeDetailPage />} loader={recipeLoader} />
        <Route path="/create-recipe" element={<CreateRecipePage postNewRecipeRequest={postRecipe} />} /> 
        <Route path="*" element={<NotFoundPage />} /> 
      </Route>
    )
  );


  return (
    <UserContextProvider>
      <RouterProvider router={router} />
    </UserContextProvider>
  );
}

export default App