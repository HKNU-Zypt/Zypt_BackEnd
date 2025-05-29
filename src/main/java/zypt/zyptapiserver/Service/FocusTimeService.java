package zypt.zyptapiserver.Service;

import zypt.zyptapiserver.domain.dto.FocusTimeDto;

public interface FocusTimeService {

    void saveFocusTime(String id, FocusTimeDto focusTimeDto);

}
