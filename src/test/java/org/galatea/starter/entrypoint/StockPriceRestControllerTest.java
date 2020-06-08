package org.galatea.starter.entrypoint;

import static org.galatea.starter.MvcConfig.APPLICATION_EXCEL;
import static org.galatea.starter.MvcConfig.TEXT_CSV;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.service.StockPriceService;
import org.galatea.starter.utils.http.converter.SettlementMissionCsvConverter;
import org.galatea.starter.utils.http.converter.SettlementMissionXlsxConverter;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;

@Slf4j
// is this necessary (had to add to SettlementRestControllerTest)
@SpringBootTest
// for running parameterized tests (run same test multiple times with different sets of parameters)
@RunWith(JUnitParamsRunner.class)
public class StockPriceControllerTest extends ASpringTest {

  @Autowired
  private ITranslator<JsonNode, List<StockPrice>> timeSeriesJsonTranslator;

  @MockBean
  private StockPriceService mockStockPriceService;

  @Autowired
  private StockPriceController settlementRestController;

  private ObjectMapper objectMapper;

  private JacksonTester<TradeAgreementMessages> agreementJsonTester;

  private JacksonTester<List<Long>> missionIdJsonTester;

  private static final Long MISSION_ID_1 = 1091L;

  @Before
  public void setup() {
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);

    Map<String, MediaType> mediaTypes = new HashMap<>();
    mediaTypes.put("json", MediaType.APPLICATION_JSON);
    mediaTypes.put("xml", MediaType.APPLICATION_XML);
    mediaTypes.put("csv", TEXT_CSV);
    mediaTypes.put("xlsx", APPLICATION_EXCEL);

    ParameterContentNegotiationStrategy parameterContentNegotiationStrategy =
        new ParameterContentNegotiationStrategy(mediaTypes);

    ContentNegotiationManager manager =
        new ContentNegotiationManager(parameterContentNegotiationStrategy);

    RestAssuredMockMvc.standaloneSetup(
        MockMvcBuilders.standaloneSetup(settlementRestController).
            addPlaceholderValue("mvc.settleMissionPath", settleMissionPath).
            addPlaceholderValue("mvc.deleteMissionPath", deleteMissionPath).
            addPlaceholderValue("mvc.updateMissionPath", updateMissionPath).
            addPlaceholderValue("mvc.getMissionsPath", getMissionsPath).
            addPlaceholderValue("mvc.getMissionPath", getMissionPath).
            setContentNegotiationManager(manager).
            setMessageConverters(new MappingJackson2HttpMessageConverter(),
                new Jaxb2RootElementHttpMessageConverter(),
                new SettlementMissionCsvConverter(),
                new SettlementMissionXlsxConverter()).
            setControllerAdvice(new RestExceptionHandler()));
  }
}
