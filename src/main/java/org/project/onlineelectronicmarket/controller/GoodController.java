package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.service.impl.AppTypeServiceImpl;
import org.project.onlineelectronicmarket.service.impl.GoodServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class GoodController {

        private final GoodServiceImpl goodServiceImpl;
        private final AppTypeServiceImpl appTypeServiceImpl;

        @Autowired
        public GoodController(GoodServiceImpl goodServiceImpl, AppTypeServiceImpl appTypeServiceImpl) {
                this.goodServiceImpl = goodServiceImpl;
                this.appTypeServiceImpl = appTypeServiceImpl;
        }

        @GetMapping("/goods")
        public ModelAndView getGoods(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                     ModelAndView modelAndView) {

                modelAndView.addObject("goods", goodServiceImpl.getPage(pageNumber, size));
                modelAndView.setViewName("good/goods");
                return modelAndView;
        }

        @GetMapping("/goodsFound")
        public ModelAndView getGoodsByQuery(@RequestParam(name = "good-query") String query, ModelAndView modelAndView) {
                modelAndView.addObject("goods", goodServiceImpl.findAllMatches(query));
                modelAndView.addObject("query", new ErrorMsg(query));
                modelAndView.setViewName("good/goodsFound");
                return modelAndView;
        }

        @GetMapping("/goodInfo/{id}")
        public ModelAndView getGoodInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {

                Optional<Good> foundGood = goodServiceImpl.findById(id);

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
                List<AppType> appTypes = appTypeServiceImpl.findAllByOrderByName();

                if (id == null) {
                        modelAndView.addObject("good", new Good());
                        modelAndView.addObject("types", appTypes);
                        modelAndView.setViewName("good/goodEdit");
                } else {
                        Optional<Good> foundGood = goodServiceImpl.findById(id);
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
                List<AppType> appTypes = appTypeServiceImpl.findAllByOrderByName();

                modelAndView.addObject("add", true);
                modelAndView.addObject("types", appTypes);
                modelAndView.addObject("good", new Good());
                modelAndView.setViewName("good/goodEdit");

                return modelAndView;
        }

        @PostMapping(value = "/good/add")
        public ModelAndView addGood(ModelAndView modelAndView, @Valid @ModelAttribute("good") Good good) {
                Good newGood = goodServiceImpl.save(good);

                modelAndView.addObject("good", newGood);
                modelAndView.setViewName("redirect:/goodInfo/" + newGood.getId());

                return modelAndView;
        }

        @PostMapping("/good/save/{id}")
        public ModelAndView saveGoodInfo(@Valid @ModelAttribute("good") Good good, @PathVariable("id") Long id, ModelAndView modelAndView) {
                good.setId(id);
                goodServiceImpl.update(good);
                modelAndView.setViewName("redirect:/goodInfo/" + good.getId());

                return modelAndView;
        }


        @PostMapping("/good/delete/{id}")
        public ModelAndView deleteGood(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<Good> foundGood = goodServiceImpl.findById(id);
                if (foundGood.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        Good good = foundGood.get();
                        if (goodServiceImpl.hasOrderEntries(good)) {
                                modelAndView.addObject("error",
                                        new ErrorMsg("Cannot delete item ["
                                                + good.getName()
                                                + "]. There are orders containing it."));
                                modelAndView.setViewName("error/invalid-action");
                        } else {
                                goodServiceImpl.delete(foundGood.get());
                                modelAndView.setViewName("redirect:/goods");
                        }
                }
                return modelAndView;
        }
}

