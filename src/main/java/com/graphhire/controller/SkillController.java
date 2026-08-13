package com.graphhire.controller;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SkillController {

    private final Driver driver;

    public SkillController(Driver driver) {
        this.driver = driver;
    }

    @GetMapping("/api/skills")
    public List<String> getSkills() {

        try (Session session = driver.session()) {

            return session.executeRead(tx ->
                    tx.run("""
                        MATCH (s:Skill)
                        RETURN s.name AS name
                        ORDER BY s.name
                    """)
                    .list(record -> record.get("name").asString())
            );
        }
    }
}