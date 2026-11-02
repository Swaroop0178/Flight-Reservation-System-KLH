package JavaProject;

import java.io.*;
import java.util.Date;

public class Main {

    static int recordsRead = 0;
    static int accepted = 0;
    static int rejected = 0;

    public static void main(String[] args) {
        File inputFile = new File("input/flights.txt");
        File outputFolder = new File("output");

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        try {
            checkInputFile(inputFile);

            try (
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter report = new BufferedWriter(
                        new FileWriter(new File("output/farequotation.txt")));
                BufferedWriter errors = new BufferedWriter(
                        new FileWriter(new File("output/errors.txt"), true));
            ) {
                errors.write("Run time: " + new Date());
                errors.newLine();

                String firstLine = br.readLine();

                if (firstLine == null || firstLine.trim().isEmpty()) {
                    throw new EmptyInputFileException("Input file has no records");
                }

                int n;
                try {
                    n = Integer.parseInt(firstLine.trim());
                } catch (NumberFormatException e) {
                    throw new InvalidRecordException("First line N is not a number", e);
                }

                if (n <= 0) {
                    throw new EmptyInputFileException("Input file has zero records");
                }

                Flight[] flights = new Flight[n];
                boolean[] mealFlags = new boolean[n];
                double[] exchangeRates = new double[n];
                int count = 0;

                String line;
                int fileLineNo = 1;

                while ((line = br.readLine()) != null) {
                    fileLineNo++;

                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    recordsRead++;

                    try {
                        String[] parts = line.split(",", -1);

                        for (int i = 0; i < parts.length; i++) {
                            parts[i] = parts[i].trim();
                        }

                        String type = parts[0];

                        if (type.equals("D")) {
                            if (parts.length != 6) {
                                throw new InvalidRecordException(
                                    "Domestic flight record must have 6 fields");
                            }

                            boolean includeMeal = Boolean.parseBoolean(parts[5]);

                            Flight f = new Flight(
                                    parts[1], parts[2], parts[3],
                                    Double.parseDouble(parts[4]));

                            flights[count] = f;
                            mealFlags[count] = includeMeal;
                            exchangeRates[count] = 0;
                            count++;
                            accepted++;

                        } else if (type.equals("I")) {
                            if (parts.length != 9) {
                                throw new InvalidRecordException(
                                    "International flight record must have 9 fields");
                            }

                            boolean includeMeal = Boolean.parseBoolean(parts[7]);

                            double rate;
                            if (parts[8].isEmpty()) {
                                rate = InternationalFlight.defaultExchangeRate;
                            } else {
                                rate = Double.parseDouble(parts[8]);
                            }

                            InternationalFlight f = new InternationalFlight(
                                    parts[1], parts[2], parts[3],
                                    Double.parseDouble(parts[4]),
                                    Double.parseDouble(parts[5]),
                                    Double.parseDouble(parts[6]));

                            // This validates exchange rate for this record.
                            f.convertFare(rate);

                            flights[count] = f;
                            mealFlags[count] = includeMeal;
                            exchangeRates[count] = rate;
                            count++;
                            accepted++;

                        } else {
                            throw new InvalidRecordException("Unknown flight type: " + type);
                        }

                    } catch (InvalidFareException e) {
                        rejected++;
                        logError(errors, fileLineNo, line, e);
                    } catch (InvalidChargeException e) {
                        rejected++;
                        logError(errors, fileLineNo, line, e);
                    } catch (InvalidExchangeRateException e) {
                        rejected++;
                        logError(errors, fileLineNo, line, e);
                    } catch (InvalidRecordException e) {
                        rejected++;
                        logError(errors, fileLineNo, line, e);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        rejected++;
                        InvalidRecordException wrapped =
                                new InvalidRecordException("Invalid record format", e);
                        logError(errors, fileLineNo, line, wrapped);
                    }
                }

                writeReport(report, flights, mealFlags, exchangeRates, count);

            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + e.getMessage());
        } catch (EmptyInputFileException e) {
            System.out.println("Input file error: " + e.getMessage());
        } catch (InvalidRecordException e) {
            System.out.println("Invalid input file: " + e.getMessage());
        }

        System.out.println("Processing complete. See output folder.");
    }

    static void checkInputFile(File file)
            throws FileNotFoundException, EmptyInputFileException {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            throw new FileNotFoundException("Cannot read input file: " + file.getPath());
        }

        if (file.length() == 0) {
            throw new EmptyInputFileException("Input file is empty");
        }
    }

    static void logError(BufferedWriter errors, int lineNo,
                         String record, Exception e) throws IOException {
        errors.write("Line " + lineNo + " | " + record + " | "
                + e.getClass().getSimpleName() + " | " + e.getMessage());
        errors.newLine();
        errors.flush();
    }

    static void writeReport(BufferedWriter report, Flight[] flights,
                            boolean[] mealFlags, double[] exchangeRates,
                            int count) throws IOException {

        report.write("===== FLIGHT FARE QUOTATION REPORT =====");
        report.newLine();
        report.newLine();

        for (int i = 0; i < count; i++) {
            Flight f = flights[i];

            report.write("----- Flight " + (i + 1) + " -----");
            report.newLine();

            f.display(report);

            double normalFare = f.calculateFare();
            report.write("Normal Fare: " + String.format("%.2f", normalFare));
            report.newLine();

            if (mealFlags[i]) {
                double mealFare = f.calculateFare(true);
                report.write("Fare With Meal: " + String.format("%.2f", mealFare));
                report.newLine();
            }

            if (f instanceof InternationalFlight) {
                InternationalFlight intl = (InternationalFlight) f;
                double converted = intl.convertFare(exchangeRates[i]);
                report.write("Converted Fare at rate "
                        + exchangeRates[i] + ": "
                        + String.format("%.2f", converted));
                report.newLine();
            }

            report.newLine();
        }

        if (count >= 2) {
            report.write("Cheaper flight between first two flights: "
                    + flights[0].compareFare(flights[1]));
            report.newLine();
        }

        report.write("Cheapest Flight: "
                + Flight.findCheapestFlight(flights));
        report.newLine();

        report.write("Total Flights Created: " + Flight.flightCount);
        report.newLine();

        report.write("Records read " + recordsRead
                + ", accepted " + accepted
                + ", rejected " + rejected + ".");
        report.newLine();

        report.flush();
    }
}