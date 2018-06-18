package io.swagger.repo;

import io.swagger.model.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends MongoRepository<Order, UUID> {
  List<Order> findById(UUID id);
}