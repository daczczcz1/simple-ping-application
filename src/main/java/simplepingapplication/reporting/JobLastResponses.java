package simplepingapplication.reporting;

import lombok.Builder;

@Builder(toBuilder = true)
record JobLastResponses(String icmpResponse, String tcpResponse, String traceResponse) {
}
