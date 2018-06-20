package io.swagger.services;

import io.swagger.api.NotFoundException;
import io.swagger.model.OneTimeOrder;
import io.swagger.model.RecurringOrder;
import io.swagger.repo.OrderRepo;
import io.swagger.repo.RecurringRepo;

import java.nio.file.InvalidPathException;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.stereotype.Service;

@Service
public class UltiOrderService {
  @Autowired
  private OrderRepo orderRepository;

  @Autowired
  private RecurringRepo recurringRepository;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public UltiOrderService(OrderRepo orderRepository, RecurringRepo recurringRepository) {
    this.orderRepository = orderRepository;
    this.recurringRepository = recurringRepository;
  }


  public OneTimeOrder findOneTimeOrderById(UUID id) throws NotFoundException {
    OneTimeOrder order = orderRepository.findById(id);
    if (order == null) {
      throw new NotFoundException(404, "No such order");
    }
    return order;
  }

  public RecurringOrder findRecurringOrderById(UUID id) throws NotFoundException {
    RecurringOrder order = recurringRepository.findById(id);
    if (order == null) {
      log.info(id.toString());
      throw new NotFoundException(404, "No such order");
    }
    return order;
  }

  public void addOneTimeOrder(OneTimeOrder body) {
    try {
      OneTimeOrder order = orderRepository.findById(body.getId());
      if (order == null) {
        throw new NotFoundException(404, "No such order");
      }
    } catch (Exception e1) {
      orderRepository.save(body);
    }
  }

  public void addRecurringOrder(RecurringOrder body) {
      RecurringOrder order = recurringRepository.findById(body.getId());
      if (order != null) {
        recurringRepository.delete(order.getId());
      }
      recurringRepository.save(body);
  }

  public void deleteRecurringOrder(UUID id) throws NotFoundException {
    if (recurringRepository.findById(id) == null) {
      throw new NotFoundException(404, "No such order");
    }
    recurringRepository.delete(id);
  }

  public RecurringOrder getRecurringOrder(UUID id) throws NotFoundException {
    RecurringOrder order = recurringRepository.findById(id);
    if (order == null) {
      throw new NotFoundException(400, "No such orders");
    }
    return order;
  }

  public void deleteRecurringOrder(UUID id) throws NotFoundException {
      if(recurringRepository.findById(id) == null) {
        throw new NotFoundException(404, "No such order");
      }
      recurringRepository.delete(id);
  }

  public RecurringOrder getRecurringOrder(UUID id) throws NotFoundException {
    RecurringOrder order = recurringRepository.findById(id);
    if (order == null) {
      throw new NotFoundException(400, "No such orders");
    }
    return order;
  }

  public List<OneTimeOrder> getAllOneTimeOrders() throws NotFoundException {
    List<OneTimeOrder> orders = orderRepository.findAll();
    if (orders == null) {
      try {
        throw new NotFoundException(404, "No such orders");
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    return orders;
  }

  public List<RecurringOrder> getAllRecurringOrders() throws NotFoundException {
    List<RecurringOrder> orders = recurringRepository.findAll();
    if (orders == null) {
      try {
        throw new NotFoundException(404, "No such orders");
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    return orders;
  }

  public void wipeAllOneTimeOrders() throws NotFoundException {
    List<OneTimeOrder> orders = orderRepository.findAll();
    orderRepository.delete(orders);
  }

  public void incrementOrResetAllRecurringOrders() {
    List<RecurringOrder> list = recurringRepository.findAll();
    for (RecurringOrder recurring : list) {
      recurring.setCyclesSinceLast(recurring.getCyclesSinceLast() + 1); //Increment our cyclical field.
      if (recurring.getCyclesSinceLast() == recurring.getCyclePeriod()) {
        recurring.setCyclesSinceLast(0); //reset the cyclical field
      }
      if (recurringRepository.findById(recurring.getId()) != null) //Only save if we can find a version of this already
      {
        recurringRepository.save(recurring);
      } else {
        try {
          throw new UnexpectedException("Tried to update entry that does not exist!");
        } catch (UnexpectedException e) {
          e.printStackTrace();
        }
      }
    }
  }
  public void updateRecurringOrder(RecurringOrder body, UUID target, String code) throws NotFoundException {
      RecurringOrder oldOrder = recurringRepository.findById(target);
      if (oldOrder == null) {
        throw new NotFoundException(404, "No Such Order");
      }
      if (body.getId().compareTo(target) != 0) {
        throw new NotFoundException(401, "Path does not match id field");
      }
      if (oldOrder.getCurrency() != body.getCurrency()) {
        throw new NotFoundException(401, "Currency type cannot be changed");
      }
      recurringRepository.save(body);
  }
}
