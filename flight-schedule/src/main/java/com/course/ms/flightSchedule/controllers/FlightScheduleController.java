package com.course.ms.flightSchedule.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.course.ms.flightSchedule.models.Flight;

@RestController
@RequestMapping(value="/api/v1/flights")
public class FlightScheduleController {
	
	@GetMapping(value="/from/{from}/to/{to}")
	public List<Flight> getFlights() {
		List<Flight> flightList = new ArrayList<>();
		
		Flight flight1 = new Flight(1L, "IN");
		Flight flight2 = new Flight(2L, "US");
		
		flightList.add(flight1);
		flightList.add(flight2);
		
		return flightList;
	}
}
