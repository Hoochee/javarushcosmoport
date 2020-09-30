package com.space.controller;

import com.space.exception.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

@Controller
@RequestMapping(value = "/rest")
public class ShipController {

    private ShipService shipService;
    @Autowired
    private void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    @ResponseBody
    public ResponseEntity<?> getAll(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", defaultValue = "ID") ShipOrder order ,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize
    ){
        Page<Ship> mainTable = shipService.getAll(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
        return new ResponseEntity<>(mainTable.getContent(), HttpStatus.OK);
    }


    @GetMapping("/ships/count")
    @ResponseBody
    public ResponseEntity<?> getCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating){

        Integer count = shipService.getCount(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
   /* @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {


        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> specification = ShipSpecification.getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification, pageable).getContent();
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public int getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "planet", required = false) String planet,
                            @RequestParam(value = "shipType", required = false) ShipType shipType,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                            @RequestParam(value = "minRating", required = false) Double minRating,
                            @RequestParam(value = "maxRating", required = false) Double maxRating) {
        Specification<com.space.model.Ship> specification = ShipSpecification.getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification).size();
    }*/

    @GetMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String id1) {

        Long longId = checkId(id1);
        return new ResponseEntity<Ship>(shipService.getShip(longId), HttpStatus.OK);
    }
    @RequestMapping(value = "/ships/{id}", method = { RequestMethod.DELETE  })
    public void deleteShip(@PathVariable(value = "id") String id){
        Long checkedId = checkId(id);
        shipService.deleteById(checkedId);
        new ResponseEntity<Ship>(HttpStatus.OK);

    }
    @PostMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> editShip(@PathVariable(value = "id") String id, @RequestBody Ship ship) {

        Long longId = checkId(id);

        return new ResponseEntity<Ship>(shipService.editShip(longId, ship), HttpStatus.OK);

    }

    @PostMapping(value = "/ships")
    @ResponseBody
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        return new ResponseEntity<Ship>(shipService.createShip(ship), HttpStatus.OK);
    }


   //@DeleteMapping (value = "/ships/{id}")

    public Long checkId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException("Uncorrect ID");
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID not digit", e);
        }
    }

}
