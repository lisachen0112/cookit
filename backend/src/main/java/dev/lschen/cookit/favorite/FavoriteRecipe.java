package dev.lschen.cookit.favorite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.lschen.cookit.recipe.Recipe;
import dev.lschen.cookit.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="favorites")
@EntityListeners(AuditingEntityListener.class)
public class FavoriteRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "favorited_by", nullable = false, updatable = false)
    @JsonIgnore
    private User favoritedBy;

    @ManyToOne
    @JoinColumn(name = "favorited_recipe", nullable = false, updatable = false)
    @JsonIgnore
    private Recipe recipe;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime favoritedAt;
}
