package com.shaastra.management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.Contests;
import com.shaastra.management.entities.Students;

@Repository
public interface StudentsRepository extends JpaRepository<Students, Long> {
    // Custom query to retrieve contest history for a student.
    @Query("SELECT c FROM Contests c JOIN c.participants cp JOIN cp.student s WHERE s.erp_id = :erpId")
    List<Contests> findContestHistoryByErpId(@Param("erpId") long erpId);
    @Query(value = "SELECT * FROM students WHERE sh_id = :shId", nativeQuery = true)
    Optional<Students> findByShId(@Param("shId") String shId);
}
