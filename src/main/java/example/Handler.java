package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.config.ConfigClient;
import software.amazon.awssdk.services.config.model.SelectResourceConfigRequest;
import software.amazon.awssdk.services.config.model.SelectResourceConfigResponse;

import java.util.ArrayList;
import java.util.List;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, String> {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ConfigClient configClient;

    public Handler() {
        try {
            configClient = ConfigClient.builder()
                    .build();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public Handler(ConfigClient configClient) {
        this.configClient = configClient;
    }

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        logger.info("ENVIRONMENT VARIABLES: {}", gson.toJson(System.getenv()));
        logger.info("CONTEXT: {}", gson.toJson(context));
        logger.info("EVENT: {}", gson.toJson(event));
        return getAllResourcesFromConfig();
    }

    String getAllResourcesFromConfig() {
        List<Object> results = new ArrayList<>();
        SelectResourceConfigRequest request = SelectResourceConfigRequest.builder()
                .expression("SELECT *")
                .build();
        String nextToken = "";
        do {
            SelectResourceConfigResponse selectResourceConfigResponse = configClient.selectResourceConfig(request);
            for (String result : selectResourceConfigResponse.results()) {
                Object o = gson.fromJson(result, Object.class);
                results.add(o);
            }
            nextToken = selectResourceConfigResponse.nextToken();
            request = SelectResourceConfigRequest.builder()
                    .expression("SELECT *")
                    .nextToken(nextToken)
                    .build();

        } while (nextToken != null);
        return gson.toJson(results);
    }
}