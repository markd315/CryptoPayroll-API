package cryptoroll.repo;

import cryptoroll.model.RecurringOrder;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringRepo extends MongoRepository<RecurringOrder, UUID> {
  RecurringOrder findById(UUID id);
}