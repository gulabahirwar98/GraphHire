package com.graphhire.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProjectController {

    private final Driver driver;

    public ProjectController(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/api/projects/{name}")
    public Map<String, Object> getProjectProfile(
            @PathVariable String name) {

        try (Session session = driver.session()) {

            return session.executeRead(tx ->
                    tx.run("""
                        MATCH (p:Project {name: $name})

                        OPTIONAL MATCH (p)-[:USES]->(s:Skill)
                        OPTIONAL MATCH (d:Developer)-[:WORKED_ON]->(p)

                        RETURN p.name AS name,
                               collect(DISTINCT s.name) AS skills,
                               collect(DISTINCT d.name) AS developers
                    """, Map.of("name", name))
                    .single()
                    .asMap()
            );
        }
    }
}