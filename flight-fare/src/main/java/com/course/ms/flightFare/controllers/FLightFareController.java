package com.course.ms.flightFare.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.course.ms.flightFare.dao.FlightFareRepository;
import com.course.ms.flightFare.feginClients.CurrencyConversionServiceProxy;
import com.course.ms.flightFare.models.CurrencyConversionVO;
import com.course.ms.flightFare.models.FlightFare;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@RestController
@RequestMapping(value="/api/v1/flight/{flightCode}/fare/{currency}")
public class FLightFareController {

	@Autowired
	private FlightFareRepository flightFareRepository;
	
	@Autowired
	private EurekaClient eurekaClient;
	
	@Value("${use.eureka.client:false}")
	private boolean useEurekaClient;
	
	@Value("${use.ribbon.backed.rest.template:false}")
	private boolean useRibbonBackedRestTemplate;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${use.feign.client:false}")
	private boolean useFeignClient;

	@Autowired
	private CurrencyConversionServiceProxy feignProxy;

	
	@Value("${base.currency:USD}")
	private String baseCurrency;
	
	@GetMapping
	public FlightFare getSingleTicketFare(@PathVariable String flightCode, @PathVariable String currency) {
		
		FlightFare fare = this.getFLightFare(flightCode);
		fare.setCurrency(currency);
		
		if(!baseCurrency.equals(currency)) {
			BigDecimal conversionRate = this.getConversion(currency);
			BigDecimal convertedFare = fare.getFlightFare().multiply(conversionRate);
			fare.setFlightFare(convertedFare);
		}
		return fare;
	}
	
	private FlightFare getFLightFare(String flightCode) {
		FlightFare flight = new FlightFare(null, flightCode, null);
		
		Example<FlightFare> fareFlight = Example.of(flight);
		FlightFare fare = flightFareRepository.findOne(fareFlight).get();
		return fare;
	}
	
	private BigDecimal getConversion(String toCurrency) {
		if(useEurekaClient) {
			Application app = eurekaClient.getApplication("currency-conversion");
			List<InstanceInfo> instances = app.getInstances();

			String serviceUri = String.format("%sapi/v1/from/{from}/to/{to}", instances.get(0).getHomePageUrl()); // http://localhost:7101/

			RestTemplate restTemplate = new RestTemplate();
			Map<String, String> urlPathVariables = new HashMap<>();
			urlPathVariables.put("from", baseCurrency);
			urlPathVariables.put("to", toCurrency);

			ResponseEntity<CurrencyConversionVO> responseEntity = restTemplate.getForEntity(serviceUri,
					CurrencyConversionVO.class, urlPathVariables);
			CurrencyConversionVO converter = responseEntity.getBody();
			return converter.getConversionRate();
		}else if (useRibbonBackedRestTemplate) {
			Map<String, String> urlPathVariables = new HashMap<>();
			urlPathVariables.put("from", baseCurrency);
			urlPathVariables.put("to", toCurrency);
			ResponseEntity<CurrencyConversionVO> responseEntity = restTemplate.getForEntity(
					"http://currency-conversion/api/v1/from/{from}/to/{to}", CurrencyConversionVO.class,
					urlPathVariables);
			CurrencyConversionVO converter = responseEntity.getBody();
			return converter.getConversionRate();
		} else if(useFeignClient) {
			CurrencyConversionVO converter = feignProxy.convertCurrency(baseCurrency, toCurrency);
			return converter.getConversionRate();
		} else {
			RestTemplate restTemplate = new RestTemplate();
			
			Map<String, String> urlPathVariables = new HashMap<>();
			urlPathVariables.put("from", baseCurrency);
			urlPathVariables.put("to", toCurrency);
			
			ResponseEntity<CurrencyConversionVO> responseEntity = restTemplate.getForEntity("http://localhost:7101/api/v1/from/{from}/to/{to}", 
								      CurrencyConversionVO.class,
								      urlPathVariables
								      );
			CurrencyConversionVO converter = responseEntity.getBody();
			return converter.getConversionRate();
		} 
	}
}
