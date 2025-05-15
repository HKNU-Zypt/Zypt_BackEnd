package zypt.zyptapiserver.livekit;

import java.io.IOException;

@FunctionalInterface
public interface LiveKitCallback<T> {
    T execute() throws IOException;
}
