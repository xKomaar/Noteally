package pl.noteally.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="notes")
public class Note
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id")
    private Catalog catalog;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "note")
    private List<SharedNote> sharedNotes;
    @Basic
    @Column(name = "title", nullable = false)
    @Size(min = 3, max = 20, message = "Size must be between 3 and 20")
    @NotBlank(message = "Title is required")
    private String title;

    @Basic
    @Column(name = "content", nullable = false)
    @Size(min = 5, max = 500, message = "Size must be between 5 and 500")
    @NotBlank(message = "Content is required")
    private String content;

    @Basic
    @Column(name = "link")
    @Size(max = 50, message = "Size must be maximum 50")
    private String link;

    @Basic
    @CreationTimestamp
    @Column(name = "date", nullable = false, columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Basic
    @Column(name = "owner")
    private String owner;

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", link='" + link + '\'' +
                ", date=" + date +
                '}';
    }
}
