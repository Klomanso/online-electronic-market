package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.AppTypeService;
import org.project.onlineelectronicmarket.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class GoodController {

        private final GoodService goodService;
        private final AppTypeService appTypeService;

        @Autowired
        public GoodController(GoodService goodService, AppTypeService appTypeService) {
                this.goodService = goodService;
                this.appTypeService = appTypeService;
        }

        @GetMapping("/goods")
        public ModelAndView getGoods(ModelAndView modelAndView) {
                List<Good> goods = goodService.findAllByOrderByName();
                modelAndView.addObject("goods", goods);
                modelAndView.setViewName("good/goods");
                return modelAndView;
        }

        @GetMapping("/goodsFound")
        public ModelAndView getGoodsByQuery(@RequestParam(name = "good-query") String query, ModelAndView modelAndView) {
                modelAndView.addObject("goods", goodService.findAllMatches(query));
                modelAndView.addObject("query", new ErrorMsg(query));
                modelAndView.setViewName("good/goodsFound");
                return modelAndView;
        }

        @GetMapping("/goodInfo/{id}")
        public ModelAndView getGoodInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {

                Optional<Good> foundGood = goodService.findById(id);

                if(foundGood.isPresent()) {
                        modelAndView.addObject("good", foundGood.get());
                        modelAndView.setViewName("good/goodInfo");
                } else {
                        modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                }

                return modelAndView;
        }
        /*
         * If given good id is null then adds new good.
         * Otherwise, returns good info (to let then update it).
         *
         */

        @GetMapping("/goodEdit/{id}")
        public ModelAndView editGoodInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {
                List<AppType> appTypes = appTypeService.findAllByOrderByName();

                if (id == null) {
                        modelAndView.addObject("good", new Good());
                        modelAndView.addObject("types", appTypes);
                        modelAndView.setViewName("good/goodEdit");
                } else {
                        Optional<Good> foundGood = goodService.findById(id);
                        if (foundGood.isEmpty()) {
                                modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                                modelAndView.setViewName("error/invalid-action");
                        } else {
                                Good good = foundGood.get();
                                modelAndView.addObject("good", good);
                                modelAndView.addObject("types", appTypes);
                                modelAndView.setViewName("good/goodEdit");
                        }
                }
                return modelAndView;
        }
        /*
         * If given id is null then saves new good with given characteristics.
         * Otherwise, updates good info.
         */

        @GetMapping("/good/add")
        public ModelAndView showAddGood(ModelAndView modelAndView) {
                List<AppType> appTypes = appTypeService.findAllByOrderByName();

                modelAndView.addObject("add", true);
                modelAndView.addObject("types", appTypes);
                modelAndView.addObject("good", new Good());
                modelAndView.setViewName("good/goodEdit");

                return modelAndView;
        }

        @PostMapping(value = "/good/add")
        public ModelAndView addGood(ModelAndView modelAndView, @ModelAttribute("good") Good good) {
                Good newGood = goodService.save(good);

                modelAndView.addObject("good", newGood);
                modelAndView.setViewName("redirect:/goodInfo/" + newGood.getId());

                return modelAndView;
        }

        @PostMapping("/good/save/{id}")
        public ModelAndView saveGoodInfo(@ModelAttribute("good") Good good, @PathVariable("id") Long id, ModelAndView modelAndView) {
                good.setId(id);
                goodService.update(good);
                modelAndView.setViewName("redirect:/goodInfo/" + good.getId());

                return modelAndView;
        }


        @PostMapping("/good/delete/{id}")
        public ModelAndView deleteGood(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<Good> foundGood = goodService.findById(id);
                if (foundGood.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        Good good = foundGood.get();
                        if (goodService.hasOrderEntries(good)) {
                                modelAndView.addObject("error",
                                        new ErrorMsg("Cannot delete item ["
                                                + good.getName()
                                                + "]. There are orders containing it."));
                                modelAndView.setViewName("error/invalid-action");
                        } else {
                                goodService.delete(foundGood.get());
                                modelAndView.setViewName("redirect:/goods");
                        }
                }
                return modelAndView;
        }
}

