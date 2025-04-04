package com.shaastra.management.handler;

import com.shaastra.management.entities.Admin;

public interface AdminService {
    Admin register(Admin admin);
    Admin getById(Long id);
    Admin update(Long id, Admin admin);
    void delete(Long id);
}
