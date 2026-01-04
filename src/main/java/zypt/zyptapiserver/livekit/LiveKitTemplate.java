package zypt.zyptapiserver.livekit;

import zypt.zyptapiserver.exception.livekit.RetrofitExecuteException;

import java.io.IOException;

public class LiveKitTemplate {

    public static <T> T execute(LiveKitCallback<T> callback) {
        try {
            return callback.execute();
        } catch (IOException e) {
            throw new RetrofitExecuteException(e.getMessage(), e.getCause());
        }
    }
}
