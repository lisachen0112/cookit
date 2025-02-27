import { createContext, useState, useEffect } from "react";

export const UserContext = createContext({});

export function UserContextProvider({ children }) {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);


    useEffect(() => {
        if (!user) {
            const token = localStorage.getItem('token');
            if (token) {
                fetchPrincipalInfo(token);
                setIsAuthenticated(true);
            }
        }
    }, []);

    const fetchPrincipalInfo = (token) => {
        fetch("/api/users", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
          .then((response) => {
            if (!response.ok) {
              throw new Error("Failed to fetch user data");
            }
            return response.json();
          })
          .then((data) => {
            setUser(data);
          })
          .catch((error) => {
            console.error("Error fetching user data:", error);
          });
      };

    const logout = () => {
        localStorage.removeItem('token');
        setIsAuthenticated(false);
        setUser(null);
    }


  return (
    <UserContext.Provider value={{user, isAuthenticated, logout, setIsAuthenticated}}>
      {children}
    </UserContext.Provider>
  );
}



