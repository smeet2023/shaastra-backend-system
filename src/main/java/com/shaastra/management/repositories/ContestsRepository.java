package com.shaastra.management.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shaastra.management.entities.Contests;

@Repository
public interface ContestsRepository extends JpaRepository<Contests, Integer> {
    @Query("SELECT c FROM Contests c WHERE c.contest_date > :dateTime")
    List<Contests> findByContestDateAfter(@Param("dateTime") OffsetDateTime dateTime);
}