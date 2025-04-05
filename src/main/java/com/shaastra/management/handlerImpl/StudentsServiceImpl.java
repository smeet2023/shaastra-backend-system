package com.shaastra.management.handlerImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.Contests;
import com.shaastra.management.entities.Students;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.StudentsService;
import com.shaastra.management.repositories.StudentsRepository;
import com.shaastra.management.resource_representation.ContestsResrep;
import com.shaastra.management.resource_representation.StudentsResrep;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentsServiceImpl implements StudentsService {

    private final StudentsRepository repository;
    private final ModelMapper modelMapper;
    // Note: For contest history, you might inject the ContestsRepository or use the custom query from StudentsRepository
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public List<StudentsResrep> getAll() {
        return repository.findAll().stream()
                .map(entity -> modelMapper.map(entity, StudentsResrep.class))
                .collect(Collectors.toList());
    }

    @Override
    public StudentsResrep getByErpId(long erpId) {
        Students student = repository.findById(erpId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with erp_id: " + erpId));
        return modelMapper.map(student, StudentsResrep.class);
    }

    @Override
    public StudentsResrep create(StudentsResrep resrep) {
        Students student = modelMapper.map(resrep, Students.class);
        String rawPassword = student.getCoding_contest_password();
        student.setCoding_contest_password(passwordEncoder.encode(rawPassword));
        student = repository.save(student);
        return modelMapper.map(student, StudentsResrep.class);
    }

    @Override
    public StudentsResrep update(long erpId, StudentsResrep resrep) {
        Students student = repository.findById(erpId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with erp_id: " + erpId));
        // Update fields as needed
        student = repository.save(student);
        return modelMapper.map(student, StudentsResrep.class);
    }

    @Override
    public StudentsResrep partialUpdate(long erpId, Map<String, Object> updates) {
        Students student = repository.findById(erpId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with erp_id: " + erpId));
        if (updates.containsKey("phone")) {
            student.setPhone((String) updates.get("phone"));
        }
        // Process additional fields accordingly.
        student = repository.save(student);
        return modelMapper.map(student, StudentsResrep.class);
    }

    @Override
    public void delete(long erpId) {
        Students student = repository.findById(erpId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with erp_id: " + erpId));
        repository.delete(student);
    }

    @Override
    public List<ContestsResrep> getContestHistory(long erpId) {
        List<Contests> contests = repository.findContestHistoryByErpId(erpId);
        return contests.stream()
                .map(contest -> modelMapper.map(contest, ContestsResrep.class))
                .collect(Collectors.toList());
    }
}
