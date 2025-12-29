package webtech.online.course.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import webtech.online.course.models.MCPContent;
import webtech.online.course.models.Question;

@Repository
public interface McqContentRepository extends JpaRepository<MCPContent, Long> {
    @Query("""
        SELECT mcp FROM MCPContent mcp
        WHERE mcp.id = :id
    """)
    MCPContent findFullTree(@Param("id") Long id);
}
