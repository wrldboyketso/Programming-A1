import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// tests for the hospital system
public class HospitalSystemTest {

    @BeforeEach
    public void setUp() {
        HospitalSystem.resetSystem();
    }

    @Test
    public void testRegisterPatient() {
        String result = HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        assertEquals("Patient registered successfully.", result);
        assertEquals(1, HospitalSystem.patientCount);
    }

    @Test
    public void testSearchPatient() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        int index = HospitalSystem.findPatientIndex("P001");
        assertEquals(0, index);

        int notFoundIndex = HospitalSystem.findPatientIndex("P999");
        assertEquals(-1, notFoundIndex);
    }

    @Test
    public void testUpdatePatientDetails() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        String result = HospitalSystem.updatePatient("P001", "", "", "50", "", "");

        assertEquals("Patient details updated successfully.", result);
        assertEquals(50, HospitalSystem.patients[0].getAge());
        assertEquals("John", HospitalSystem.patients[0].getFirstName());
    }

    @Test
    public void testDeletePatient() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        String result = HospitalSystem.deletePatient("P001");

        assertEquals("Patient deleted successfully.", result);
        assertEquals(0, HospitalSystem.patientCount);
    }

        @Test
        public void testDeleteInpatientReleasesBed() {
                HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                                "Male", "Flu", PatientCategory.INPATIENT);
                HospitalSystem.allocateBed("P001");

                String result = HospitalSystem.deletePatient("P001");

                assertEquals("Patient deleted successfully.", result);
                assertFalse(HospitalSystem.ward[0][0].isOccupied());
                assertEquals(0, HospitalSystem.countOccupiedBeds());
        }

    @Test
    public void testAllocateBed() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.INPATIENT);

        String result = HospitalSystem.allocateBed("P001");

        assertEquals("Bed B01 allocated to John Smith.", result);
        assertTrue(HospitalSystem.ward[0][0].isOccupied());
    }

    @Test
    public void testReleaseBed() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.INPATIENT);
        HospitalSystem.allocateBed("P001");

        String result = HospitalSystem.releaseBed("P001");

        assertEquals("Bed B01 has been released.", result);
        assertFalse(HospitalSystem.ward[0][0].isOccupied());
    }

    @Test
    public void testPreventDuplicatePatientIds() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        String result = HospitalSystem.registerPatient("P001", "Jane", "Doe", 30,
                "Female", "Cold", PatientCategory.OUTPATIENT);

        assertEquals("A patient with this ID already exists. Please use a different ID.", result);
        assertEquals(1, HospitalSystem.patientCount);
    }

        @Test
        public void testPreventBlankPatientId() {
                String result = HospitalSystem.registerPatient("", "John", "Smith", 45,
                                "Male", "Flu", PatientCategory.OUTPATIENT);

                assertEquals("Patient ID cannot be blank.", result);
                assertEquals(0, HospitalSystem.patientCount);
        }

        @Test
        public void testPreventNegativeAge() {
                String result = HospitalSystem.registerPatient("P001", "John", "Smith", -1,
                                "Male", "Flu", PatientCategory.OUTPATIENT);

                assertEquals("Age cannot be negative.", result);
                assertEquals(0, HospitalSystem.patientCount);
        }

        @Test
        public void testInvalidAgeUpdateDoesNotChangePatient() {
                HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                                "Male", "Flu", PatientCategory.OUTPATIENT);

                String result = HospitalSystem.updatePatient("P001", "Jane", "", "old", "", "");

                assertEquals("Age must be a whole number. Patient details were not changed.", result);
                assertEquals("John", HospitalSystem.patients[0].getFirstName());
                assertEquals(45, HospitalSystem.patients[0].getAge());
        }

    @Test
    public void testPreventAllocatingOccupiedBedToSamePatient() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.INPATIENT);
        HospitalSystem.allocateBed("P001");

        String result = HospitalSystem.allocateBed("P001");

        assertEquals("This patient already occupies bed B01.", result);
    }

    @Test
    public void testTwoPatientsCannotShareTheSameBed() {
        HospitalSystem.registerPatient("P001", "John", "Smith", 45,
                "Male", "Flu", PatientCategory.INPATIENT);
        HospitalSystem.registerPatient("P002", "Jane", "Doe", 30,
                "Female", "Cold", PatientCategory.INPATIENT);

        HospitalSystem.allocateBed("P001");
        HospitalSystem.allocateBed("P002");

        // P001 should be in B01, and P002 should be in a different bed, B02
        assertEquals("B01", HospitalSystem.findBedByPatientId("P001").getBedNumber());
        assertEquals("B02", HospitalSystem.findBedByPatientId("P002").getBedNumber());
    }

    @Test
    public void testPreventBedAllocationWhenWardIsFull() {
        for (int i = 1; i <= 20; i++) {
            String id = String.format("P%03d", i);
            HospitalSystem.registerPatient(id, "Test", "Patient", 30,
                    "Male", "Flu", PatientCategory.INPATIENT);
            HospitalSystem.allocateBed(id);
        }

        HospitalSystem.registerPatient("P021", "Extra", "Patient", 40,
                "Male", "Flu", PatientCategory.INPATIENT);
        String result = HospitalSystem.allocateBed("P021");

        assertEquals("No beds are available. Cannot allocate a bed at this time.", result);
    }

    @Test
    public void testSortPatientsBySurname() {
        HospitalSystem.registerPatient("P003", "Amy", "Zulu", 25,
                "Female", "Cold", PatientCategory.OUTPATIENT);
        HospitalSystem.registerPatient("P001", "Ben", "Brown", 35,
                "Male", "Cough", PatientCategory.OUTPATIENT);
        HospitalSystem.registerPatient("P002", "Zane", "Adams", 40,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        HospitalSystem.sortPatientsBySurname();

        assertEquals("Adams", HospitalSystem.patients[0].getLastName());
        assertEquals("Brown", HospitalSystem.patients[1].getLastName());
        assertEquals("Zulu", HospitalSystem.patients[2].getLastName());
    }

    @Test
    public void testSortPatientsByPatientId() {
        HospitalSystem.registerPatient("P003", "Amy", "Zulu", 25,
                "Female", "Cold", PatientCategory.OUTPATIENT);
        HospitalSystem.registerPatient("P001", "Ben", "Brown", 35,
                "Male", "Cough", PatientCategory.OUTPATIENT);
        HospitalSystem.registerPatient("P002", "Zane", "Adams", 40,
                "Male", "Flu", PatientCategory.OUTPATIENT);

        HospitalSystem.sortPatientsByPatientId();

        assertEquals("P001", HospitalSystem.patients[0].getPatientId());
        assertEquals("P002", HospitalSystem.patients[1].getPatientId());
        assertEquals("P003", HospitalSystem.patients[2].getPatientId());
    }
}
