import React from 'react'
import { useState, useEffect } from 'react';
import Spinner from '../components/Spinner';
import RecipeCard from '../components/RecipeCard';

const PersonalRecipesPage = () => {
    const [recipes, setRecipes] = useState({});
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRecipes = async () => {
            try {
                const response = await fetch(`/api/users/103/recipes`);
                const data = await response.json();
                setRecipes(data);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching recipes: ', error);
            } finally {
                setLoading(false);
            }
        }
        fetchRecipes();
    }
        , []);
  return (
    <section className="bg-white px-4 py-10">
      <div className="container-xl lg:container m-auto">
        <h2 className="text-3xl font-bold text-medium-custom mb-6 text-center font-title">
          Your fabulous recipes
        </h2>

        { loading ? (
          <Spinner loading={loading}/>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            { recipes.content && recipes.content.map((recipe) => (
              <RecipeCard key={recipe.recipeId} recipe={recipe}/>
            ))}
          </div>
        )}
      </div>
    </section>
  )
}

export default PersonalRecipesPage