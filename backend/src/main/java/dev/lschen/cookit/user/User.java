package dev.lschen.cookit.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.lschen.cookit.recipe.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Recipe> uploadedRecipes;

    @ManyToMany()
    @JsonManagedReference
    private List<SavedRecipe> savedRecipes;



}
