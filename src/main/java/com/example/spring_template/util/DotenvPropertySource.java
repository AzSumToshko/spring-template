package com.example.spring_template.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Set;

public class DotenvPropertySource extends EnumerablePropertySource<Dotenv> {

    public DotenvPropertySource(String name, Dotenv dotenv) {
        super(name, dotenv);
    }

    @Override
    public String[] getPropertyNames() {
        return this.source.entries().stream()
                .map(DotenvEntry::getKey)
                .toArray(String[]::new);
    }

    @Override
    public Object getProperty(String name) {
        return this.source.get(name);
    }
}