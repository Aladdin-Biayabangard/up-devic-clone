package com.team.updevic001.configuration.config.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FullTextInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initFullTextIndex() {
        try {
            // 1. tsvector column əlavə et (əgər yoxdursa)
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM information_schema.columns 
                        WHERE table_name='courses' AND column_name='document_with_weights'
                    ) THEN
                        ALTER TABLE courses ADD COLUMN document_with_weights tsvector;
                    END IF;
                END
                $$;
            """);

            // 2. Trigger function yarat (əgər mövcud deyilsə)
            jdbcTemplate.execute("""
                CREATE OR REPLACE FUNCTION courses_tsvector_update_trigger() RETURNS trigger AS $$
                BEGIN
                    NEW.document_with_weights :=
                        setweight(to_tsvector('english', coalesce(NEW.title, '')), 'A') ||
                        setweight(to_tsvector('english', coalesce(NEW.description, '')), 'B');
                    RETURN NEW;
                END
                $$ LANGUAGE plpgsql;
            """);

            // 3. Trigger əlavə et (əgər yoxdursa)
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_trigger WHERE tgname = 'tsvectorupdate'
                    ) THEN
                        CREATE TRIGGER tsvectorupdate
                        BEFORE INSERT OR UPDATE ON courses
                        FOR EACH ROW EXECUTE FUNCTION courses_tsvector_update_trigger();
                    END IF;
                END
                $$;
            """);

            // 4. GIN index əlavə et (əgər yoxdursa)
            jdbcTemplate.execute("""
                DO $$
                BEGIN
                    IF NOT EXISTS (
                        SELECT 1 FROM pg_indexes 
                        WHERE tablename = 'courses' AND indexname = 'courses_document_idx'
                    ) THEN
                        CREATE INDEX courses_document_idx ON courses USING GIN (document_with_weights);
                    END IF;
                END
                $$;
            """);

            System.out.println("PostgreSQL FullText index setup complete.");
        } catch (Exception e) {
            System.out.println("PostgreSQL FullText setup error: " + e.getMessage());
        }
    }
}
