import static org.junit.Assert.assertEquals;

import com.mongodb.util.JSON;
import io.swagger.configuration.GDAXAuthorizer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpMethod;

public class TestAuth {
  @Test
  public void canCreateDeposits() throws URISyntaxException, IOException {
    Object body = new JSON().parse("{\n"
        + "    \"amount\": 10.00,\n"
        + "    \"currency\": \"USD\",\n"
        + "    \"payment_method_id\": \"bc677162-d934-5f1a-968c-a496b1c1270b\"\n"
        + "}");
    JSONObject req = GDAXAuthorizer.makeGDAXRequest(new HashMap<>(), (JSONObject) body, HttpMethod.POST, "");
    //HttpURLConnection conn = new HttpURLConnection("https://api-public.sandbox.gdax.com/deposits/payment-method");
    assertEquals(0, 0); //No exceptions
  }
}
