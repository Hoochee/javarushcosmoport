package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {
    Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName);
    List<Ship> getAllShips(Specification<Ship> specification);
    Ship createShip(Ship ship);
    Ship getShip(Long id);
    Ship editShip(Long id, Ship ship);
    void deleteById(Long id);
    boolean existsById(long id) ;

    Page<Ship> getAll(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize);

    Integer getCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating);
}
