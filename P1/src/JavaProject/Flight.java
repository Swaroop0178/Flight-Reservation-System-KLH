package JavaProject;

import java.io.BufferedWriter;
import java.io.IOException;

public class Flight {
    private String flightNo;
    private String source;
    private String destination;
    private double baseFare;

    public static String airlineName = "Indian Airlines";
    public static double fuelSurchargeRate = 0.08;
    public static double mealCharge = 400;
    public static int flightCount = 0;

    public Flight() {
        flightCount++;
    }

    public Flight(String flightNo, String source, String destination)
            throws InvalidRecordException {
        this();
        setFlightNo(flightNo);
        setSource(source);
        setDestination(destination);
    }

    public Flight(String flightNo, String source, String destination, double baseFare)
            throws InvalidFareException, InvalidRecordException {
        this(flightNo, source, destination);
        setBaseFare(baseFare);
    }

    public String getFlightNo() {
        return flightNo;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setFlightNo(String flightNo) throws InvalidRecordException {
        if (flightNo == null || flightNo.trim().isEmpty()) {
            throw new InvalidRecordException("Flight number cannot be empty");
        }
        this.flightNo = flightNo.trim();
    }

    public void setSource(String source) throws InvalidRecordException {
        if (source == null || source.trim().isEmpty()) {
            throw new InvalidRecordException("Source cannot be empty");
        }
        this.source = source.trim();
    }

    public void setDestination(String destination) throws InvalidRecordException {
        if (destination == null || destination.trim().isEmpty()) {
            throw new InvalidRecordException("Destination cannot be empty");
        }
        this.destination = destination.trim();
    }

    public void setBaseFare(double baseFare) throws InvalidFareException {
        if (baseFare <= 0) {
            throw new InvalidFareException("Base fare must be greater than 0", baseFare);
        }
        this.baseFare = baseFare;
    }

    public double calculateFare() {
        return baseFare * (1 + fuelSurchargeRate);
    }

    public double calculateFare(boolean includeMeal) {
        if (includeMeal) {
            return calculateFare() + mealCharge;
        }
        return calculateFare();
    }

    public String compareFare(Flight other) {
        if (calculateFare() <= other.calculateFare()) {
            return flightNo;
        }
        return other.flightNo;
    }

    public void display(BufferedWriter bw) throws IOException {
        bw.write("Flight No: " + flightNo);
        bw.newLine();
        bw.write("Source: " + source);
        bw.newLine();
        bw.write("Destination: " + destination);
        bw.newLine();
        bw.write("Base Fare: " + String.format("%.2f", baseFare));
        bw.newLine();
    }

    public static String findCheapestFlight(Flight[] flights) {
        Flight cheapest = null;

        for (Flight f : flights) {
            if (f == null) {
                continue;
            }
            if (cheapest == null || f.calculateFare() < cheapest.calculateFare()) {
                cheapest = f;
            }
        }

        return cheapest == null ? "No flights available" : cheapest.flightNo;
    }
}
