import React from 'react'
import {
  Route,
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider
} from 'react-router-dom';
import Homepage from './pages/Homepage';
import MainLayout from './layout/MainLayout';
import RecipePage, { recipeLoader } from './pages/RecipePage';
import NotFoundPage from './pages/NotFoundPage';
import CreateRecipePage from './pages/CreateRecipePage';
import PersonalRecipesPage from './pages/PersonalRecipesPage';
import FavoritedRecipesPage from './pages/FavoritedRecipesPage';

const App = () => {

  const postRecipe = async (newRecipe) => {
    console.log(newRecipe);
    try {
      const jwtToken = 'eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InRlc3QyQG1haWwuY29tIiwic3ViIjoidGVzdDIiLCJpYXQiOjE3NDA1NzI3MTQsImV4cCI6MTc0MDU4MTM1NCwiYXV0aG9yaXRpZXMiOltdfQ.0-gtV_6YfV4GgJ5kPmiH09ce6HZ2d8dRNBTo8H55u40';
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
      <Route index element={<Homepage />} />
      <Route path="/recipes/:recipeId" element={<RecipePage />} loader={recipeLoader} />
      <Route path="/create-recipe" element={<CreateRecipePage postNewRecipeRequest={postRecipe} />} /> 
      <Route path="/your-recipes" element={<PersonalRecipesPage />}/>
      <Route path="/favorites" element={<FavoritedRecipesPage />}/>
      <Route path="*" element={<NotFoundPage />} /> 
    </Route>
    )
  );

  return <RouterProvider router={router}/>
}

export default App