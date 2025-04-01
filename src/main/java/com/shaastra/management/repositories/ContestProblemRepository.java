package com.shaastra.management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.ContestProblem;

@Repository
public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer> {
    @Query("SELECT cp FROM ContestProblem cp WHERE cp.problem_difficulty = :problemDifficulty")
    List<ContestProblem> findByProblemDifficulty(@Param("problemDifficulty") String problemDifficulty);
    
    @Query("SELECT COUNT(cp) > 0 FROM ContestProblem cp WHERE cp.problem_title = :problemTitle AND cp.id <> :id")
    boolean existsByProblemTitleAndIdNot(@Param("problemTitle") String problemTitle, @Param("id") Integer id);
}
