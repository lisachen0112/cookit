import React from 'react'
import { useState, useEffect } from 'react';
import Recipe from './Recipe';

const RecipesList = () => {
    const [recipes, setRecipes] = useState({});
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchRecipes = async () => {
            try {
                const response = await fetch('http://localhost:8088/api/v1/recipes');
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

    console.log(recipes);

  return (
    <section className="bg-white px-4 py-10 ml-58">
      <div className="container-xl lg:container m-auto">
        <h2 className="text-3xl font-bold text-text-dark-custom mb-6 text-center">
          Browse Recipes
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            { recipes.content && recipes.content.map((recipe) => (
                <Recipe key={recipe.recipeId} recipe={recipe}/>
            ))}
        </div>
      </div>
    </section>
  )
}

export default RecipesList