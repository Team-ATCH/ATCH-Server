package project.atch.domain.chat.repository;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import project.atch.domain.chat.entity.Chat;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {

    @Autowired
    private MongoClient mongoClient;

    @Override
    public Flux<Chat> findOldestMessagesFromAllRooms(int limit, long lastId) {
        MongoDatabase database = mongoClient.getDatabase("atch");
        MongoCollection<Document> collection = database.getCollection("chat");

        List<Bson> pipeline = new ArrayList<>();
        if (lastId > 0L) {
            pipeline.add(Aggregates.match(Filters.lt("roomId", lastId))); // roomId가 targetRoomId보다 작은 채팅방만 선택
        }

        pipeline.add(Aggregates.sort(Sorts.ascending("createdAt"))); // createdAt 필드를 오름차순으로 정렬
        pipeline.add(Aggregates.group("$roomId",
                Accumulators.first("id", "$_id"),
                Accumulators.first("roomId", "$roomId"),
                Accumulators.first("content", "$content"),
                Accumulators.first("fromId", "$fromId"),
                Accumulators.first("createdAt", "$createdAt"),
                Accumulators.first("read", "$read")
        ));
        pipeline.add(Aggregates.sort(Sorts.orderBy(Sorts.descending("createdAt")))); // 그룹화 후 createdAt 필드를 내림차순으로 정렬
        pipeline.add(Aggregates.limit(limit));

        // Aggregation 실행 후 Flux로 변환하여 반환
        try {
            return Flux.from(collection.aggregate(pipeline))
                    .map(document -> {
                        ObjectId id = document.getObjectId("id");
                        Long roomId = document.getLong("roomId");
                        String content = document.getString("content");
                        Long fromId = document.getLong("fromId");
                        Date createdAt = document.getDate("createdAt");
                        Boolean read = document.getBoolean("read");

                        return new Chat(id, roomId, content, fromId, createdAt, read);
                    })
                    .subscribeOn(Schedulers.boundedElastic()); // 비동기 실행
        } catch (Exception e) {
            log.error(e.toString());
            return Flux.error(new CustomException(ErrorCode.QUERY_EXECUTION_FAILED));
        }
    }

}

