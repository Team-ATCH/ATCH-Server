package project.atch.domain.chat.repository;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import project.atch.domain.chat.entity.Chat;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Repository
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {

    @Autowired
    private MongoClient mongoClient;

    @Override
    public Flux<Chat> findOldestMessagesFromAllRooms() {
        MongoDatabase database = mongoClient.getDatabase("atch");
        MongoCollection<Document> collection = database.getCollection("chat");

        return Flux.from(collection.aggregate(Arrays.asList(
                Aggregates.sort(Sorts.ascending("timestamp")),
                Aggregates.group("$roomId",
                        Accumulators.first("id", "$_id"),
                        Accumulators.first("roomId", "$roomId"),
                        Accumulators.first("content", "$content"),
                        Accumulators.first("fromId", "$fromId"),
                        Accumulators.first("timestamp", "$timestamp"))
        ))).map(document -> new Chat(
                document.getObjectId("id"),
                document.getLong("roomId"),
                document.getString("content"),
                document.getLong("fromId")
        ));
    }
}

