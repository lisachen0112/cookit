package dev.lschen.cookit.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.lschen.cookit.recipe.Recipe;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ingredients")
@Getter
@Setter
@Builder
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ingredientId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String measurement;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonIgnore
    private Recipe recipe;
}
