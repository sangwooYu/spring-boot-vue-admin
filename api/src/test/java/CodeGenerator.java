import com.google.common.base.CaseFormat;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zoctan.api.core.constant.ProjectConstant.*;

/**
 * 코드 생성기 코드 생성기는 다음을 기반으로 해당 데이터 테이블 이름을 생성합니다. Entity、Mapper、Service、Controller 개발 간소화
 *
 * @author Zoctan
 * @date 2018/05/27
 */
class CodeGenerator {
  // JDBC 구성은 프로젝트의 실제 구성으로 변경하십시오.
  private static final String JDBC_URL =
      "jdbc:mysql://localhost:3306/admin_test"
          + "?useUnicode=true&characterEncoding=utf-8&useLegacyDatetimeCode=false&serverTimezone=UTC";
  private static final String JDBC_USERNAME = "root";
  private static final String JDBC_PASSWORD = "root";
  private static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
  // 하드 디스크에 있는 프로젝트의 기본 경로
  private static final String PROJECT_PATH = System.getProperty("user.dir");
  // 템플릿 위치
  private static final String TEMPLATE_FILE_PATH =
      CodeGenerator.PROJECT_PATH + "/src/test/resources/generator/template";
  // 자바 파일 경로
  private static final String JAVA_PATH = "/src/main/java";
  // 리소스 파일 경로
  private static final String RESOURCES_PATH = "/src/main/resources";
  // 생성된 서비스 경로
  private static final String PACKAGE_PATH_SERVICE =
      CodeGenerator.packageConvertPath(SERVICE_PACKAGE);
  // 생성된 서비스 구현 경로
  private static final String PACKAGE_PATH_SERVICE_IMPL =
      CodeGenerator.packageConvertPath(SERVICE_IMPL_PACKAGE);
  // 생성된 컨트롤러의 경로
  private static final String PACKAGE_PATH_CONTROLLER =
      CodeGenerator.packageConvertPath(CONTROLLER_PACKAGE);

  // @author
  private static final String AUTHOR = "Zoctan";
  // @date
  private static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
  private static final boolean isRestful = true;

  public static void main(final String[] args) {
    final Scanner scanner = new Scanner(System.in);
    System.out.print("관련 문서가 이미 존재할 수 있으므로 가능한 한 오류가 없는지 확인하세요. y 또는 n 을 입력해주세요");
    if (!scanner.next().equals("y")) {
      return;
    }
//    CodeGenerator.genCode("report", "wechat");
//    // genCodeByCustomModelName("输入表名","输入自定义Model名称");
  }

  /**
   * 코드는 데이터 테이블 이름에서 생성되며, 모델 이름은 데이터 테이블 이름을 파싱하여 얻고, 밑줄은 큰 고비 형식으로 구문 분석하여 얻습니다. 예를 들어, 테이블 이름 "t_user_detail"을 입력하면 다음과 같은 코드가 생성됩니다.
   * TUserDetail、TUserDetailMapper、TUserDetailService ...
   *
   * @param tableNames 데이터 시트 이름...
   */
  private static void genCode(final String... tableNames) {
    for (final String tableName : tableNames) {
      CodeGenerator.genCodeByCustomModelName(tableName, null);
    }
  }

  /**
   * 테이블 이름 및 사용자 지정 모델 이름으로 코드를 생성합니다. 예: 테이블 이름 "t_user_detail"을 입력하고 사용자 지정 모델 이름 "sysUser"를 입력하면 다음과 같은 코드가 생성됩니다.
   * sysUser、UserMapper、UserService ...
   *
   * @param tableName 데이터 시트 이름
   * @param modelName 사용자 지정 모델 이름
   */
  private static void genCodeByCustomModelName(final String tableName, final String modelName) {
    CodeGenerator.genModelAndMapper(tableName, modelName);
    CodeGenerator.genService(tableName, modelName);
    CodeGenerator.genController(tableName, modelName);
  }

