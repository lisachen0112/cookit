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
import EditRecipePage from './pages/EditRecipePage';
import { UserContextProvider } from '../context/userContext';

const App = () => {



  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<MainLayout />}>
        <Route index element={<RecipeListPage />} />
        <Route path="/my-recipes" element={<RecipeListPage />}/>
        <Route path="/favorites" element={<RecipeListPage />}/>
        
        <Route path="/recipes/:recipeId" element={<RecipeDetailPage />} loader={recipeLoader} />
        <Route path="/create-recipe" element={<CreateRecipePage />} /> 
        <Route path="/edit-recipe/:recipeId" element={<EditRecipePage />} />
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