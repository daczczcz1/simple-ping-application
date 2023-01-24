package simplepingapplication.reporting;

import org.json.JSONObject;

import static org.json.JSONObject.NULL;

public class JsonFormatter {
    public static String formatJson(String host, JobLastResponses lastResponses) {
        JSONObject object = new JSONObject();
        object.put("host", host);
        object.put("icmp_ping", lastResponses.icmpResponse() != null ? lastResponses.icmpResponse() : NULL);
        object.put("tcp_ping", lastResponses.tcpResponse() != null ? lastResponses.tcpResponse() : NULL);
        object.put("trace", lastResponses.traceResponse() != null ? lastResponses.traceResponse() : NULL);
        return object.toString();
    }
}
