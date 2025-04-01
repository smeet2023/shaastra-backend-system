package com.shaastra.management.handlerImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.ContestProblem;
import com.shaastra.management.exceptions.CustomBadRequestException;
import com.shaastra.management.exceptions.CustomConflictException;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.ContestProblemService;
import com.shaastra.management.repositories.ContestProblemRepository;
import com.shaastra.management.resource_representation.ContestProblemResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContestProblemServiceImpl implements ContestProblemService {

    private final ContestProblemRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public List<ContestProblemResrep> getAll() {
        return repository.findAll().stream()
                .map(entity -> modelMapper.map(entity, ContestProblemResrep.class))
                .collect(Collectors.toList());
    }

    @Override
    public ContestProblemResrep getById(Integer id) {
        ContestProblem cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + id));
        return modelMapper.map(cp, ContestProblemResrep.class);
    }

    @Override
    public ContestProblemResrep create(ContestProblemResrep resrep) {
        ContestProblem cp = modelMapper.map(resrep, ContestProblem.class);
        cp = repository.save(cp);
        return modelMapper.map(cp, ContestProblemResrep.class);
    }

    @Override
    public ContestProblemResrep update(Integer id, ContestProblemResrep resrep) {
        ContestProblem cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + id));
        // Update entity fields manually
        cp = repository.save(cp);
        return modelMapper.map(cp, ContestProblemResrep.class);
    }

    @Override
    public ContestProblemResrep partialUpdate(Integer id, Map<String, Object> updates) {
        ContestProblem cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + id));

        try {
            if (updates.containsKey("problem_title")) {
                Object title = updates.get("problem_title");
                if (title instanceof String newTitle) {
                    // Check for existing title conflict
                    if (repository.existsByProblemTitleAndIdNot(newTitle, id)) {
                        throw new CustomConflictException("A problem with title '" + newTitle + "' already exists.");
                    }
                    cp.setProblem_title(newTitle);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'problem_title'. Expected String.");
                }
            }
            if (updates.containsKey("problem_description")) {
                Object desc = updates.get("problem_description");
                if (desc instanceof String) {
                    cp.setProblem_description((String) desc);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'problem_description'. Expected String.");
                }
            }
            if (updates.containsKey("problem_solution")) {
                Object solution = updates.get("problem_solution");
                if (solution instanceof String) {
                    cp.setProblem_solution((String) solution);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'problem_solution'. Expected String.");
                }
            }
            if (updates.containsKey("problem_difficulty")) {
                Object difficulty = updates.get("problem_difficulty");
                if (difficulty instanceof String) {
                    cp.setProblem_difficulty((String) difficulty);
                } else {
                    throw new CustomBadRequestException("Invalid type for 'problem_difficulty'. Expected String.");
                }
            }
        } catch (ClassCastException | IllegalArgumentException ex) {
            throw new CustomBadRequestException("Error processing partial update: " + ex.getMessage(), ex);
        }

        cp = repository.save(cp);
        return modelMapper.map(cp, ContestProblemResrep.class);
    }

    @Override
    public void delete(Integer id) {
        ContestProblem cp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContestProblem not found with id: " + id));
        repository.delete(cp);
    }

    @Override
    public List<ContestProblemResrep> searchByDifficulty(String difficulty) {
        return repository.findByProblemDifficulty(difficulty).stream()
                .map(entity -> modelMapper.map(entity, ContestProblemResrep.class))
                .collect(Collectors.toList());
    }
}
