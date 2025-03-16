import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final WeatherApp weatherApp = new WeatherApp();

    public static void main(String[] args) throws IOException {
        System.out.println("\n=== 🌦️ Weather Wizard 3000 🌈 ===");
        System.out.println("Your personal weather forecasting assistant!\n");

        while (true) {
            LocalDate date = LocalDate.now();
            int choice = 0;
            boolean boo = false;

            do {
                printMenu_time();
                boo = false;
                choice = getMenuChoice();
                switch (choice) {
                    case 1 -> date = LocalDate.now();
                    case 2 -> date = LocalDate.now().plusDays(1);
                    case 3 -> date = LocalDate.now().plusDays(2);
                    case 4 -> date = LocalDate.now().plusDays(3);
                    case 5 -> date = LocalDate.now().plusDays(4);
                    case 6 -> {
                        exitProgram();
                        return;
                    }
                    default -> {
                        System.out.println("⚠️  Invalid option! Please try again.");
                        boo = true;
                    }
                }
            }while(boo);

            boo = false;
            do {
                printMenu_location();
                choice = getMenuChoice();
                switch (choice) {
                    case 1 -> boo = handleCitySearch(date);
                    case 2 -> boo = handleAutoLocation(date);
                    case 3 -> boo = handleManualCoordinates(date);
                    case 4 -> {
                        exitProgram();
                        return;
                    }
                    default -> {
                        System.out.println("⚠️  Invalid option! Please try again.");
                        boo = true;
                    }
                }
            }while(boo);

            boo = false;
            do{
                printMenu_Ai();
                choice = getMenuChoice();
                switch(choice){
                    case 1 -> System.out.println(weatherApp.AiSuggestion());
                    case 2 -> boo = false;
                    default -> {
                        System.out.println("⚠️  Invalid option! Please try again.");
                        boo = true;
                    }
                }
            }while(boo);
        }
    }

    private static void printMenu_Ai(){
        System.out.println("Do you want our Ai Weather Wizard 3000 help you to dress properly in this weather ?");
        System.out.println("1. yes");
        System.out.println("2. no");
    }

    private static void printMenu_time() {
        LocalDate today = LocalDate.now();
        System.out.println("\n-------- Main Menu --------");
        System.out.println("1. Today");
        System.out.println("2. Tomorrow");
        System.out.println("3. " + today.plusDays(2));
        System.out.println("4. " + today.plusDays(3));
        System.out.println("5. " + today.plusDays(4));
        System.out.println("6. Exit");
        System.out.print("Enter your choice (1-6): ");
    }

    private static void printMenu_location() {
        System.out.println("\nChoose one of the following search methods:");
        System.out.println("1. Search by city name");
        System.out.println("2. Use my current location");
        System.out.println("3. Enter coordinates manually");
        System.out.println("4. Exit");
        System.out.print("Enter your choice (1-4): ");
    }

    private static int getMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean handleCitySearch(LocalDate targetDate) {
        System.out.print("\nEnter city name (e.g., Tokyo): ");
        String city = scanner.nextLine().trim();

        try {
            weatherApp.fetchWeatherDatawWithCityName(city, targetDate);
            System.out.println("\n" + weatherApp.getWeatherMessage());
            return false;
        } catch (Exception e) {
            System.out.println("⛈️  Failed to fetch data: " + e.getMessage());
            return true;
        }
    }

    private static boolean handleAutoLocation(LocalDate targetDate) {
        System.out.println("\n🔍 Detecting your location...");
        try {
            double[] coords = WeatherApp.getAutoLocation();
            System.out.printf("📍 Detected coordinates: %.4f, %.4f%n", coords[0], coords[1]);
            weatherApp.fetchWeatherData(coords[0], coords[1], targetDate);
            System.out.println("\n" + weatherApp.getWeatherMessage());
            return false;
        } catch (Exception e) {
            System.out.println("🌩️  Location detection failed: " + e.getMessage());
            return true;
        }
    }

    private static boolean handleManualCoordinates(LocalDate targetDate) {
        try {
            System.out.print("\nEnter latitude (-90 to 90): ");
            double lat = parseCoordinate("Latitude");

            System.out.print("Enter longitude (-180 to 180): ");
            double lon = parseCoordinate("Longitude");

            if (isValidCoordinate(lat, -90, 90) && isValidCoordinate(lon, -180, 180)) {
                weatherApp.fetchWeatherData(lat, lon, targetDate);
                System.out.println("\n" + weatherApp.getWeatherMessage());
            } else {
                System.out.println("❌  Invalid coordinates! Values out of range.");
            }
            return false;
        } catch (Exception e) {
            System.out.println("🌧️  Error: " + e.getMessage());
            return true;
        }
    }

    private static double parseCoordinate(String type) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid " + type.toLowerCase() + "! Please enter a valid number: ");
            }
        }
    }

    private static boolean isValidCoordinate(double value, double min, double max) {
        return value >= min && value <= max;
    }


    private static void exitProgram() {
        System.out.println("\n✨ Thank you for using Weather Wizard 3000!");
        System.out.println("☁️  Stay dry and have a wonderful day!\n");
        scanner.close();
    }


}