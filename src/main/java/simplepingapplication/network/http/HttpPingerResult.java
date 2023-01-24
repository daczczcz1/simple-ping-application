package simplepingapplication.network.http;

public record HttpPingerResult(Integer status, long responseTimeMillis, String url){}
