package org.project.onlineelectronicmarket.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.project.onlineelectronicmarket.model.AppType;
import org.project.onlineelectronicmarket.model.Good;
import org.project.onlineelectronicmarket.service.AppTypeService;
import org.project.onlineelectronicmarket.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

/*
        @GetMapping("/goods")
        public String getGoods(Model model) {
                List<Good> goods = goodService.findAllByOrderByName();
                model.addAttribute("goods", goods);
                return "good/goods";
        }
*/

        @GetMapping("/goods")
        public ModelAndView getGoods(ModelAndView modelAndView) {
                List<Good> goods = goodService.findAllByOrderByName();
                modelAndView.addObject("goods", goods);
                modelAndView.setViewName("good/goods");
                return modelAndView;
        }

        /*
                @GetMapping("/goods-found")
                public String getGoodsByQuery(@RequestParam(name = "good-query") String query,
                                              Model model) {
                        List<Good> foundByName = goodService.findByNameContaining(query);
                        List<Good> foundByCompany = goodService.findByCompanyContaining(query);
                        List<Good> foundByAssemblyPlace = goodService.findByAssemblyPlaceContaining(query);
                        List<Good> foundByDescription = goodService.findByDescriptionContaining(query);

                        Set<Good> foundGoods = Stream.of(
                                        foundByName,
                                        foundByCompany,
                                        foundByAssemblyPlace,
                                        foundByDescription)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toSet());

                        model.addAttribute("goods", foundGoods);
                        model.addAttribute("query", new ErrorMsg(query));
                        return "good/goods-found";
                }
        */
        @GetMapping("/goods-found")
        public ModelAndView getGoodsByQuery(@RequestParam(name = "good-query") String query, ModelAndView modelAndView) {
                modelAndView.addObject("goods", goodService.findAllMatches(query));
                modelAndView.addObject("query", new ErrorMsg(query));
                modelAndView.setViewName("good/goods-found");
                return modelAndView;
        }

/*
        @GetMapping("/good-info")
        public String getGoodInfo(@RequestParam(name = "good-id") Long id,
                                  Model model) {
                Optional<Good> foundGood = goodService.findById(id);
                if (foundGood.isPresent()) {
                        Good good = foundGood.get();
                        model.addAttribute("good", good);
                        return "good/good-info";
                } else {
                        model.addAttribute("error", new ErrorMsg("There is no good with id=" + id));
                        return "error/invalid-action";
                }
        }
*/

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
        public String editGoodInfo(@RequestParam(name = "good-id", required = false) Long id,
                                   Model model) {
                List<AppType> appTypes = appTypeService.findAllByOrderByName();
                if (id == null) {
                        model.addAttribute("good", new Good());
                        model.addAttribute("types", appTypes);
                        return "good/good-edit";
                } else {
                        Optional<Good> foundGood = goodService.findById(id);
                        if (foundGood.isEmpty()) {
                                model.addAttribute("error", new ErrorMsg("There is no good with id=" + id));
                                return "error/invalid-action";
                        } else {
                                Good good = foundGood.get();
                                model.addAttribute("good", good);
                                model.addAttribute("types", appTypes);
                                return "good/good-edit";
                        }
                }
        }

        /*
         * If given id is null then saves new good with given characteristics.
         * Otherwise, updates good info.
         */
        @PostMapping("/good-save")
        public String saveGoodInfo(@RequestParam(name = "good-id", required = false) Long id,
                                   @RequestParam(name = "good-name") String name,
                                   @RequestParam(name = "good-price") Double price,
                                   @RequestParam(name = "good-company") String company,
                                   @RequestParam(name = "good-assembly-place") String assemblyPlace,
                                   @RequestParam(name = "good-quantity") Integer quantity,
                                   @RequestParam(name = "good-description", required = false) String description,
                                   @RequestParam(name = "good-type-id") Long appTypeId,
                                   Model model) {
                boolean successfullySaved;
                Good savedGood = null;

                Optional<AppType> foundAppType = appTypeService.findById(appTypeId);
                if (foundAppType.isEmpty()) {
                        model.addAttribute("error",
                                new ErrorMsg("Experienced some trouble. Cannot find app type with id="
                                        + id
                                        + ".\n Please, try again."));
                        return "error/invalid-action";
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
                                model.addAttribute("error", new ErrorMsg("There is no good with id=" + id));
                                return "error/invalid-action";
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
                        return "redirect:/good-info?good-id=" + savedGood.getId();
                } else {
                        model.addAttribute("error", new ErrorMsg("Cannot save good info"));
                        return "error/invalid-action";
                }
        }

        @PostMapping("/good-delete")
        public String deleteGood(@RequestParam(name = "good-id") Long id,
                                 Model model) {
                Optional<Good> foundGood = goodService.findById(id);
                if (foundGood.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("There is no good with id=" + id));
                        return "error/invalid-action";
                } else {
                        Good good = foundGood.get();
                        if (goodService.hasOrderEntries(good)) {
                                model.addAttribute("error",
                                        new ErrorMsg("Cannot delete item ["
                                                + good.getName()
                                                + "]. There are orders containing it."));
                                return "error/invalid-action";
                        } else {
                                goodService.delete(foundGood.get());
                                return "redirect:/goods";
                        }
                }
        }
}

