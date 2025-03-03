package dev.lschen.cookit.recipe.service;

import dev.lschen.cookit.exception.OperationNotPermittedException;
import dev.lschen.cookit.ingredient.Ingredient;
import dev.lschen.cookit.instruction.ContentType;
import dev.lschen.cookit.instruction.Instruction;
import dev.lschen.cookit.instruction.InstructionRepository;
import dev.lschen.cookit.instruction.InstructionRequest;
import dev.lschen.cookit.recipe.*;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RecipeServiceEditTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private InstructionRepository instructionRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private final Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);
    private Recipe recipeOwn;
    private Recipe recipeOther;
    private RecipeRequest request;
    private User principal;

    @BeforeEach
    void setUp() {
        principal = User.builder().userId(1L).username("principal").build();
        User otherUser = User.builder().userId(2L).username("otherUser").build();

        recipeOwn = Recipe.builder()
                .recipeId(1L)
                .ingredients(new ArrayList<>())
                .instructions(new ArrayList<>())
                .createdBy(principal)
                .build();
        recipeOther = Recipe.builder()
                .recipeId(2L)
                .ingredients(new ArrayList<>())
                .instructions(new ArrayList<>())
                .createdBy(otherUser)
                .build();

        request = new RecipeRequest(
                "request",
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    public void throwExceptionWhenTryingToUpdateNonexistentRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request, authentication))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, times(0)).save(any(Recipe.class));
    }

    @Test
    public void throwExceptionWhenTryingToUpdateOtherUserRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipeOther));
        when(authentication.getPrincipal()).thenReturn(principal);

        assertThatThrownBy(() -> recipeService.updateRecipe(1L, request, authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("Cannot modify other users recipes");

        verify(recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    public void UpdateRecipeIfExists() throws IOException {
        List<InstructionRequest> instructionsRequest = new ArrayList<>();
        instructionsRequest.add(new InstructionRequest(0, ContentType.TEXT, "step1", null));

        RecipeRequest updateRequest = new RecipeRequest(
                "new title",
                "new description",
                null,
                "newVideoUrl",
                List.of("ingredient1"),
                instructionsRequest);


        List<Ingredient> newIngredients = new ArrayList<>();
        newIngredients.add(Ingredient.builder()
                .ingredientId(1L)
                .content("ingredient1")
                .build());

        List<Instruction> newInstructions = new ArrayList<>();
        newInstructions.add(Instruction.builder()
                .orderIndex(0)
                .type(ContentType.TEXT)
                .content("step1")
                .build());
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

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipeOwn));
        when(authentication.getPrincipal()).thenReturn(principal);
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
