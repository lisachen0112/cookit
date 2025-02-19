package dev.lschen.cookit.favorite;

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
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "favorited_by", nullable = false, updatable = false)
    private User favoritedBy;

    @ManyToOne
    @JoinColumn(name = "recipe", nullable = false, updatable = false)
    private Recipe recipe;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime favoritedAt;
}
