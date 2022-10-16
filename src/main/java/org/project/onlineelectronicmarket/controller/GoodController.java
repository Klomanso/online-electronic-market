package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.service.AppTypeService;
import org.project.onlineelectronicmarket.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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

        @GetMapping("/goods-found")
        public ModelAndView getGoodsByQuery(@RequestParam(name = "good-query") String query, ModelAndView modelAndView) {
                modelAndView.addObject("goods", goodService.findAllMatches(query));
                modelAndView.addObject("query", new ErrorMsg(query));
                modelAndView.setViewName("good/goods-found");
                return modelAndView;
        }

        @GetMapping("/good-info")
        public ModelAndView getGoodInfo(@RequestParam(name = "good-id") Long id, ModelAndView modelAndView) {

                Optional<Good> foundGood = goodService.findById(id);

                if(foundGood.isPresent()) {
                        modelAndView.addObject("good", foundGood.get());
                        modelAndView.setViewName("good/good-info");
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

        @GetMapping("/good-edit")
        public ModelAndView editGoodInfo(@RequestParam(name = "good-id", required = false) Long id, ModelAndView modelAndView) {
                List<AppType> appTypes = appTypeService.findAllByOrderByName();

                if (id == null) {
                        modelAndView.addObject("good", new Good());
                        modelAndView.addObject("types", appTypes);
                        modelAndView.setViewName("good/good-edit");
                } else {
                        Optional<Good> foundGood = goodService.findById(id);
                        if (foundGood.isEmpty()) {
                                modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                                modelAndView.setViewName("error/invalid-action");
                        } else {
                                Good good = foundGood.get();
                                modelAndView.addObject("good", good);
                                modelAndView.addObject("types", appTypes);
                                modelAndView.setViewName("good/good-edit");
                        }
                }
                return modelAndView;
        }
        /*
         * If given id is null then saves new good with given characteristics.
         * Otherwise, updates good info.
         */

        @PostMapping("/good-save")
        public ModelAndView saveGoodInfo(@RequestParam(name = "good-id", required = false) Long id,
                                   @Valid @RequestParam(name = "good-name") String name,
                                   @Valid @RequestParam(name = "good-price") Double price,
                                   @Valid @RequestParam(name = "good-company") String company,
                                   @Valid @RequestParam(name = "good-assembly-place") String assemblyPlace,
                                   @Valid @RequestParam(name = "good-quantity") Integer quantity,
                                   @Valid @RequestParam(name = "good-description", required = false) String description,
                                   @RequestParam(name = "good-type-id") Long appTypeId,
                                   ModelAndView modelAndView) {
                boolean successfullySaved;
                Good savedGood = null;

                Optional<AppType> foundAppType = appTypeService.findById(appTypeId);
                if (foundAppType.isEmpty()) {
                        modelAndView.addObject("error",
                                new ErrorMsg("Experienced some trouble. Cannot find app type with id="
                                        + id
                                        + ".\n Please, try again."));
                        modelAndView.setViewName("error/invalid-action");
                        return modelAndView;
                }
                AppType appType = foundAppType.get();

                if (id == null) {
                        Good good = new Good(appType, name,
                                price, company, assemblyPlace,
                                quantity, description);
                        Good newGood = goodService.save(good);
                        successfullySaved = (newGood.getId() != null);
                        if (successfullySaved) {
                                savedGood = newGood;
                        }
                } else {
                        Optional<Good> foundGood = goodService.findById(id);
                        if (foundGood.isEmpty()) {
                                modelAndView.addObject("error", new ErrorMsg("There is no good with id=" + id));
                                modelAndView.setViewName("error/invalid-action");
                                return modelAndView;
                        } else {
                                Good good = foundGood.get();
                                good.setAppType(appType);
                                good.setName(name);
                                good.setPrice(price);
                                good.setCompany(company);
                                good.setAssemblyPlace(assemblyPlace);
                                good.setQuantity(quantity);
                                good.setDescription(description);
                                Optional<Good> updatedGood = goodService.update(good);
                                successfullySaved = updatedGood.isPresent();
                                if (successfullySaved) {
                                        savedGood = updatedGood.get();
                                }
                        }
                }

                if (successfullySaved) {
                        modelAndView.setViewName("redirect:/good-info?good-id=" + savedGood.getId());
                } else {
                        modelAndView.addObject("error", new ErrorMsg("Cannot save good info"));
                        modelAndView.setViewName("error/invalid-action");
                }
                return modelAndView;
        }
        @PostMapping("/good-delete")
        public ModelAndView deleteGood(@RequestParam(name = "good-id") Long id, ModelAndView modelAndView) {
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

