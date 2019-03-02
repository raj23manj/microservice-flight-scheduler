package com.course.ms.flightSchedule.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.course.ms.flightSchedule.dao.FlightScheduleRepository;
import com.course.ms.flightSchedule.models.Flight;

@Service("flightScheduleService")
public class FlightScheduleServiceImpl implements FlightScheduleService{
	
	@Value("${airline.disabled}")
	private String airlineDisabled;
	
	@Autowired
	private FlightScheduleRepository flightRepository;

	@Override
	public List<Flight> getFLights(String from, String to) {
		Flight filterFlight = new Flight();
		filterFlight.setFlightFrom(from);
		filterFlight.setFlightTo(to);
		// https://stackoverflow.com/questions/27626825/spring-data-jpa-query-by-example
		Example<Flight> flightFilterExample = Example.of(filterFlight);
		List<Flight> flightList = flightRepository.findAll(flightFilterExample);
		
		if(!CollectionUtils.isEmpty(flightList)) {
			flightList = flightList.stream().filter(flight -> !airlineDisabled.equals(flight.getAirline()))
							   .collect(Collectors.toList());
		}
		
		return flightList;
	}

}
