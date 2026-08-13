package com.graphhire.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@RestController
public class DeveloperController {

    private final Driver driver;

    public DeveloperController(Driver driver) {
        this.driver = driver;
    }
    

    @GetMapping("/api/developers")
    public List<Map<String, Object>> searchDevelopers(
            @RequestParam String skill) {

        try (Session session = driver.session()) {

            return session.executeRead(tx ->
                    tx.run("""
                        MATCH (d:Developer)-[:HAS_SKILL]->(s:Skill)
                        WHERE toLower(s.name) = toLower($skill)
                        RETURN d.name AS name,
                               collect(s.name) AS skills
                        ORDER BY d.name
                    """, Map.of("skill", skill))
                    .list(record -> Map.of(
                            "name", record.get("name").asString(),
                            "skills", record.get("skills").asList()
                    ))
            );
        }
        
    }
    @GetMapping("/api/developers/{name}/projects")
    public List<Map<String, Object>> getDeveloperProjects(
            @PathVariable String name) {

        try (Session session = driver.session()) {

            return session.executeRead(tx ->
                    tx.run("""
                        MATCH (d:Developer {name: $name})
                              -[:HAS_SKILL]->(s:Skill)
                              <-[:USES]-(p:Project)
                        RETURN d.name AS developer,
                               s.name AS skill,
                               p.name AS project
                        ORDER BY p.name
                    """, Map.of("name", name))
                    .list(record -> Map.of(
                            "developer", record.get("developer").asString(),
                            "skill", record.get("skill").asString(),
                            "project", record.get("project").asString()
                    ))
            );
        }
    }
    @GetMapping("/api/developers/{name}")
    public Map<String, Object> getDeveloperProfile(
            @PathVariable String name) {

        try (Session session = driver.session()) {

            return session.executeRead(tx ->
                    tx.run("""
                        MATCH (d:Developer {name: $name})

                        OPTIONAL MATCH (d)-[:HAS_SKILL]->(s:Skill)
                        OPTIONAL MATCH (d)-[:WORKED_ON]->(p:Project)

                        RETURN d.name AS name,
                               d.location AS location,
                               d.bio AS bio,
                               collect(DISTINCT s.name) AS skills,
                               collect(DISTINCT p.name) AS projects
                    """, Map.of("name", name))
                    .single()
                    .asMap()
            );
        }
    }
}