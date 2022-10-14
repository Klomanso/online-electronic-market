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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        public String getOrders(Model model) {
                List<Order> orders = orderService.findAllByOrderByOrderedAtDesc();
                model.addAttribute("orders", orders);
                return "order/orders";
        }

        @GetMapping("/order-add")
        public String addNewOrder() {
                basket.clear();
                return "order/order-add";
        }

        @GetMapping("/order-show-basket")
        public String showBasket(@RequestParam(name = "good-id") Long goodId,
                                 @RequestParam(name = "item-number") Integer itemNumber,
                                 Model model) {
                Optional<Good> foundGood = goodService.findById(goodId);
                if (foundGood.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("There is no good with id=" + goodId));
                        return "error/invalid-action";
                }
                Good good = foundGood.get();

                if (basket.containsKey(good)) {
                        basket.put(good, basket.get(good) + itemNumber);
                } else {
                        basket.put(good, itemNumber);
                }

                model.addAttribute("items", basket.keySet());
                model.addAttribute("basket", basket);
                return "order/order-show-basket";
        }

        @GetMapping("/item-add")
        public String addNewItemToBasket(Model model) {
                List<Good> goods = goodService.findAll();
                model.addAttribute("goods", goods);
                return "order/item-add";
        }

        @GetMapping("/order-checkout")
        public String selectUserAndDeliveryTerms(Model model) {
                if (basket.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("You should select at least one item"));
                        return "error/invalid-action";
                }
                model.addAttribute("basket", basket);
                List<User> users = userService.findAll();
                model.addAttribute("users", users);
                return "order/order-checkout";
        }

        @PostMapping("/order-save")
        public String orderSave(@RequestParam(name = "order-user-id") Long userId,
                                @Valid @RequestParam(name = "order-delivery-address") String deliveryAddress,
                                @RequestParam(name = "order-deliver-on", required = false)
                                @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                LocalDate deliverOn,
                                Model model) {
                LocalDateTime now = trimToMinutes(LocalDateTime.now());

                Optional<User> foundUser = userService.findById(userId);
                if (foundUser.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("There is no user with id="
                                + userId)
                                + ". Therefore, cannot add an order for her/him");
                        return "error/invalid-action";
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
                        return "redirect:/order-info?order-id=" + newOrder.getId();
                } else {
                        model.addAttribute("error", new ErrorMsg("Cannot save order info"));
                        return "error/invalid-action";
                }
        }

        @PostMapping("/order-update-status")
        public String orderUpdateStatus(@RequestParam(name = "order-id") Long id,
                                        Model model) {
                Optional<Order> foundOrder = orderService.findById(id);
                if (foundOrder.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("There is no order with id=" + id));
                        return "error/invalid-action";
                } else {
                        Order order = foundOrder.get();
                        if (order.getStatus().equals(statusService.processing())) {
                                order.setStatus(statusService.complete());
                        } else if (order.getStatus().equals(statusService.complete())) {
                                order.setStatus(statusService.delivered());
                        } else {
                                model.addAttribute("error", new ErrorMsg("Order is already delivered"));
                                return "error/invalid-action";
                        }
                        Optional<Order> updatedOrder = orderService.update(order);
                        if (updatedOrder.isPresent()) {
                                return "redirect:/order-info?order-id=" + updatedOrder.get().getId();
                        } else {
                                model.addAttribute("error", new ErrorMsg("Cannot update order status"));
                                return "error/invalid-action";
                        }
                }
        }

        @GetMapping("/order-info")
        public String getOrderInfo(@RequestParam(name = "order-id") Long id,
                                   Model model) {
                Optional<Order> foundOrder = orderService.findById(id);
                if (foundOrder.isPresent()) {
                        Order order = foundOrder.get();
                        model.addAttribute("order", order);
                        model.addAttribute("items", order.getGoodItems());

                        double totalPrice = 0.0;
                        List<OrderGood> items = order.getGoodItems();
                        for (OrderGood item : items) {
                                totalPrice += item.getQuantity() * item.getGood().getPrice();
                        }
                        model.addAttribute("total", totalPrice);

                        return "order/order-info";
                } else {
                        model.addAttribute("error", new ErrorMsg("There is no order with id=" + id));
                        return "error/invalid-action";
                }
        }
}
