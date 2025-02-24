import React from 'react'
import {
  Route,
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider
} from 'react-router-dom';
import Homepage from './pages/Homepage';
import MainLayout from './layout/MainLayout';
import RecipePage from './pages/RecipePage';
import NotFoundPage from './pages/NotFoundPage';

const router = createBrowserRouter(
  createRoutesFromElements(
  <Route path="/" element={<MainLayout />}>
    <Route index element={<Homepage />} />
    {/* <Route path="/recipe" element={<RecipePage />} /> */}
    <Route path="*" element={<NotFoundPage />} /> 
  </Route>
  )
);

const App = () => {
  return <RouterProvider router={router}/>
}

export default App