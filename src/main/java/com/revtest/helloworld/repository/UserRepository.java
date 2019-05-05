package com.revtest.helloworld.repository;


import org.springframework.data.repository.CrudRepository;

import com.revtest.helloworld.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    /*
     * Get user by user name. Please note the format should be
     * findBy<column_name>.
     */
    User findByUserName(String username);   

}
