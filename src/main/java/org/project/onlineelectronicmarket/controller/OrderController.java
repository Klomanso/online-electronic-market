package org.project.onlineelectronicmarket.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.OrderGood;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.GoodService;
import org.project.onlineelectronicmarket.service.OrderService;
import org.project.onlineelectronicmarket.service.StatusService;
import org.project.onlineelectronicmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class OrderController {

        private final GoodService goodService;

        private final OrderService orderService;

        private final UserService userService;

        private final StatusService statusService;

        private final HashMap<Good, Integer> basket = new HashMap<>();

        @Autowired
        public OrderController(GoodService goodService, OrderService orderService,
                               UserService userService, StatusService statusService) {
                this.goodService = goodService;
                this.orderService = orderService;
                this.userService = userService;
                this.statusService = statusService;
        }

        private LocalDateTime trimToMinutes(LocalDateTime value) {
                return LocalDateTime.of(
                        value.getYear(),
                        value.getMonth(),
                        value.getDayOfMonth(),
                        value.getHour(),
                        value.getMinute());
        }

        @GetMapping("/orders")
        public ModelAndView getOrders(ModelAndView modelAndView) {
                List<Order> orders = orderService.findAllByOrderByOrderedAtDesc();
                modelAndView.addObject("orders", orders);
                modelAndView.setViewName("order/orders");
                return modelAndView;
        }

        @GetMapping("/order-add")
        public String addNewOrder() {
                basket.clear();
                return "order/order-add";
        }

        @GetMapping("/order-show-basket")
        public ModelAndView showBasket(@RequestParam(name = "good-id") Long goodId,
                                 @RequestParam(name = "item-number") Integer itemNumber, ModelAndView modelAndView) {

                Optional<Good> foundGood = goodService.findById(goodId);

                if (foundGood.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + goodId));
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }

                Good good = foundGood.get();

                if (basket.containsKey(good)) {
                        basket.put(good, basket.get(good) + itemNumber);
                } else {
                        basket.put(good, itemNumber);
                }

                modelAndView.addObject("items", basket.keySet());
                modelAndView.addObject("basket", basket);
                modelAndView.setViewName("order/order-show-basket");

                return modelAndView;
        }

        @GetMapping("/item-add")
        public ModelAndView addNewItemToBasket(ModelAndView modelAndView) {
                List<Good> goods = goodService.findAll();
                modelAndView.addObject("goods", goods);
                modelAndView.setViewName("order/item-add");
                return modelAndView;
        }

        @GetMapping("/order-checkout")
        public ModelAndView selectUserAndDeliveryTerms(ModelAndView modelAndView) {
                if (basket.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("You should select at least one item"));
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }
                modelAndView.addObject("basket", basket);
                List<User> users = userService.findAll();
                modelAndView.addObject("users", users);
                modelAndView.setViewName("order/order-checkout");

                return modelAndView;
        }

        @PostMapping("/order-save")
        public ModelAndView orderSave(@RequestParam(name = "order-user-id") Long userId,
                                @Valid @RequestParam(name = "order-delivery-address") String deliveryAddress,
                                @RequestParam(name = "order-deliver-on", required = false)
                                @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate deliverOn,
                                ModelAndView modelAndView) {

                LocalDateTime now = trimToMinutes(LocalDateTime.now());

                Optional<User> foundUser = userService.findById(userId);
                if (foundUser.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id="
                                + userId)
                                + ". Therefore, cannot add an order for her/him");
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }

                User user = foundUser.get();

                Order order = new Order(now, statusService.processing(),
                        deliveryAddress, deliverOn, user);

                for (Entry<Good, Integer> pair : basket.entrySet()) {
                        Good good = pair.getKey();
                        Integer number = pair.getValue();
                        if (good.getQuantity() < number) {
                                number = good.getQuantity();
                                good.setQuantity(0);
                        } else {
                                good.setQuantity(good.getQuantity() - number);
                        }
                        goodService.update(good);
                        OrderGood item = new OrderGood(order, good, number);
                        order.getGoodItems().add(item);
                }

                Order newOrder = orderService.save(order);
                if (newOrder.getId() != null) {
                        modelAndView.setViewName("redirect:/order-info?order-id=" + newOrder.getId());
                } else {
                        modelAndView.addObject("error", new ErrorMsg("Cannot save order info"));
                        modelAndView.setViewName("error/invalid-action");
                }

                return modelAndView;
        }

        @PostMapping("/order-update-status")
        public ModelAndView orderUpdateStatus(@RequestParam(name = "order-id") Long id, ModelAndView modelAndView) {
                Optional<Order> foundOrder = orderService.findById(id);

                if (foundOrder.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no order with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        Order order = foundOrder.get();
                        if (order.getStatus().equals(statusService.processing())) {
                                order.setStatus(statusService.complete());
                        } else if (order.getStatus().equals(statusService.complete())) {
                                order.setStatus(statusService.delivered());
                        } else {
                                modelAndView.addObject("error", new ErrorMsg("Order is already delivered"));
                                modelAndView.setViewName("error/invalid-action");
                                return modelAndView;
                        }

                        Optional<Order> updatedOrder = orderService.update(order);

                        if (updatedOrder.isPresent()) {
                                modelAndView.setViewName("redirect:/order-info?order-id=" + updatedOrder.get().getId());
                        } else {
                                modelAndView.addObject("error", new ErrorMsg("Cannot update order status"));
                                modelAndView.setViewName("error/invalid-action");
                        }
                }
                return modelAndView;
        }

        @GetMapping("/order-info")
        public ModelAndView getOrderInfo(@RequestParam(name = "order-id") Long id, ModelAndView modelAndView) {
                Optional<Order> foundOrder = orderService.findById(id);

                if (foundOrder.isPresent()) {
                        Order order = foundOrder.get();
                        modelAndView.addObject("order", order);
                        modelAndView.addObject("items", order.getGoodItems());

                        double totalPrice = 0.0;
                        List<OrderGood> items = order.getGoodItems();
                        for (OrderGood item : items) {
                                totalPrice += item.getQuantity() * item.getGood().getPrice();
                        }
                        modelAndView.addObject("total", totalPrice);
                        modelAndView.setViewName("order/order-info");

                } else {
                        modelAndView.addObject("error", new ErrorMsg("There is no order with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                }

                return modelAndView;
        }
}
