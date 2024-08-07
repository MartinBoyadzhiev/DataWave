package com.datawave.datawaveapp;


import com.datawave.datawaveapp.model.entity.RoleEntity;
import com.datawave.datawaveapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.datawave.datawaveapp.repository")
public class DataWaveApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(DataWaveApplication.class, args);

    }

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (this.roleRepository.count() == 0) {
            this.roleRepository.save(new RoleEntity("USER"));
            this.roleRepository.save(new RoleEntity("ADMIN"));
        }
    }
}
