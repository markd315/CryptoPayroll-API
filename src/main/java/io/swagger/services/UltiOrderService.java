package io.swagger.services;

import io.swagger.api.NotFoundException;
import io.swagger.model.OneTimeOrder;
import io.swagger.model.RecurringOrder;
import io.swagger.repo.OrderRepo;
import io.swagger.repo.RecurringRepo;
import java.rmi.UnexpectedException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UltiOrderService {
  private final OrderRepo orderRepository;
  private final RecurringRepo recurringRepository;


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
    try {
      RecurringOrder order = recurringRepository.findById(body.getId());
      if (order == null) {
        throw new NotFoundException(404, "No such order");
      }
    } catch (Exception e1) {
      recurringRepository.save(body);
    }
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
    if (orders == null || orders.size() == 0) {
      try {
        throw new NotFoundException(404, "No such order");
      } catch (NotFoundException e) {
        e.printStackTrace();
      }
    }
    orderRepository.delete(orders);
  }

  public void incrementOrResetAllRecurringOrders() {
    List<RecurringOrder> list = recurringRepository.findAll();
    for (RecurringOrder recurring : list) {
      recurring.setCyclesSinceLast(recurring.getCyclesSinceLast() + 1); //Increment our cyclical field.
      if (recurring.getCyclesSinceLast() == recurring.getCyclePeriod()) {
        recurring.setCyclesSinceLast(0); //reset the cyclical field
      }
      if (recurringRepository.findById(recurring.getOrder().getId()) != null) //Only save if we can find a version of this already
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
}
