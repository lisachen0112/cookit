import { useEffect, useState } from "react";
import { useLocation, useOutletContext } from "react-router-dom";
import Spinner from "../components/Spinner";
import RecipesList from "../components/RecipesList";
import { useContext } from "react";
import { UserContext } from "../../context/userContext";

const RecipeListPage = () => {
  const location = useLocation();
  const { openAuthModal } = useOutletContext();
  const { user, isAuthenticated } = useContext(UserContext);
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState("");

  const pageType = location.pathname.includes("favorites")
    ? "favorites"
    : location.pathname.includes("my-recipes")
    ? "my-recipes"
    : "all";

    useEffect(() => {
      if (isAuthenticated || pageType === "all") {
        fetchRecipes();
      } else {
        openAuthModal();
      }
    }, [pageType, isAuthenticated]);
  

  const fetchRecipes = async () => {
    let url = "/api/recipes";
    setTitle("Browse recipes");
    if (pageType === "my-recipes") {
        setTitle("My recipes");
        url = `/api/users/${user.userId}/recipes`;
    }
    if (pageType === "favorites") {
        setTitle("Your Favorites");
        url = `/api/users/${user.userId}/favorites`;
    } 

    try {    
        const response = await fetch(url);
        const data = await response.json();
        setRecipes(data);
    } catch (error) {
        console.error('Error fetching recipes: ', error);
    } finally {
        setLoading(false);
    }
  };

  return (
    <div>
        {loading ? (<Spinner loading={loading}/>) : (
        <RecipesList recipes={recipes} title={title}/>
        )}  
    </div>
  );
};

export default RecipeListPage;