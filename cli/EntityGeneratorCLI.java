package cli;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EntityGeneratorCLI {
    // To run the command inside root directory: java cli/EntityGeneratorCLI.java EngineFour
    // To run the command inside root directory: java cli/EntityGeneratorCLI.java Motor model:String horsepower:int ....

    private static final String BASE_PATH = "src/main/java";
    private static final String MIGRATIONS_PATH = "src/main/resources/db/migration";
    private static final String BASE_PACKAGE = resolveBasePackage();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java EntityGeneratorCLI <EntityName> [<field:type>...]");
            return;
        }

        String entityName = args[0];
        List<String> fields = args.length > 1 ? Arrays.asList(Arrays.copyOfRange(args, 1, args.length)) : new ArrayList<>();

        generateEntity(entityName, fields);
        generateDTOs(entityName, fields);
        generateRepository(entityName);
        generateService(entityName);
        generateController(entityName);
        generateMigration(entityName, fields);
        appendSecurityAccess(entityName);

        System.out.println("✅ Generated all components for " + entityName);
    }

    private static String resolveBasePackage() {
        try {
            Path pomPath = Paths.get(System.getProperty("user.dir"), "pom.xml");
            List<String> lines = Files.readAllLines(pomPath);
            String groupId = null;
            String artifactId = null;
            boolean insideParent = false;

            for (String line : lines) {
                line = line.trim();
                if (line.equals("<parent>")) insideParent = true;
                if (line.equals("</parent>")) insideParent = false;
                if (line.startsWith("<groupId>") && !insideParent && groupId == null) {
                    groupId = line.replace("<groupId>", "").replace("</groupId>", "").trim();
                }
                if (line.startsWith("<artifactId>") && !insideParent && artifactId == null) {
                    artifactId = line.replace("<artifactId>", "").replace("</artifactId>", "").trim();
                }
                if (groupId != null && artifactId != null) break;
            }
            if (groupId != null && artifactId != null) {
                return groupId + "." + artifactId.replace("-", "_");
            }
        } catch (IOException ignored) {}
        return "com.example.app";
    }

    private static void generateEntity(String name, List<String> fields) throws IOException {
        String className = name;
        String tableName = name.toLowerCase() + "s";
        String fileName = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/domain/entity/" + className + ".java";

        String allArgs = fields.isEmpty() ? "// @AllArgsConstructor" : "@AllArgsConstructor";

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(BASE_PACKAGE).append(".domain.entity;\n\n")
            .append("import ").append(BASE_PACKAGE).append(".domain.entity.base.BaseEntity;\n")
            .append("import jakarta.persistence.*;\n")
            .append("import lombok.*;\n\n")
            .append("@Entity\n")
            .append("@Table(name = \"").append(tableName).append("\")\n")
            .append("@Getter\n@Setter\n@NoArgsConstructor\n").append(allArgs).append("\n")
            .append("public class ").append(className).append(" extends BaseEntity {\n\n");

        for (String field : fields) {
            String[] parts = field.split(":");
            String fieldName = parts[0];
            String fieldType = parts[1];
            sb.append("    @Column(nullable = false)\n")
                .append("    private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        sb.append("}\n");
        writeFile(fileName, sb.toString());
    }

    private static String getDtoAnnotations(List<String> fields) {
        return fields.isEmpty()
            ? "@Getter\n@Setter\n@NoArgsConstructor\n// @AllArgsConstructor\n"
            : "@Getter\n@Setter\n@NoArgsConstructor\n@AllArgsConstructor\n";
    }

    private static void generateDTOs(String name, List<String> fields) throws IOException {
        String basePath = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/domain/dto/";

        StringBuilder fieldsBuilder = new StringBuilder();
        for (String field : fields) {
            String[] parts = field.split(":");
            fieldsBuilder.append("    private ").append(parts[1]).append(" ").append(parts[0]).append(";\n");
        }

        String annotations = getDtoAnnotations(fields);

        String reqFile = basePath + "request/" + name + "RequestDTO.java";
        String resFile = basePath + "response/" + name + "ResponseDTO.java";

        writeFile(reqFile, "package " + BASE_PACKAGE + ".domain.dto.request;\n\nimport lombok.*;\n\n" + annotations + "public class " + name + "RequestDTO {\n" + fieldsBuilder + "}\n");
        writeFile(resFile, "package " + BASE_PACKAGE + ".domain.dto.response;\n\nimport lombok.*;\nimport java.util.UUID;\n\n" + "@Getter\n@Setter\n@NoArgsConstructor\n@AllArgsConstructor\n" + "public class " + name + "ResponseDTO {\n    private UUID id;\n" + fieldsBuilder + "}\n");
    }

    private static void generateRepository(String name) throws IOException {
        String fileName = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/repository/" + name + "Repository.java";
        String content = "package " + BASE_PACKAGE + ".repository;\n\n" +
            "import " + BASE_PACKAGE + ".domain.entity." + name + ";\n" +
            "import " + BASE_PACKAGE + ".repository.base.BaseRepository;\n" +
            "import org.springframework.stereotype.Repository;\n\n" +
            "@Repository\n" +
            "public interface " + name + "Repository extends BaseRepository<" + name + "> { }\n";

        writeFile(fileName, content);
    }

    private static void generateService(String name) throws IOException {
        String fileName = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/service/crud/" + name + "Service.java";
        String content = "package " + BASE_PACKAGE + ".service.crud;\n\n" +
            "import " + BASE_PACKAGE + ".domain.dto.request." + name + "RequestDTO;\n" +
            "import " + BASE_PACKAGE + ".domain.dto.response." + name + "ResponseDTO;\n" +
            "import " + BASE_PACKAGE + ".domain.entity." + name + ";\n" +
            "import " + BASE_PACKAGE + ".repository." + name + "Repository;\n" +
            "import " + BASE_PACKAGE + ".service.crud.base.BaseService;\n" +
            "import org.modelmapper.ModelMapper;\n" +
            "import org.springframework.context.MessageSource;\n" +
            "import org.springframework.stereotype.Service;\n\n" +
            "@Service\n" +
            "public class " + name + "Service extends BaseService<" + name + ", " + name + "RequestDTO, " + name + "ResponseDTO> {\n" +
            "    public " + name + "Service(" + name + "Repository repository, ModelMapper modelMapper, MessageSource messageSource) {\n" +
            "        super(repository, modelMapper, " + name + ".class, " + name + "ResponseDTO.class, messageSource);\n" +
            "    }\n" +
            "}\n";

        writeFile(fileName, content);
    }

    private static void generateController(String name) throws IOException {
        String fileName = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/controller/" + name + "Controller.java";
        String content = "package " + BASE_PACKAGE + ".controller;\n\n" +
            "import " + BASE_PACKAGE + ".constant.Constants;\n" +
            "import " + BASE_PACKAGE + ".controller.base.BaseController;\n" +
            "import " + BASE_PACKAGE + ".domain.dto.request." + name + "RequestDTO;\n" +
            "import " + BASE_PACKAGE + ".domain.dto.response." + name + "ResponseDTO;\n" +
            "import " + BASE_PACKAGE + ".domain.entity." + name + ";\n" +
            "import " + BASE_PACKAGE + ".service.crud." + name + "Service;\n" +
            "import io.swagger.v3.oas.annotations.tags.Tag;\n" +
            "import org.springframework.context.ApplicationEventPublisher;\n" +
            "import org.springframework.web.bind.annotation.*;\n\n" +
            "@RestController\n" +
            "@RequestMapping(Constants.API_ENDPOINT + \"/" + name.toLowerCase() + "s\")\n" +
            "@Tag(name = \"" + name + " API\", description = \"Operations related to " + name.toLowerCase() + "s.\")\n" +
            "public class " + name + "Controller extends BaseController<" + name + ", " + name + "RequestDTO, " + name + "ResponseDTO> {\n" +
            "    private final ApplicationEventPublisher eventPublisher;\n\n" +
            "    public " + name + "Controller(" + name + "Service service, ApplicationEventPublisher eventPublisher) {\n" +
            "        super(service);\n" +
            "        this.eventPublisher = eventPublisher;\n" +
            "    }\n" +
            "}\n";

        writeFile(fileName, content);
    }

    private static void generateMigration(String name, List<String> fields) throws IOException {
        double latestVersion = Files.list(Paths.get(MIGRATIONS_PATH))
            .map(path -> path.getFileName().toString())
            .filter(f -> f.matches("V\\d+\\.\\d+__.*"))
            .map(f -> f.substring(1, f.indexOf("__")))
            .mapToDouble(version -> {
                try {
                    return Double.parseDouble(version);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            })
            .max().orElse(1.0);

        double nextVersion = latestVersion + 0.1;
        String formattedVersion = String.format("%.1f", nextVersion).replace(',', '.');
        String filename = String.format("%s/V%s__CREATE_%s_TABLE.sql", MIGRATIONS_PATH, formattedVersion, name.toUpperCase());
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(name.toLowerCase()).append("s (\n")
            .append("    id UUID PRIMARY KEY");
        for (String field : fields) {
            String[] parts = field.split(":");
            String type = mapType(parts[1]);
            sb.append(",\n    ").append(parts[0]).append(" ").append(type).append(" NOT NULL");
        }
        sb.append("\n);");
        writeFile(filename, sb.toString());
    }

    private static void appendSecurityAccess(String entityName) throws IOException {
        String path = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/config/SecurityConfiguration.java";
        Path filePath = Path.of(path);
        List<String> lines = Files.readAllLines(filePath);

        String endpoint = "/" + entityName.toLowerCase() + "s/**";
        String base = "                    .requestMatchers(HttpMethod.";

        List<String> newRules = List.of(
            base + "GET, API_ENDPOINT + \"" + endpoint + "\").permitAll()",
            base + "POST, API_ENDPOINT + \"" + endpoint + "\").hasAuthority(Roles.ADMIN.name())",
            base + "PUT, API_ENDPOINT + \"" + endpoint + "\").hasAuthority(Roles.ADMIN.name())",
            base + "DELETE, API_ENDPOINT + \"" + endpoint + "\").hasAuthority(Roles.ADMIN.name())\n"
        );

        int insertIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(".requestMatchers(\"/api/v1/admin/**\")")) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex != -1) {
            lines.addAll(insertIndex, newRules);
            Files.write(filePath, lines);
            System.out.println("🔐 Security access appended for: " + endpoint);
        }
    }

    private static String mapType(String javaType) {
        return switch (javaType) {
            case "String" -> "VARCHAR(255)";
            case "int", "Integer" -> "INTEGER";
            case "long", "Long" -> "BIGINT";
            case "boolean", "Boolean" -> "BOOLEAN";
            case "double", "Double" -> "DOUBLE PRECISION";
            default -> javaType.toUpperCase();
        };
    }

    private static void writeFile(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        Files.writeString(Path.of(path), content);
    }
}
