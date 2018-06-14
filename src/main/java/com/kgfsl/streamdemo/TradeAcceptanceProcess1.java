package com.kgfsl.streamdemo;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TradeAcceptanceProcess1 {

	private final String filename = "E:\\Shanthini\\flight.txt";
	private List<Flight> allFlights;

	public void readGiveUpCSVFile() throws FileNotFoundException {

		String[] columns = new String[] { "id", "name", "departure", "arrival", "fare" };
		allFlights = (ParseCsvToBean.convert(filename, ',', Flight.class, columns));
		allFlights.forEach(System.out::println);

		System.out.println("allFlights.size() .... >" + allFlights.size());
		// uniqueTradeNo.size()); type 1

		Set<Flight> uniqueFlights = allFlights.stream()
				.collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Flight::getId))));

		// directly removing the elements from list if already existed in set
		// type 2
		// Set<String> uniqueFlights = new HashSet<>();
		// allFlights.removeIf(p -> !uniqueFlights.add(p.getId()));

		System.out.println("uniqueFlights.size() .... >" + uniqueFlights.size());

		// Departure wise Flights count
		Map<String, Long> departureCounting = uniqueFlights.stream()
				.collect(Collectors.groupingBy(Flight::getDeparture, Collectors.counting()));
		System.out.println(departureCounting);

		// Heathrow to Londan flights
		Predicate<Flight> depatrurePredicate = x -> "Heathrow".equals(x.getDeparture());
		Predicate<Flight> arrivalPredicate = x -> "London".equals(x.getArrival());
		System.out.println(uniqueFlights.stream().filter(depatrurePredicate.and(arrivalPredicate)).map(x -> x.getId())
				.collect(Collectors.toList()));

		// Airlines name wise grouping
		Map<String, Set<String>> result = uniqueFlights.stream()
				.collect(Collectors.groupingBy(Flight::getName, Collectors.mapping(Flight::getId, Collectors.toSet())));
		System.out.println(result);

		// RoundTrip flights
		System.out.println("++++++++++++++");
		Predicate<Flight> rountripPredicate = x -> (x.getDeparture().equals("Heathrow")
				&& (x.getArrival().equals("London"))
				|| (x.getDeparture().equals("London")) && (x.getArrival().equals("Heathrow")));

		System.out.println(allFlights.stream().filter(rountripPredicate)
				.collect(Collectors.groupingBy(Flight::getId, Collectors.toList())));

		// Minimum fare RoundTrip flights
		System.out.println("?????????????");
		System.out.println(
				allFlights.stream().filter(rountripPredicate).min(Comparator.comparing(Flight::getFare)).get());

		Map<String, Optional<Flight>> personByMaxAge = allFlights.stream().filter(rountripPredicate)
				.collect(Collectors.groupingBy(Flight::getId, Collectors.minBy(Comparator.comparing(Flight::getFare))));

		System.out.println("personByMaxAge ...>" + personByMaxAge.toString());

		// Group persons by their ID
		Map<String, List<Flight>> peopleById = allFlights.stream().collect(Collectors.groupingBy(Flight::getId));

		// Print out groups of people that share one ID
		peopleById.values().stream().filter(peopleWithSameId -> peopleWithSameId.size() > 1)
				.forEach(peopleWithSameId -> System.out.println("People with identical IDs: " + peopleWithSameId));

		List<List<Flight>> result4 = peopleById.values().stream()
				.filter(peopleWithSameId -> peopleWithSameId.size() > 1).collect(Collectors.toList());
		System.out.println("result4 *************");
		System.out.println(result4);

		Map<Object, Integer> roundtripFareTotal = allFlights.stream()
		.filter(rountripPredicate)
				.collect(Collectors.groupingBy(p -> p.getId(), Collectors.summingInt(p -> p.getFare())));
				
				System.out.println("************* roundtripFareTotal *************");
				System.out.println(roundtripFareTotal);
		
	}

}
