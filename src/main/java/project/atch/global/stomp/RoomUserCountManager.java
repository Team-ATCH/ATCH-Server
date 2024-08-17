package project.atch.global.stomp;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomUserCountManager {

    private final ConcurrentHashMap<Long, Integer> roomUserCountMap = new ConcurrentHashMap<>();

    // 사용자 수 증가
    public void incrementUserCount(Long roomId) {
        roomUserCountMap.merge(roomId, 1, Integer::sum);
    }

    // 사용자 수 감소
    public void decrementUserCount(Long roomId) {
        roomUserCountMap.computeIfPresent(roomId, (key, count) -> count > 1 ? count - 1 : null);
    }

    // 특정 roomId의 사용자 수 조회
    public int getUserCount(Long roomId) {
        return roomUserCountMap.getOrDefault(roomId, 0);
    }
}

