package com.graphhire.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class SeedService implements CommandLineRunner {

    private final Driver driver;

    public SeedService(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {

        try (Session session = driver.session()) {

            session.executeWrite(tx -> {

                tx.run("""
                    MERGE (d:Developer {name: 'Gulab'})

    SET d.location = 'Bhopal',
        d.bio = 'Java Backend Developer passionate about Spring Boot and REST APIs'
                    MERGE (java:Skill {name: 'Java'})
                    MERGE (spring:Skill {name: 'Spring Boot'})
                    MERGE (mysql:Skill {name: 'MySQL'})
                    MERGE (project:Project {name: 'AROVIA'})

                    MERGE (d)-[:HAS_SKILL]->(java)
                    MERGE (d)-[:HAS_SKILL]->(spring)
                    MERGE (d)-[:HAS_SKILL]->(mysql)

                    MERGE (d)-[:WORKED_ON]->(project)

                    MERGE (project)-[:USES]->(java)
                    MERGE (project)-[:USES]->(spring)
                    MERGE (project)-[:USES]->(mysql)
                """);

                return null;
            });
        }

        System.out.println("GraphHire seed data inserted successfully!");
    }
}