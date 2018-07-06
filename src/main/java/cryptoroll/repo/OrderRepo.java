package cryptoroll.repo;

import cryptoroll.model.OneTimeOrder;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends MongoRepository<OneTimeOrder, UUID> {
  OneTimeOrder findById(UUID id);
}