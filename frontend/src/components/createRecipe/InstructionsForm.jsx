import { FaPlus, FaRegImage } from "react-icons/fa6";
import { FaMinus, FaFont } from "react-icons/fa";

const InstructionsForm = ({ instructions, setInstructions}) => {
    const title = "TITLE";
    const text = "TEXT";
    const image = "IMAGE";

    const handleAddInstruction = (type) => {
        setInstructions([...instructions, { type, content: "" }]);
    };

    const handleInstructionChange = (index, value) => {
        const updatedInstructions = [...instructions];
        updatedInstructions[index]["content"] = value;
        setInstructions(updatedInstructions);
    };

    const handleRemoveInstruction = (index) => {
        const instruction = instructions[index];
        if (instruction.type === image && instruction.content) {
            URL.revokeObjectURL(instruction.content);
        }
        
        setInstructions(instructions.filter((_, i) => i !== index));
    };

    const handleFileChange = (index, file) => {
        const updatedInstructions = [...instructions];
        updatedInstructions[index].content = file;
        setInstructions(updatedInstructions);
    }


  return (
    <div className="mb-4 ">
      <label htmlFor="instructions" className="block text-gray-700 font-bold mb-2">
        Instructions
      </label>
      
      {instructions.map((instruction, index) => (
        <div key={index} className="flex flex-col mb-2">
            <div className="flex items-center">
                {instruction.type === title && (
                    <input
                    type="text"
                    className="border rounded w-full py-2 px-3 mb-2 font-bold"
                    placeholder="Step Title"
                    value={instruction.title}
                    onChange={(e) => handleInstructionChange(index, e.target.value)}
                    />
                )}
                {instruction.type === text && (
                    <textarea
                    className="border rounded w-full py-2 px-3 mb-2"
                    placeholder="Step description..."
                    value={instruction.content}
                    onChange={(e) => handleInstructionChange(index, e.target.value)}
                    />
                )}
                {instruction.type === image && (
                    <div className="flex flex-col justify-center items-center w-full">
                        {instruction.content && (
                            <img
                                src={URL.createObjectURL(instruction.content)}
                                alt="Preview"
                                className="w-100 h-100 object-cover rounded"
                            />
                            )
                        }
                        <input
                            type="file"
                            accept="image/*"
                            className="border rounded py-2 px-3 my-2"
                            onChange={(e) => handleFileChange(index, e.target.files[0])}
                        />
                    </div>
                )}
                <button
                    type="button"
                    className="text-red-600 font-bold py-1 px-2 focus:outline-none focus:shadow-outline cursor-pointer"
                    onClick={() => handleRemoveInstruction(index)}
                >
                    <FaMinus />
                </button>
            </div>

        </div>
      ))}

      <div className="flex gap-4 mt-4">
        <button
          type="button"
          className="font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline 
          flex items-center cursor-pointer"
          onClick={() => handleAddInstruction(title)}
        >
          <FaFont className="mr-2" /> Add Title
        </button>
        <button
          type="button"
          className="font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline 
          flex items-center cursor-pointer"
          onClick={() => handleAddInstruction(text)}
        >
          <FaPlus className="mr-2" /> Add Text
        </button>
        <button
          type="button"
          className="font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline
          flex items-center cursor-pointer"
          onClick={() => handleAddInstruction(image)}
        >
          <FaRegImage className="mr-2" /> Add Image
        </button>
      </div>
    </div>
  )
}

export default InstructionsForm