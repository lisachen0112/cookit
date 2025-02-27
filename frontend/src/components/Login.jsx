import { useState } from 'react';
import { toast } from 'react-hot-toast';
import { UserContext } from '../../context/userContext';
import { useContext } from 'react';

const Login = ({ closeModal }) => {
    const { setIsAuthenticated } = useContext(UserContext);
    const [data, setData] = useState({
        username: '',
        password: ''
    });

    const loginUser = async (e) => {
        e.preventDefault();
        const { username, password } = data;
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });
            const responseData = await response.json();

            if (!response.ok) {
                if (responseData?.validationErrors) {
                    responseData.validationErrors.forEach(error => toast.error(error));
                } else if (responseData?.error) {
                    toast.error(responseData.error);
                }
                return;
            }
            localStorage.setItem('token', responseData.token);
            closeModal();
            setData({});
            toast.success("Let's cook!");
            window.location.reload();
        } catch (error) {
            toast.error('Something went wrong. Please try again.');
            console.error('Login error:', error);
        }
    }

    return (
        <form onSubmit={loginUser}>
            <label htmlFor="username" className="text-sm">
                Username
            </label>
            <input
            type="text"
            placeholder="Username"
            className="w-full p-2 mb-3 border border-gray-300 rounded"
            value={data.username}
            onChange={(e) => setData({...data, username: e.target.value})}
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
                Login
            </button>
        </form>
    )
}

export default Login