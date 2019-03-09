package com.course.ms.flightFare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FlightFareApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightFareApplication.class, args);
	}

}
