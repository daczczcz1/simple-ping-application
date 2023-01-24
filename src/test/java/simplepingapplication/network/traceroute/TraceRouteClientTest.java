package simplepingapplication.network.traceroute;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TraceRouteClientTest {
    Runtime mockRuntime = mock();
    Process mockProcess = mock();

    TraceRouteClient client;

    @BeforeEach
    public void setup() throws IOException {
        reset(mockRuntime, mockProcess);
        when(mockRuntime.exec(anyString())).thenReturn(mockProcess);

        client = new TraceRouteClient(mockRuntime);
    }

    @Test
    public void shouldReturnTraceRouteOutput() throws IOException, InterruptedException {

        when(mockProcess.waitFor()).thenReturn(0);
        String traceRouteResponse = "traceRouteResponse";
        var commandOutput = new ByteArrayInputStream(traceRouteResponse.getBytes());
        when(mockProcess.getInputStream()).thenReturn(commandOutput);

        TraceRouteResult result = client.traceRouteTo("somehost");
        Assertions.assertEquals(traceRouteResponse, result.output());
    }
}