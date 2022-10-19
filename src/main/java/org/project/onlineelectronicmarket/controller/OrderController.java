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
import org.project.onlineelectronicmarket.service.impl.GoodServiceImpl;
import org.project.onlineelectronicmarket.service.impl.OrderServiceImpl;
import org.project.onlineelectronicmarket.service.impl.StatusServiceImpl;
import org.project.onlineelectronicmarket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class OrderController {

        private final GoodServiceImpl goodServiceImpl;

        private final OrderServiceImpl orderServiceImpl;

        private final UserServiceImpl userServiceImpl;

        private final StatusServiceImpl statusServiceImpl;

        private final HashMap<Good, Integer> cart = new HashMap<>();

        @Autowired
        public OrderController(GoodServiceImpl goodServiceImpl, OrderServiceImpl orderServiceImpl,
                               UserServiceImpl userServiceImpl, StatusServiceImpl statusServiceImpl) {
                this.goodServiceImpl = goodServiceImpl;
                this.orderServiceImpl = orderServiceImpl;
                this.userServiceImpl = userServiceImpl;
                this.statusServiceImpl = statusServiceImpl;
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
        public ModelAndView getGoods(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                     ModelAndView modelAndView) {

                modelAndView.addObject("orders", orderServiceImpl.getPage(pageNumber, size));
                modelAndView.setViewName("order/orders");
                return modelAndView;
        }

        @GetMapping("/order-add")
        public String addNewOrder() {
                cart.clear();
                return "order/order-add";
        }

        @GetMapping("/item-add")
        public ModelAndView addNewItemToCart(ModelAndView modelAndView) {
                List<Good> goods = goodServiceImpl.findAll();
                modelAndView.addObject("goods", goods);
                modelAndView.setViewName("order/item-add");
                return modelAndView;
        }

        @GetMapping("/order-show-cart")
        public ModelAndView showCart(@RequestParam(name = "good-id") Long goodId,
                                     @RequestParam(name = "item-number") Integer itemNumber, ModelAndView modelAndView) {

                Optional<Good> foundGood = goodServiceImpl.findById(goodId);

                if (foundGood.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + goodId));
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }

                Good good = foundGood.get();

                if (cart.containsKey(good)) {
                        cart.put(good, cart.get(good) + itemNumber);
                } else {
                        cart.put(good, itemNumber);
                }

                modelAndView.addObject("items", cart.keySet());
                modelAndView.addObject("cart", cart);
                modelAndView.setViewName("order/order-show-cart");

                return modelAndView;
        }

        @GetMapping("/order-checkout")
        public ModelAndView selectUserAndDeliveryTerms(ModelAndView modelAndView) {
                if (cart.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("You should select at least one item"));
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }
                modelAndView.addObject("cart", cart);
                List<User> users = userServiceImpl.findAll();
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

                Optional<User> foundUser = userServiceImpl.findById(userId);
                if (foundUser.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id="
                                + userId)
                                + ". Therefore, cannot add an order for her/him");
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }

                User user = foundUser.get();

                Order order = new Order(now, statusServiceImpl.processing(),
                        deliveryAddress, deliverOn, user);

                for (Entry<Good, Integer> pair : cart.entrySet()) {
                        Good good = pair.getKey();
                        Integer number = pair.getValue();
                        if (good.getQuantity() < number) {
                                number = good.getQuantity();
                                good.setQuantity(0);
                        } else {
                                good.setQuantity(good.getQuantity() - number);
                        }
                        goodServiceImpl.update(good);
                        OrderGood item = new OrderGood(order, good, number);
                        order.getGoodItems().add(item);
                }

                Order newOrder = orderServiceImpl.save(order);
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
                Optional<Order> foundOrder = orderServiceImpl.findById(id);

                if (foundOrder.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no order with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        Order order = foundOrder.get();
                        if (!orderServiceImpl.setOrderStatus(order)) {
                                modelAndView.addObject("error", new ErrorMsg("Order is already delivered"));
                                modelAndView.setViewName("error/invalid-action");
                                return modelAndView;
                        }

                        Optional<Order> updatedOrder = orderServiceImpl.update(order);

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
                Optional<Order> foundOrder = orderServiceImpl.findById(id);

                if (foundOrder.isPresent()) {
                        Order order = foundOrder.get();
                        modelAndView.addObject("order", order);
                        modelAndView.addObject("items", order.getGoodItems());

                        modelAndView.addObject("total", orderServiceImpl.getTotalPrice(order));
                        modelAndView.setViewName("order/order-info");

                } else {
                        modelAndView.addObject("error", new ErrorMsg("There is no order with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                }

                return modelAndView;
        }
}
