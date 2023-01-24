package simplepingapplication.reporting;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonFormatterTest {

    @Test
    public void shouldFormatJsonWithCorrectFieldNames() {
        String expected = """
                {"host":"host","icmp_ping":null,"tcp_ping":null,"trace":null}
                """;
        String actual = JsonFormatter.formatJson("host", JobLastResponses.builder().build());
        Assertions.assertEquals(new JSONObject(expected).toMap(), new JSONObject(actual).toMap());
    }
}