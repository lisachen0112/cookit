import React, { useState } from "react";
import { MdClose } from "react-icons/md";
import Login from "./Login";
import Register from "./Register";

const AuthModal = ({ closeModal }) => {
  const [isLogin, setIsLogin] = useState(true);

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-gray-500/40">
      <div className="bg-white p-6 rounded-lg shadow-lg w-96 relative">
        <button
            onClick={closeModal}
            className="text-gray-500 text-xl absolute top-2 right-3 cursor-pointer"
        >
            <MdClose />
        </button>
        <h2 className="text-2xl font-bold mb-4 text-center">
            {isLogin ? "Log In" : "Sign Up"}
            </h2>
        
        {isLogin ? <Login closeModal={closeModal} /> : <Register closeModal={closeModal}/>}

        <p className="text-center mt-3">
          {isLogin ? "Don't have an account?" : "Already have an account?"}{" "}
          <button
            onClick={() => setIsLogin(!isLogin)}
            className="text-medium-custom underline cursor-pointer"
          >
            {isLogin ? "Register" : "Log In"}
          </button>
        </p>
      </div>
    </div>
  );
};

export default AuthModal;
