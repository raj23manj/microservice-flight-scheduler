package com.course.ms.flightSchedule;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.course.ms.flightSchedule.dao.FlightScheduleRepository;
import com.course.ms.flightSchedule.models.Flight;

@Component
public class BootstrapRepository implements CommandLineRunner{
	
	
	private final FlightScheduleRepository flightRepository;	

	public BootstrapRepository(FlightScheduleRepository flightRepository) {
		super();
		this.flightRepository = flightRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		Flight flight1 = new Flight(1L, "UL-191", "DEL", "TYO", "Srilankan Airlines", "05:10", "07:35");
		Flight flight2 = new Flight(2L, "UL-191", "DEL", "TYO", "china southern Airlines", "05:15", "09:35");
		Flight flight3 = new Flight(3L, "UL-191", "DEL", "TYO", "china southern Airlines", "12:10", "14:35");
		Flight flight4 = new Flight(4L, "UL-191", "DEL", "TYO", "Srilankan Airlines", "15:10", "18:40");
		
		flightRepository.save(flight1);
		flightRepository.save(flight2);
		flightRepository.save(flight3);
		flightRepository.save(flight4);
		
	}

}
