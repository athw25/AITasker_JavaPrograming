// Notification.java
@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type;

    private boolean isRead;

    private LocalDateTime createdAt;
}