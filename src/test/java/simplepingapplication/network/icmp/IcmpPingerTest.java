package simplepingapplication.network.icmp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IcmpPingerTest {
    Runtime mockRuntime = mock();
    Process mockProcess = mock();

    IcmpPinger pinger;

    @BeforeEach
    public void setup() throws IOException {
        reset(mockRuntime, mockProcess);
        when(mockRuntime.exec(anyString())).thenReturn(mockProcess);

        pinger = new IcmpPinger(mockRuntime);
    }

    @Test
    public void shouldReturnExitCodeAndOutput() throws IOException, InterruptedException {

        when(mockProcess.waitFor()).thenReturn(0);
        String pingResponse = "pingResponse";
        var commandOutput = new ByteArrayInputStream(pingResponse.getBytes());
        when(mockProcess.getInputStream()).thenReturn(commandOutput);
        IcmpPingerResult expected = new IcmpPingerResult(0, pingResponse);

        IcmpPingerResult result = pinger.ping("somehost");
        Assertions.assertEquals(expected, result);
    }
}