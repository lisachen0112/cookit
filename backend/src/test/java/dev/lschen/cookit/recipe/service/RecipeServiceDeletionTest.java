package dev.lschen.cookit.recipe.service;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.recipe.RecipeRepository;
import dev.lschen.cookit.recipe.RecipeService;
import dev.lschen.cookit.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecipeServiceDeletionTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;
    private final Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

    private User principal;

    @BeforeEach
    void setUp() {
        principal = User.builder().userId(1L).username("principal").build();
        recipe = Recipe.builder()
                .recipeId(1L)
                .createdBy(principal)
                .build();

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .build());
        ingredients.add(Ingredient.builder()
                .ingredientId(2L)
                .build());
        recipe.setIngredients(ingredients);

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(Instruction.builder()
                .recipe(recipe)
                .build());
        recipe.setInstructions(instructions);
    }

    @Test
    public void throwExceptionWhenTryingToDeleteNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteById(1L, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void throwExceptionWhenTryingToDeleteOtherUserRecipe() {
        User otherUser = User.builder().userId(2L).username("otherUser").build();
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(authentication.getPrincipal()).thenReturn(otherUser);

        assertThatThrownBy(() -> recipeService.deleteById(1L, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot delete other users recipes");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void deleteRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(authentication.getPrincipal()).thenReturn(principal);

        recipeService.deleteById(1L, authentication);

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

}
