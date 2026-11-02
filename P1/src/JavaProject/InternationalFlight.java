package JavaProject;

import java.io.BufferedWriter;
import java.io.IOException;

public class InternationalFlight extends Flight {
    private double airportTax;
    private double visaFee;

    public static double defaultExchangeRate = 83;

    public InternationalFlight(String flightNo, String source, String destination,
                               double baseFare, double airportTax, double visaFee)
            throws InvalidFareException, InvalidChargeException, InvalidRecordException {
        super(flightNo, source, destination, baseFare);
        setAirportTax(airportTax);
        setVisaFee(visaFee);
    }

    public double getAirportTax() {
        return airportTax;
    }

    public double getVisaFee() {
        return visaFee;
    }

    public void setAirportTax(double airportTax) throws InvalidChargeException {
        if (airportTax <= 0) {
            throw new InvalidChargeException("Airport tax must be greater than 0");
        }
        this.airportTax = airportTax;
    }

    public void setVisaFee(double visaFee) throws InvalidChargeException {
        if (visaFee <= 0) {
            throw new InvalidChargeException("Visa fee must be greater than 0");
        }
        this.visaFee = visaFee;
    }

    @Override
    public double calculateFare() {
        return super.calculateFare() + airportTax + visaFee;
    }

    public double convertFare(double exchangeRate) {
        if (exchangeRate <= 0) {
            throw new InvalidExchangeRateException(
                "Exchange rate must be greater than 0: " + exchangeRate);
        }
        return calculateFare() * exchangeRate;
    }

    @Override
    public void display(BufferedWriter bw) throws IOException {
        super.display(bw);
        bw.write("Airport Tax: " + String.format("%.2f", airportTax));
        bw.newLine();
        bw.write("Visa Fee: " + String.format("%.2f", visaFee));
        bw.newLine();
    }
}