  private static void genModelAndMapper(final String tableName, String modelName) {
    final Context context = new Context(ModelType.FLAT);
    context.setId("Potato");
    context.setTargetRuntime("MyBatis3Simple");
    context.addProperty(PropertyRegistry.CONTEXT_BEGINNING_DELIMITER, "`");
    context.addProperty(PropertyRegistry.CONTEXT_ENDING_DELIMITER, "`");

    final JDBCConnectionConfiguration jdbcConnectionConfiguration =
        new JDBCConnectionConfiguration();
    jdbcConnectionConfiguration.setConnectionURL(CodeGenerator.JDBC_URL);
    jdbcConnectionConfiguration.setUserId(CodeGenerator.JDBC_USERNAME);
    jdbcConnectionConfiguration.setPassword(CodeGenerator.JDBC_PASSWORD);
    jdbcConnectionConfiguration.setDriverClass(CodeGenerator.JDBC_DIVER_CLASS_NAME);
    context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

    final PluginConfiguration pluginConfiguration = new PluginConfiguration();
    pluginConfiguration.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
    pluginConfiguration.addProperty("mappers", MAPPER_INTERFACE_REFERENCE);
    context.addPluginConfiguration(pluginConfiguration);

    final JavaModelGeneratorConfiguration javaModelGeneratorConfiguration =
        new JavaModelGeneratorConfiguration();
    javaModelGeneratorConfiguration.setTargetProject(
        CodeGenerator.PROJECT_PATH + CodeGenerator.JAVA_PATH);
    javaModelGeneratorConfiguration.setTargetPackage(ENTITY_PACKAGE);
    context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

    final SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration =
        new SqlMapGeneratorConfiguration();
    sqlMapGeneratorConfiguration.setTargetProject(
        CodeGenerator.PROJECT_PATH + CodeGenerator.RESOURCES_PATH);
    sqlMapGeneratorConfiguration.setTargetPackage("mapper");
    context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

    final JavaClientGeneratorConfiguration javaClientGeneratorConfiguration =
        new JavaClientGeneratorConfiguration();
    javaClientGeneratorConfiguration.setTargetProject(
        CodeGenerator.PROJECT_PATH + CodeGenerator.JAVA_PATH);
    javaClientGeneratorConfiguration.setTargetPackage(MAPPER_PACKAGE);
    javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
    context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

    final TableConfiguration tableConfiguration = new TableConfiguration(context);
    tableConfiguration.setTableName(tableName);
    if (StringUtils.isNotEmpty(modelName)) {
      tableConfiguration.setDomainObjectName(modelName);
    }
    tableConfiguration.setGeneratedKey(new GeneratedKey("id", "Mysql", true, null));
    context.addTableConfiguration(tableConfiguration);

    final List<String> warnings;
    final MyBatisGenerator generator;
    try {
      final Configuration config = new Configuration();
      config.addContext(context);
      config.validate();

      final DefaultShellCallback callback = new DefaultShellCallback(true);
      warnings = new ArrayList<>();
      generator = new MyBatisGenerator(config, callback, warnings);
      generator.generate(null);
    } catch (final Exception e) {
      throw new RuntimeException("모델 및 매퍼 생성 실패", e);
    }

    if (generator.getGeneratedJavaFiles().isEmpty() || generator.getGeneratedXmlFiles().isEmpty()) {
      throw new RuntimeException("모델 및 매퍼를 생성하지 못했습니다:" + warnings);
    }
    if (StringUtils.isEmpty(modelName)) {
      modelName = CodeGenerator.tableNameConvertUpperCamel(tableName);
    }
    System.out.println(modelName + ".java가 성공적으로 생성되었습니다.");
    System.out.println(modelName + "MyMapper.java가 성공적으로 생성되었습니다.");
    System.out.println(modelName + "MyMapper.xml이 성공적으로 생성되었습니다.");
  }

