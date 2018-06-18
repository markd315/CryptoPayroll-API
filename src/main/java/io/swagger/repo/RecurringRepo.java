package io.swagger.repo;

import io.swagger.model.RecurringOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringRepo extends MongoRepository<RecurringOrder, UUID> {
  List<RecurringOrder> findById(UUID id);
}