public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByProjectIdOrderBySentAtAsc(Long projectId);
}