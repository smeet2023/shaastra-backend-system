package com.shaastra.management.handlerImpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shaastra.management.entities.Admin;
import com.shaastra.management.exceptions.ResourceNotFoundException;
import com.shaastra.management.handler.AdminService;
import com.shaastra.management.repositories.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Admin register(Admin admin) {
        // Here you could add extra logic such as password encoding
    	admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    @Override
    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + id));
    }

    @Override
    public Admin update(Long id, Admin admin) {
        Admin existing = getById(id);
        existing.setUsername(admin.getUsername());
        existing.setPassword(admin.getPassword()); // In real apps, encode the password!
        existing.setEmail(admin.getEmail());
        return adminRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}
