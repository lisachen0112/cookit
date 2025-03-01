import { useEffect, useState } from 'react'

const Instructions = ({ instructions }) => {
  return (
    <div className="p-6 rounded-lg shadow-md mt-6">
      <h2 className="text-medium-custom text-2xl font-bold mb-3 font-title">Instructions</h2>
      <ul>
        {instructions
        .sort((a, b) => a.orderIndex - b.orderIndex)
        .map((instruction) => (
          <li key={instruction.id} className="mb-2">
            {instruction.type === 'TITLE' && <h4 className="font-bold text-lg">{instruction.content}</h4>}
            {instruction.type === 'TEXT' && <p>{instruction.content}</p>}
            {instruction.type === 'IMAGE' && instruction.content && (
              <div className="flex justify-center">
                <img
                  src={"http://localhost:8088/api/v1/uploads/" + instruction.content}
                  alt="Instruction image"
                  className="max-w-full h-auto rounded"
                  style={{ maxHeight: '300px', objectFit: 'contain' }}
                />
              </div>

            )}
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Instructions