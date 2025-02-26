package dev.lschen.cookit.instruction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.lschen.cookit.recipe.Recipe;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="instructions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"recipe_id", "order_index"})
        })
public class Instruction {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference("instructions")
    private Recipe recipe;
}
