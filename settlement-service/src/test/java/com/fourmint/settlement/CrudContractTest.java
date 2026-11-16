package com.fourmint.settlement;
import com.fourmint.platform.spring.AbstractCrudContract;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(classes = SettlementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.datasource.url=jdbc:h2:mem:settlement-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "spring.datasource.username=sa", "spring.datasource.password="})
@ActiveProfiles("demo")
class CrudContractTest extends AbstractCrudContract {
    protected String domain() { return "settlement"; }
}
