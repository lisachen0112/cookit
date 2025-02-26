import React from 'react'
import RecipeCard from './RecipeCard';

const RecipesList = ({ recipes, title }) => {
  return (
    <section className="bg-white px-4 py-10">
      <div className="container-xl lg:container m-auto">
        <h2 className="text-3xl font-bold text-medium-custom mb-6 text-center font-title">
          { title }
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          { recipes.content && recipes.content.map((recipe) => (
            <RecipeCard key={recipe.recipeId} recipe={recipe}/>
          ))}
        </div>
      </div>
    </section>
  )
}

export default RecipesList