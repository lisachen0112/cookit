import React from 'react'

const Instructions = ({ instructions }) => {
  return (
    <div className="p-6 rounded-lg shadow-md mt-6">
      <h3 className="text-medium-custom text-lg font-bold mb-3">Instructions</h3>
      <ul>
        {instructions.map((instruction) => (
          <li key={instruction.id} className="mb-2">
            {instruction.type === 'TITLE' && <h4 className="font-bold">{instruction.content}</h4>}
            {instruction.type === 'TEXT' && <p>{instruction.content}</p>}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Instructions