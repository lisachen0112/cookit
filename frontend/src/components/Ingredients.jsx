import React from 'react'

const Ingredients = ({ ingredients }) => {
  return (
    <div className="p-6 rounded-lg shadow-md mt-6">
      <h2 className="text-medium-custom text-2xl font-bold mb-3 font-title">Ingredients</h2>
      <ul className="list-disc list-inside">
        {ingredients.map((ingredient) => (
          <li key={ingredient.ingredientId} className="mb-2">
            {ingredient.content}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Ingredients