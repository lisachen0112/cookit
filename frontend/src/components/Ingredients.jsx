import React from 'react'

const Ingredients = ({ ingredients }) => {
  return (
    <div className="p-6 rounded-lg shadow-md mt-6">
      <h3 className="text-medium-custom text-lg font-bold mb-3">Ingredients</h3>
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