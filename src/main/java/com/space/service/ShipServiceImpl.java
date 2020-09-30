package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exception.BadRequestException;
import com.space.exception.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.specification.ShipSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;
    @Autowired
    private void setShipRepository(ShipRepository shipRepository){
        this.shipRepository=shipRepository;
    }
    @Override
    public Ship getShip(Long id) {
        if (!shipRepository.existsById(id))
            throw new ShipNotFoundException("Ship not found");

        return shipRepository.findById(id).get();
    }
    public Page<Ship> getAll(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder order,
            Integer pageNumber,
            Integer pageSize){
        Specification spec = createSpec(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipRepository.findAll(spec, PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName())));

    }
    private Specification<Ship> createSpec(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating){
        ShipSpecificationBuilder builder = new ShipSpecificationBuilder();

        if(name != null){
            builder.with("name", ":", name);
        }

        if(planet != null){
            builder.with("planet", ":", planet);
        }

        if (shipType != null){
            builder.with("shipType", ":", shipType);
        }

        if (isUsed != null) {
            builder.with("isUsed", ":", isUsed);
        }

        if(minSpeed != null){
            builder.with("speed", ">", minSpeed);
        }

        if(maxSpeed != null){
            builder.with("speed", "<", maxSpeed);
        }

        if(minCrewSize != null) {
            builder.with("crewSize", ">", minCrewSize);
        }

        if(maxCrewSize != null) {
            builder.with("crewSize", "<", maxCrewSize);
        }

        if(minRating != null) {
            builder.with("rating", ">", minRating);
        }

        if(maxRating != null) {
            builder.with("rating", "<", maxRating);
        }

        if(after != null) {
            builder.with("prodDate", ">", new Date(after));
        }

        if(before != null) {
            builder.with("prodDate", "<", new Date(before));
        }

        Specification<Ship> spec = builder.build();
        return spec;
    }


    public Integer getCount(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating){
        Specification spec = createSpec(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipRepository.findAll(spec).size();
    }
    @Override
    public Ship editShip(Long id, Ship ship) {
        checkShipParams(ship);
        if(!existsById(id)) throw new ShipNotFoundException();
        Ship editedShip = shipRepository.findById(id).get();

        if (ship.getName() != null)
            editedShip.setName(ship.getName());

        if (ship.getPlanet() != null)
            editedShip.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            editedShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            editedShip.setProdDate(ship.getProdDate());

        if (ship.getSpeed() != null)
            editedShip.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null)
            editedShip.setUsed(ship.getUsed());

        if (ship.getCrewSize() != null)
            editedShip.setCrewSize(ship.getCrewSize());
        Double rating = calculateRating(editedShip);
        editedShip.setRating(rating);
        return shipRepository.save(editedShip);
    }

    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName) {
        return shipRepository.findAll(specification,sortedByName);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Ship createShip(Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null)
            throw new BadRequestException("One of Ship params is null");

        checkShipParams(ship);

        if (ship.getUsed() == null)
            ship.setUsed(false);

        Double rating = calculateRating(ship);
        ship.setRating(rating);

        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public void deleteById(Long id) {
        if(shipRepository.existsById(id))
            shipRepository.deleteById(id);
         else {
            throw new ShipNotFoundException("Ship not found");
        }

    }

    @Override
    public boolean existsById(long id) {
        return shipRepository.existsById(id);
    }



    private void checkShipParams(Ship ship) {

        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException("Incorrect Ship.name");

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException("Incorrect Ship.planet");

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException("Incorrect Ship.crewSize");

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            throw new BadRequestException("Incorrect Ship.speed");

        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
                throw new BadRequestException("Incorrect Ship.date");
        }
    }



    private Double calculateRating(Ship ship) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        int year = cal.get(Calendar.YEAR);

        BigDecimal rating = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }
}
