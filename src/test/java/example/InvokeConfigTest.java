package example;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.config.ConfigClient;
import software.amazon.awssdk.services.config.model.SelectResourceConfigRequest;
import software.amazon.awssdk.services.config.model.SelectResourceConfigResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class InvokeConfigTest {

    @Test
    void invokeTest() {
        ConfigClient configClient = mock(ConfigClient.class);
        TestContext testContext = new TestContext();
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        SelectResourceConfigResponse response = SelectResourceConfigResponse.builder().build();
        doReturn(response).when(configClient).selectResourceConfig(any(SelectResourceConfigRequest.class));
        Handler handler = new Handler(configClient);

        String result = handler.handleRequest(event, testContext);

        assertEquals("[]", result);
    }

}
