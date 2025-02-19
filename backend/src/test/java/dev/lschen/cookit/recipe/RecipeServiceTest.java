package dev.lschen.cookit.recipe;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private InstructionRepository instructionRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;
    private RecipeRequest request;
    private RecipeResponse response;
    private Authentication authentication;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test1")
                .build();
        recipe = Recipe.builder()
                .recipeId(1L)
                .title("title")
                .description("description")
                .ingredients(new ArrayList<>())
                .instructions(new ArrayList<>())
                .build();

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(Ingredient.builder()
                .ingredientId(1L)
                .name("ingredient1")
                .quantity(10)
                .measurement("grams")
                .recipe(recipe)
                .build());
        ingredients.add(Ingredient.builder()
                .ingredientId(2L)
                .name("ingredient2")
                .quantity(5)
                .measurement("cups")
                .recipe(recipe)
                .build());
        recipe.getIngredients().addAll(ingredients);

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(Instruction.builder()
                        .recipe(recipe)
                        .orderIndex(0)
                        .content("Step 1")
                        .type(Instruction.ContentType.TITLE)
                .build());
        recipe.getInstructions().addAll(instructions);

        request = new RecipeRequest("title",
                "description",
                "imageURL",
                "videoUrl",
                ingredients,
                instructions
        );

        response = new RecipeResponse(
                1L,
                "title",
                "description",
                null,
                null,
                ingredients,
                instructions,
                null,
                null,
                null
        );
    }

    @Test
    public void RecipeCreatedCorrectlyFromRequest() {
        when(recipeMapper.toRecipe(any(RecipeRequest.class))).thenReturn(recipe);
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        RecipeResponse result = recipeService.createRecipe(request);

        assertThat(result.title()).isEqualTo(request.title());
        assertThat(result.description()).isEqualTo(request.description());
        assertThat(result.ingredients().size()).isEqualTo(request.ingredients().size());
        assertThat(result.instructions().size()).isEqualTo(request.instructions().size());

        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    public void GetAllRecipesFromRepository() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        List<RecipeResponse> results = recipeService.findAll();

        verify(recipeRepository, times(1)).findAll();
        assertThat(results).isEqualTo(List.of(response));
    }

    @Test
    public void ThrowExceptionWhenTryingToGetNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void ReturnRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(response);

        RecipeResponse result = recipeService.findById(1L);

        verify(recipeRepository, times(1)).findById(1L);
        assertThat(result).isEqualTo(response);
    }

    @Test
    public void ThrowExceptionWhenTryingToDeleteNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).deleteById(anyLong());
    }

    @Test
    public void DeleteRecipeIfExists() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        recipeService.deleteById(1L);

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void ThrowExceptionWhenTryingToUpdateNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }

    @Test
    public void throwExceptionWhenTryingToUpdateOtherUserRecipe() {
        User otherUser = new User();
        recipe.setCreatedBy(otherUser);
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(authentication.getPrincipal()).thenReturn(user);

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot modify other users recipes");

        verify(recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    public void UpdateRecipeIfExists() {
        authentication = mock(UsernamePasswordAuthenticationToken.class);
        recipe.setCreatedBy(user);

        List<Ingredient> newIngredients = new ArrayList<>();
        newIngredients.add(Ingredient.builder()
                .ingredientId(1L)
                .name("ingredient1")
                .quantity(10)
                .measurement("grams")
                .build());

        List<Instruction> newInstructions = new ArrayList<>();
        newInstructions.add(Instruction.builder()
                .orderIndex(0)
                .content("Wash veggies")
                .type(Instruction.ContentType.TEXT)
                .build());

        RecipeRequest updateRequest = new RecipeRequest(
                "new title",
                "new description",
                "newImageUrl",
                "newVideoUrl",
                newIngredients,
                newInstructions);
        Recipe expectedRecipe = Recipe.builder()
                .recipeId(1L)
                .title(updateRequest.title())
                .imageUrl(updateRequest.imageUrl())
                .videoUrl(updateRequest.videoUrl())
                .description(updateRequest.description())
                .ingredients(newIngredients)
                .instructions(newInstructions)
                .build();
        newIngredients.forEach(ingredient -> ingredient.setRecipe(expectedRecipe));
        newInstructions.forEach(instruction -> instruction.setRecipe(expectedRecipe));

        RecipeResponse expectedResponse = new RecipeResponse(
                1L,
                "new title",
                "new description",
                "newImageUrl",
                "newVideoUrl",
                newIngredients,
                newInstructions,
                null,
                null,
                null
        );

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(authentication.getPrincipal()).thenReturn(user);
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(recipeMapper.toRecipeResponse(any(Recipe.class))).thenReturn(expectedResponse);

        RecipeResponse newRecipe = recipeService.updateRecipe(1L, updateRequest, authentication);

        assertThat(newRecipe.title()).isEqualTo(expectedResponse.title());
        assertThat(newRecipe.description()).isEqualTo(expectedResponse.description());
        assertThat(newRecipe.imageUrl()).isEqualTo(expectedResponse.imageUrl());
        assertThat(newRecipe.videoUrl()).isEqualTo(expectedResponse.videoUrl());
        assertThat(newRecipe.ingredients()).isEqualTo(expectedResponse.ingredients());
        assertThat(newRecipe.instructions()).isEqualTo(expectedResponse.instructions());

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(1)).save(any(Recipe.class));
        verify(instructionRepository, times(1)).deleteByRecipe(any(Recipe.class));
    }
}