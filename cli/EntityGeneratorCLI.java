package cli;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EntityGeneratorCLI {

    private static final String BASE_PATH = "src/main/java";
    private static final String MIGRATIONS_PATH = "src/main/resources/db/migration";
    private static final String BASE_PACKAGE = "com.example.spring_template";

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java EntityGeneratorCLI <EntityName> <field:type> [<field:type>...]");
            return;
        }

        String entityName = args[0];
        List<String> fields = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));

        generateEntity(entityName, fields);
        generateDTOs(entityName, fields);
        generateRepository(entityName);
        generateService(entityName);
        generateController(entityName);
        generateMigration(entityName, fields);

        System.out.println("✅ Generated all components for " + entityName);
    }

    private static void generateEntity(String name, List<String> fields) throws IOException {
        String className = name;
        String tableName = name.toLowerCase() + "s";
        String fileName = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/domain/entity/" + className + ".java";

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(BASE_PACKAGE).append(".domain.entity;\n\n")
            .append("import ").append(BASE_PACKAGE).append(".domain.entity.base.BaseEntity;\n")
            .append("import jakarta.persistence.*;\n")
            .append("import lombok.*;\n\n")
            .append("@Entity\n")
            .append("@Table(name = \"").append(tableName).append("\")\n")
            .append("@Getter\n@Setter\n@NoArgsConstructor\n@AllArgsConstructor\n")
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

    private static void generateDTOs(String name, List<String> fields) throws IOException {
        String basePath = BASE_PATH + "/" + BASE_PACKAGE.replace(".", "/") + "/domain/dto/";

        StringBuilder fieldsBuilder = new StringBuilder();
        for (String field : fields) {
            String[] parts = field.split(":");
            fieldsBuilder.append("    private ").append(parts[1]).append(" ").append(parts[0]).append(";\n");
        }

        String annotations = "@Getter\n@Setter\n@AllArgsConstructor\n@NoArgsConstructor\n";

        String reqFile = basePath + "request/" + name + "RequestDTO.java";
        String resFile = basePath + "response/" + name + "ResponseDTO.java";

        writeFile(reqFile, "package " + BASE_PACKAGE + ".domain.dto.request;\n\nimport lombok.*;\n\n" + annotations + "public class " + name + "RequestDTO {\n" + fieldsBuilder + "}\n");
        writeFile(resFile, "package " + BASE_PACKAGE + ".domain.dto.response;\n\nimport lombok.*;\nimport java.util.UUID;\n\n" + annotations + "public class " + name + "ResponseDTO {\n    private UUID id;\n" + fieldsBuilder + "}\n");
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
        int latestVersion = Files.list(Paths.get(MIGRATIONS_PATH))
            .map(path -> path.getFileName().toString())
            .filter(f -> f.startsWith("V1."))
            .map(f -> f.replace("V1.", "").split("__")[0])
            .mapToInt(Integer::parseInt)
            .max().orElse(0);

        int nextVersion = latestVersion + 1;
        String filename = String.format("%s/V1.%d__CREATE_%s_TABLE.sql", MIGRATIONS_PATH, nextVersion, name.toUpperCase());
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(name.toLowerCase()).append("s (\n")
            .append("    id UUID PRIMARY KEY,\n");
        for (String field : fields) {
            String[] parts = field.split(":");
            String type = mapType(parts[1]);
            sb.append("    ").append(parts[0]).append(" ").append(type).append(" NOT NULL,\n");
        }
        sb.setLength(sb.length() - 2); // Remove trailing comma
        sb.append("\n);\n");
        writeFile(filename, sb.toString());
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
