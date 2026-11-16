package com.fourmint.workflow;
import com.fourmint.platform.spring.AbstractCrudContract;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(classes = WorkflowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.datasource.url=jdbc:h2:mem:workflow-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "spring.datasource.username=sa", "spring.datasource.password="})
@ActiveProfiles("demo")
class CrudContractTest extends AbstractCrudContract {
    protected String domain() { return "workflow"; }
}
