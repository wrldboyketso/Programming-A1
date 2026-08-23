import java.util.Scanner;
import java.util.InputMismatchException;

// main class - handles patients and beds
public class HospitalSystem {

    static Patient[] patients = new Patient[100];
    static int patientCount = 0;

    static final int WARD_ROWS = 4;
    static final int WARD_COLUMNS = 5;
    static Bed[][] ward = new Bed[WARD_ROWS][WARD_COLUMNS];

    static Scanner input = new Scanner(System.in);

    // asks for a whole number, and keeps asking if the input is not a number
    public static int readInt(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = input.nextInt();
                input.nextLine();
                return value;
            } catch (InputMismatchException mistake) {
                System.out.println("Please enter a whole number.");
                input.nextLine(); // clear the bad input so we can ask again
            }
        }
    }

    public static void main(String[] args) {

        initialiseWard();

        int choice;

        do {
            System.out.println();
            System.out.println("===== MediCare Hospital - Main Menu =====");
            System.out.println("1. Register New Patient");
            System.out.println("2. Search Patient");
            System.out.println("3. Update Patient");
            System.out.println("4. Delete Patient");
            System.out.println("5. Display All Patients");
            System.out.println("6. Allocate Bed to Inpatient");
            System.out.println("7. Release Bed (Discharge Patient)");
            System.out.println("8. Display Ward Layout");
            System.out.println("9. Display Available Beds");
            System.out.println("10. Display Occupied Beds");
            System.out.println("11. Reports Menu");
            System.out.println("0. Exit");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    searchPatient();
                    break;
                case 3:
                    updatePatient();
                    break;
                case 4:
                    deletePatient();
                    break;
                case 5:
                    displayAllPatients();
                    break;
                case 6:
                    allocateBed();
                    break;
                case 7:
                    releaseBed();
                    break;
                case 8:
                    displayWardLayout();
                    break;
                case 9:
                    displayAvailableBeds();
                    break;
                case 10:
                    displayOccupiedBeds();
                    break;
                case 11:
                    reportsMenu();
                    break;
                case 0:
                    System.out.println("Exiting program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }

        } while (choice != 0);

        input.close();
    }

    // makes the 20 beds, B01 to B20
    public static void initialiseWard() {
        int bedNumber = 1;
        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                String label = String.format("B%02d", bedNumber);
                ward[row][col] = new Bed(label);
                bedNumber++;
            }
        }
    }

    // reads new patient info from console
    public static void registerPatient() {

        if (patientCount >= patients.length) {
            System.out.println("Cannot register more patients. The system is full.");
            return;
        }

        System.out.print("Enter Patient ID: ");
        String id = input.nextLine();

        System.out.print("Enter First Name: ");
        String firstName = input.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = input.nextLine();

        int age = readInt("Enter Age: ");

        System.out.print("Enter Gender: ");
        String gender = input.nextLine();

        System.out.print("Enter Medical Condition: ");
        String condition = input.nextLine();

        System.out.println("Select Patient Category:");
        System.out.println("1. Inpatient");
        System.out.println("2. Outpatient");
        System.out.println("3. Emergency");
        int catChoice = readInt("Enter choice: ");

        PatientCategory category;
        if (catChoice == 1) {
            category = PatientCategory.INPATIENT;
        } else if (catChoice == 2) {
            category = PatientCategory.OUTPATIENT;
        } else {
            category = PatientCategory.EMERGENCY;
        }

        // this part does the actual work, so tests can call it too
        String result = registerPatient(id, firstName, lastName, age, gender, condition, category);
        System.out.println(result);
    }

    // does the register logic, used by console and by tests
    public static String registerPatient(String id, String firstName, String lastName, int age,
                                          String gender, String medicalCondition, PatientCategory category) {

        if (patientCount >= patients.length) {
            return "Cannot register more patients. The system is full.";
        }

        if (id == null || id.trim().isEmpty()) {
            return "Patient ID cannot be blank.";
        }

        if (age < 0) {
            return "Age cannot be negative.";
        }

        if (findPatientIndex(id) != -1) {
            return "A patient with this ID already exists. Please use a different ID.";
        }

        // inpatients get the extra ward/bed fields, others don't
        Patient newPatient;
        if (category == PatientCategory.INPATIENT) {
            newPatient = new Inpatient(id, firstName, lastName, age, gender, medicalCondition,
                    category, 1, "Not Allocated");
        } else {
            newPatient = new Patient(id, firstName, lastName, age, gender, medicalCondition, category);
        }

        patients[patientCount] = newPatient;
        patientCount++;

        return "Patient registered successfully.";
    }

    // finds a patient by id and shows it
    public static void searchPatient() {

        System.out.print("Enter Patient ID to search: ");
        String id = input.nextLine();

        int index = findPatientIndex(id);

        if (index == -1) {
            System.out.println("Patient not found.");
        } else {
            System.out.println("Patient found:");
            patients[index].displayDetails();
        }
    }

    // reads new values from console for updating a patient
    public static void updatePatient() {

        System.out.print("Enter Patient ID to update: ");
        String id = input.nextLine();

        int index = findPatientIndex(id);

        if (index == -1) {
            System.out.println("Patient not found.");
            return;
        }

        Patient p = patients[index];

        System.out.println("Leave a field blank and press Enter to keep the current value.");

        System.out.print("Enter new First Name (" + p.getFirstName() + "): ");
        String firstName = input.nextLine();

        System.out.print("Enter new Last Name (" + p.getLastName() + "): ");
        String lastName = input.nextLine();

        System.out.print("Enter new Age (" + p.getAge() + "): ");
        String ageInput = input.nextLine();

        System.out.print("Enter new Gender (" + p.getGender() + "): ");
        String gender = input.nextLine();

        System.out.print("Enter new Medical Condition (" + p.getMedicalCondition() + "): ");
        String condition = input.nextLine();

        // this part does the actual work, so tests can call it too
        String result = updatePatient(id, firstName, lastName, ageInput, gender, condition);
        System.out.println(result);
    }

    // does the update logic. leave a field blank to keep it the same
    public static String updatePatient(String id, String firstName, String lastName,
                                        String ageInput, String gender, String condition) {

        int index = findPatientIndex(id);

        if (index == -1) {
            return "Patient not found.";
        }

        Patient p = patients[index];

        int newAge = p.getAge();
        if (!ageInput.isEmpty()) {
            try {
                newAge = Integer.parseInt(ageInput);
            } catch (NumberFormatException mistake) {
                return "Age must be a whole number. Patient details were not changed.";
            }

            if (newAge < 0) {
                return "Age cannot be negative. Patient details were not changed.";
            }
        }

        if (!firstName.isEmpty()) {
            p.setFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            p.setLastName(lastName);
        }
        if (!ageInput.isEmpty()) {
            p.setAge(newAge);
        }
        if (!gender.isEmpty()) {
            p.setGender(gender);
        }
        if (!condition.isEmpty()) {
            p.setMedicalCondition(condition);
        }

        return "Patient details updated successfully.";
    }

    // reads a patient id from console and deletes them
    public static void deletePatient() {

        System.out.print("Enter Patient ID to delete: ");
        String id = input.nextLine();

        String result = deletePatient(id);
        System.out.println(result);
    }

    // does the delete logic
    public static String deletePatient(String id) {

        int index = findPatientIndex(id);

        if (index == -1) {
            return "Patient not found.";
        }

        Bed bed = findBedByPatientId(id);
        if (bed != null) {
            bed.setOccupied(false);
            bed.setPatientId(null);
        }

        // shift everyone after this one back by one spot
        for (int i = index; i < patientCount - 1; i++) {
            patients[i] = patients[i + 1];
        }

        patients[patientCount - 1] = null;
        patientCount--;

        return "Patient deleted successfully.";
    }

    // shows every patient
    public static void displayAllPatients() {

        if (patientCount == 0) {
            System.out.println("No patients registered yet.");
            return;
        }

        System.out.println("===== All Registered Patients =====");
        for (int i = 0; i < patientCount; i++) {
            patients[i].displayDetails();
            System.out.println("------------------------------");
        }
    }

    // returns the array index for a patient id, or -1 if not found
    public static int findPatientIndex(String id) {
        for (int i = 0; i < patientCount; i++) {
            if (patients[i].getPatientId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    // reads a patient id from console and gives them a bed
    public static void allocateBed() {

        System.out.print("Enter Patient ID: ");
        String id = input.nextLine();

        String result = allocateBed(id);
        System.out.println(result);
    }

    // does the allocate bed logic
    public static String allocateBed(String id) {

        int patientIndex = findPatientIndex(id);

        if (patientIndex == -1) {
            return "Patient not found.";
        }

        Patient p = patients[patientIndex];

        if (p.getCategory() != PatientCategory.INPATIENT) {
            return "Only Inpatients may be allocated a hospital bed.";
        }

        Bed existingBed = findBedByPatientId(id);
        if (existingBed != null) {
            return "This patient already occupies bed " + existingBed.getBedNumber() + ".";
        }

        // find the first free bed
        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                Bed bed = ward[row][col];
                if (!bed.isOccupied()) {
                    bed.setOccupied(true);
                    bed.setPatientId(id);

                    Inpatient inpatient = (Inpatient) p;
                    inpatient.setBedNumber(bed.getBedNumber());

                    return "Bed " + bed.getBedNumber() + " allocated to " + p.getFirstName() + " " + p.getLastName() + ".";
                }
            }
        }

        return "No beds are available. Cannot allocate a bed at this time.";
    }

    // reads a patient id from console and frees their bed
    public static void releaseBed() {

        System.out.print("Enter Patient ID to discharge: ");
        String id = input.nextLine();

        String result = releaseBed(id);
        System.out.println(result);
    }

    // does the release bed logic
    public static String releaseBed(String id) {

        Bed bed = findBedByPatientId(id);

        if (bed == null) {
            return "This patient does not currently occupy a bed.";
        }

        String bedNumber = bed.getBedNumber();
        bed.setOccupied(false);
        bed.setPatientId(null);

        int patientIndex = findPatientIndex(id);
        if (patientIndex != -1) {
            Inpatient inpatient = (Inpatient) patients[patientIndex];
            inpatient.setBedNumber("Not Allocated");
        }

        return "Bed " + bedNumber + " has been released.";
    }

    // shows the 4x5 ward grid
    public static void displayWardLayout() {
        System.out.println("===== Ward Layout =====");
        System.out.println("(X = occupied, - = available)");
        System.out.println();

        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                Bed bed = ward[row][col];
                String status = bed.isOccupied() ? "X" : "-";
                System.out.print(bed.getBedNumber() + "[" + status + "]  ");
            }
            System.out.println();
        }
    }

    // shows only free beds
    public static void displayAvailableBeds() {
        System.out.println("===== Available Beds =====");
        boolean foundAny = false;

        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                Bed bed = ward[row][col];
                if (!bed.isOccupied()) {
                    System.out.println(bed.getBedNumber());
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("No beds are currently available.");
        }
    }

    // shows only occupied beds and who is in them
    public static void displayOccupiedBeds() {
        System.out.println("===== Occupied Beds =====");
        boolean foundAny = false;

        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                Bed bed = ward[row][col];
                if (bed.isOccupied()) {
                    String patientName = getPatientNameById(bed.getPatientId());
                    System.out.println(bed.getBedNumber() + " - Patient ID: " + bed.getPatientId() + " (" + patientName + ")");
                    foundAny = true;
                }
            }
        }

        if (!foundAny) {
            System.out.println("No beds are currently occupied.");
        }
    }

    // finds the bed a patient is in, or null
    public static Bed findBedByPatientId(String patientId) {
        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                Bed bed = ward[row][col];
                if (bed.isOccupied() && bed.getPatientId().equals(patientId)) {
                    return bed;
                }
            }
        }
        return null;
    }

    // gets a patient's full name from their id
    public static String getPatientNameById(String patientId) {
        int index = findPatientIndex(patientId);
        if (index == -1) {
            return "Unknown";
        }
        return patients[index].getFirstName() + " " + patients[index].getLastName();
    }

    // ===== reports =====

    public static void reportsMenu() {

        int choice;

        do {
            System.out.println();
            System.out.println("===== Reports Menu =====");
            System.out.println("1. Display All Patients");
            System.out.println("2. Display Available Beds");
            System.out.println("3. Display Occupied Beds");
            System.out.println("4. Display Total Number of Registered Patients");
            System.out.println("5. Display Total Number of Occupied Beds");
            System.out.println("6. Display Ward Occupancy Percentage");
            System.out.println("7. Sort Patients by Surname");
            System.out.println("8. Sort Patients by Patient ID");
            System.out.println("0. Back to Main Menu");

            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    displayAllPatients();
                    break;
                case 2:
                    displayAvailableBeds();
                    break;
                case 3:
                    displayOccupiedBeds();
                    break;
                case 4:
                    displayTotalPatients();
                    break;
                case 5:
                    displayTotalOccupiedBeds();
                    break;
                case 6:
                    displayOccupancyPercentage();
                    break;
                case 7:
                    sortPatientsBySurname();
                    break;
                case 8:
                    sortPatientsByPatientId();
                    break;
                case 0:
                    System.out.println("Returning to Main Menu.");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }

        } while (choice != 0);
    }

    public static void displayTotalPatients() {
        System.out.println("Total registered patients: " + patientCount);
    }

    public static void displayTotalOccupiedBeds() {
        int occupiedCount = countOccupiedBeds();
        System.out.println("Total occupied beds: " + occupiedCount);
    }

    public static void displayOccupancyPercentage() {
        int occupiedCount = countOccupiedBeds();
        int totalBeds = WARD_ROWS * WARD_COLUMNS;
        double percentage = (occupiedCount * 100.0) / totalBeds;
        System.out.printf("Ward occupancy: %.1f%%%n", percentage);
    }

    public static int countOccupiedBeds() {
        int occupiedCount = 0;
        for (int row = 0; row < WARD_ROWS; row++) {
            for (int col = 0; col < WARD_COLUMNS; col++) {
                if (ward[row][col].isOccupied()) {
                    occupiedCount++;
                }
            }
        }
        return occupiedCount;
    }

    // bubble sort by surname, same way as in the textbook
    public static void sortPatientsBySurname() {

        int a, b;
        Patient temp;
        int highSubscript = patientCount - 1;

        for (a = 0; a < highSubscript; ++a) {
            for (b = 0; b < highSubscript; ++b) {
                if (patients[b].getLastName().compareToIgnoreCase(patients[b + 1].getLastName()) > 0) {
                    temp = patients[b];
                    patients[b] = patients[b + 1];
                    patients[b + 1] = temp;
                }
            }
        }

        System.out.println("Patients sorted by surname.");
        displayAllPatients();
    }

    // same bubble sort, but by patient id
    public static void sortPatientsByPatientId() {

        int a, b;
        Patient temp;
        int highSubscript = patientCount - 1;

        for (a = 0; a < highSubscript; ++a) {
            for (b = 0; b < highSubscript; ++b) {
                if (patients[b].getPatientId().compareToIgnoreCase(patients[b + 1].getPatientId()) > 0) {
                    temp = patients[b];
                    patients[b] = patients[b + 1];
                    patients[b + 1] = temp;
                }
            }
        }

        System.out.println("Patients sorted by Patient ID.");
        displayAllPatients();
    }

    // resets everything, used before each test
    public static void resetSystem() {
        patients = new Patient[100];
        patientCount = 0;
        initialiseWard();
    }
}
