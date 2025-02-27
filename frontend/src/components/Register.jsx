import { useState } from 'react';
import { toast } from 'react-hot-toast';


const Register = ({ closeModal }) => {
    const [data, setData] = useState({
        firstName: '',
        lastName: '',
        username: '',
        email: '',
        password: ''
    });

    const registerUser = async (e) => {
        e.preventDefault();
        const { firstName, lastName, username, email, password } = data;
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ firstName, lastName, username, email, password }),
            });
            
            if (response.ok) {
                setData({});
                toast.success('User registered successfully');
                return;
            }

            const {responseData} = await response.json();
            if (responseData.error) {
                toast.error(responseData.error);
            }

        } catch (error) {
            toast.error('Failed to register user');
            console.error('Failed to register user', error
            );
        }
    }

    return (
        <form onSubmit={registerUser}>
            <label htmlFor="first-name" className="text-sm">
                First name
            </label>
            <input
            id="first-name"
            type="text"
            placeholder="Mario"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.firstName}
            onChange={(e) => setData({...data, firstName: e.target.value})}
            />

            <label htmlFor="last-name" className="text-sm">
                Last Name
            </label>
            <input
            id="last-name"
            type="text"
            placeholder="Rossi"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.lastName}
            onChange={(e) => setData({...data, lastName: e.target.value})}
            />

            <label htmlFor="username" className="text-sm">
                Username
            </label>
            <input
            id="username"
            type="text"
            placeholder="Username"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.username}
            onChange={(e) => setData({...data, username: e.target.value})}
            />

            <label htmlFor="email" className="text-sm">
                Email
            </label>
            <input
            type="email"
            placeholder="Email"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.email}
            onChange={(e) => setData({...data, email: e.target.value})}
            />

            <label htmlFor="password" className="text-sm">
                Password
            </label>
            <input
            type="password"
            placeholder="Password"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.password}
            onChange={(e) => setData({...data, password: e.target.value})}
            />

            <button type="submit" className="w-full bg-medium-custom text-white p-2 rounded cursor-pointer">
                Register
            </button>
        </form>
  )
};

export default Register;