  private static void genService(final String tableName, final String modelName) {
    try {
      final freemarker.template.Configuration cfg = CodeGenerator.getConfiguration();

      final Map<String, Object> data = new HashMap<>();
      data.put("date", CodeGenerator.DATE);
      data.put("author", CodeGenerator.AUTHOR);
      final String modelNameUpperCamel =
          StringUtils.isEmpty(modelName)
              ? CodeGenerator.tableNameConvertUpperCamel(tableName)
              : modelName;
      data.put("modelNameUpperCamel", modelNameUpperCamel);
      data.put("modelNameLowerCamel", CodeGenerator.tableNameConvertLowerCamel(tableName));
      data.put("basePackage", BASE_PACKAGE);

      final File file =
          CodeGenerator.createFileDir(
              CodeGenerator.PROJECT_PATH
                  + CodeGenerator.JAVA_PATH
                  + CodeGenerator.PACKAGE_PATH_SERVICE
                  + modelNameUpperCamel
                  + "Service.java");
      cfg.getTemplate("service.ftl").process(data, new FileWriter(file));
      System.out.println(modelNameUpperCamel + "Service.java가 성공적으로 생성되었습니다.");

      final File file1 =
          CodeGenerator.createFileDir(
              CodeGenerator.PROJECT_PATH
                  + CodeGenerator.JAVA_PATH
                  + CodeGenerator.PACKAGE_PATH_SERVICE_IMPL
                  + modelNameUpperCamel
                  + "ServiceImpl.java");
      cfg.getTemplate("service-impl.ftl").process(data, new FileWriter(file1));
      System.out.println(modelNameUpperCamel + "ServiceImpl.java가 성공적으로 생성되었습니다.");
    } catch (final Exception e) {
      throw new RuntimeException("서비스 생성에 실패했습니다.", e);
    }
  }

  private static File createFileDir(final String name) throws RuntimeException {
    final File file = new File(name);
    if (!file.getParentFile().exists()) {
      final boolean createSuccess = file.getParentFile().mkdirs();
      if (!createSuccess) {
        throw new RuntimeException("폴더 만들기에 실패했습니다.");
      }
    }
    return file;
  }

  private static void genController(final String tableName, final String modelName) {
    try {
      final freemarker.template.Configuration cfg = CodeGenerator.getConfiguration();

      final Map<String, Object> data = new HashMap<>();
      data.put("date", CodeGenerator.DATE);
      data.put("author", CodeGenerator.AUTHOR);
      final String modelNameUpperCamel =
          StringUtils.isEmpty(modelName)
              ? CodeGenerator.tableNameConvertUpperCamel(tableName)
              : modelName;
      data.put(
          "baseRequestMapping", CodeGenerator.modelNameConvertMappingPath(modelNameUpperCamel));
      data.put("modelNameUpperCamel", modelNameUpperCamel);
      data.put(
          "modelNameLowerCamel",
          CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, modelNameUpperCamel));
      data.put("basePackage", BASE_PACKAGE);

      final File file =
          CodeGenerator.createFileDir(
              CodeGenerator.PROJECT_PATH
                  + CodeGenerator.JAVA_PATH
                  + CodeGenerator.PACKAGE_PATH_CONTROLLER
                  + modelNameUpperCamel
                  + "Controller.java");

      if (CodeGenerator.isRestful) {
        cfg.getTemplate("controller-restful.ftl").process(data, new FileWriter(file));
      } else {
        cfg.getTemplate("controller.ftl").process(data, new FileWriter(file));
      }
      System.out.println(modelNameUpperCamel + "Controller.java가 성공적으로 생성되었습니다.");
    } catch (final Exception e) {
      throw new RuntimeException("컨트롤러 생성에 실패했습니다.", e);
    }
  }

  private static freemarker.template.Configuration getConfiguration() throws IOException {
    final freemarker.template.Configuration cfg =
        new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_23);
    cfg.setDirectoryForTemplateLoading(new File(CodeGenerator.TEMPLATE_FILE_PATH));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
    return cfg;
  }

  private static String tableNameConvertLowerCamel(final String tableName) {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName.toLowerCase());
  }

  private static String tableNameConvertUpperCamel(final String tableName) {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName.toLowerCase());
  }

  private static String tableNameConvertMappingPath(String tableName) {
    tableName = tableName.toLowerCase(); // 대문자 테이블 이름과 호환
    return "/" + (tableName.contains("_") ? tableName.replaceAll("_", "/") : tableName);
  }

  private static String modelNameConvertMappingPath(final String modelName) {
    final String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelName);
    return CodeGenerator.tableNameConvertMappingPath(tableName);
  }

  private static String packageConvertPath(final String packageName) {
    return String.format(
        "/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
  }
}
