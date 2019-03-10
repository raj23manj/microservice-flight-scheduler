package com.course.ms.flightSchedule.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.course.ms.flightSchedule.dao.FlightScheduleRepository;
import com.course.ms.flightSchedule.models.Flight;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service("flightScheduleService")
public class FlightScheduleServiceImpl implements FlightScheduleService{
	
	@Value("${airline.disabled}")
	private String airlineDisabled;
	
	@Autowired
	private FlightScheduleRepository flightRepository;
    
	@HystrixCommand(commandKey="getFlightsKey", fallbackMethod="buildFallbackFlights")
	@Override
	public List<Flight> getFLights(String from, String to) {
		Flight filterFlight = new Flight();
		filterFlight.setFlightFrom(from);
		filterFlight.setFlightTo(to);
		// https://stackoverflow.com/questions/27626825/spring-data-jpa-query-by-example
		sleepRandomly();
		Example<Flight> flightFilterExample = Example.of(filterFlight);
		List<Flight> flightList = flightRepository.findAll(flightFilterExample);
		
		if(!CollectionUtils.isEmpty(flightList)) {
			flightList = flightList.stream().filter(flight -> !airlineDisabled.equals(flight.getAirline()))
							   .collect(Collectors.toList());
		}
		
		return flightList;
	}
	
	public List<Flight> buildFallbackFlights(String from, String to) {
		Flight flight = new Flight();
		flight.setId(0L);
		flight.setFlightFrom(from);
		flight.setFlightTo(to);
		flight.setFlightCode("Sorry!!! No flight available this time");
		flight.setAirline("N/A");
		flight.setDepartureTime("N/A");
		flight.setArrivalTime("N/A");
		
		List<Flight> flightList = new ArrayList<>();
		flightList.add(flight);
		return flightList;
	}
	
	public void sleepRandomly() {
		Random rand = new Random();
		int randomNum = rand.nextInt(3) + 1;
		if(randomNum == 3) {
			System.out.println("It is a chance for demonstrating Hystrix action");
			try {
				System.out.println("Start sleeping...." + System.currentTimeMillis());
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Hystrix thread interupted...." + System.currentTimeMillis());
				e.printStackTrace();
			}
		}
	}

}
