package com.capstoneproject.ElitesTracker.services.implementation;

import com.capstoneproject.ElitesTracker.dtos.requests.SearchRequest;
import com.capstoneproject.ElitesTracker.dtos.responses.AttendanceSheetResponse;
import com.capstoneproject.ElitesTracker.exceptions.EntityDoesNotExistException;
import com.capstoneproject.ElitesTracker.exceptions.RecordNotFoundException;
import com.capstoneproject.ElitesTracker.models.Attendance;
import com.capstoneproject.ElitesTracker.models.EliteUser;
import com.capstoneproject.ElitesTracker.services.interfaces.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.capstoneproject.ElitesTracker.enums.ExceptionMessages.RECORD_NOT_FOUND_EXCEPTION;
import static com.capstoneproject.ElitesTracker.utils.AppUtil.*;
import static com.capstoneproject.ElitesTracker.utils.HardCoded.SPACE;

@Service
@AllArgsConstructor
public class EliteSearchService implements SearchService {
    private EliteAttendanceService eliteAttendanceService;

    @Override
    public List<AttendanceSheetResponse> searchAttendanceReportForSelf(SearchRequest request, EliteUser foundUser){
        return getAttendanceSheetResponses(request, foundUser);
    }

    @Override
    public List<AttendanceSheetResponse> searchAttendanceReportForNative(SearchRequest request, EliteUser foundUser) {
        if(!foundUser.getCohort().equals(request.getCohort())){
            throw new EntityDoesNotExistException(nativeNotFoundMessage(foundUser.getCohort()));
        }
        return getAttendanceSheetResponses(request, foundUser);
    }

    @Override
    public List<AttendanceSheetResponse> searchAttendanceReportForCohort(SearchRequest request) {
        List<Attendance> attendanceList = eliteAttendanceService.findAllAttendances();
        return buildAttendanceSheetForCohort(request, attendanceList);
    }

    private static List<AttendanceSheetResponse> buildAttendanceSheetForCohort(SearchRequest request, List<Attendance> attendanceList) {
        String startDate = stringDateToString(request.getStartDate());
        String endDate = stringDateToString(request.getEndDate());
        List<AttendanceSheetResponse> attendanceSheet = new ArrayList<>();

        int counter = 1;
        for (int i = 0; i < attendanceList.size(); i++) {
            boolean isMatch = isValidRange(attendanceList, startDate, endDate, i) && (request.getCohort().equals(attendanceList.get(i).getCohort()));
            buildAttendanceReport(attendanceList, attendanceSheet, i, isMatch, counter);
            counter++;
        }

        if(attendanceSheet.isEmpty()){
            throw new RecordNotFoundException(RECORD_NOT_FOUND_EXCEPTION.getMessage());
        }
        return attendanceSheet;
    }

    private List<AttendanceSheetResponse> getAttendanceSheetResponses(SearchRequest request, EliteUser foundUser) {
        List<Attendance> attendanceList = eliteAttendanceService.findAllAttendances();

        if(attendanceList.isEmpty()) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_EXCEPTION.getMessage());
        }

        List<AttendanceSheetResponse> attendanceSheet = buildAttendanceLogForSelf(request, foundUser, attendanceList);

        if(attendanceSheet.isEmpty()){
            throw new RecordNotFoundException(RECORD_NOT_FOUND_EXCEPTION.getMessage());
        }
        return attendanceSheet;
    }

    private static List<AttendanceSheetResponse> buildAttendanceLogForSelf(SearchRequest request, EliteUser foundUser, List<Attendance> attendanceList) {
        String startDate = stringDateToString(request.getStartDate());
        String endDate = stringDateToString(request.getEndDate());

        List<AttendanceSheetResponse> attendanceSheet = new ArrayList<>();
        int counter = 1;

        for (int i = 0; i < attendanceList.size(); i++) {
            if ((foundUser.getCohort().equals(attendanceList.get(i).getUser().getCohort())) && (foundUser.getId().equals(attendanceList.get(i).getUser().getId()))){
                boolean isMatch = isValidRange(attendanceList, startDate, endDate, i);
                buildAttendanceReport(attendanceList, attendanceSheet, i, isMatch, counter);
                counter++;
            }
        }
        return attendanceSheet;
    }

    private static void buildAttendanceReport(List<Attendance> attendanceList, List<AttendanceSheetResponse> attendanceSheet, int i, boolean isMatch, int counter) {
        if(isMatch){
            AttendanceSheetResponse foundReport = AttendanceSheetResponse.builder()
                    .serialNumber(String.valueOf(counter))
                    .firstName(attendanceList.get(i).getUser().getFirstName())
                    .lastName(attendanceList.get(i).getUser().getLastName())
                    .cohort(attendanceList.get(i).getCohort())
                    .attendanceStatus(attendanceList.get(i).getStatus().toString())
                    .date(attendanceList.get(i).getDateTaken()+SPACE+attendanceList.get(i).getTimeTaken())
                    .build();
            attendanceSheet.add(foundReport);
        }
    }

    private static boolean isValidRange(List<Attendance> attendanceList, String startDate, String endDate, int i) {
        return  (stringToLocalDate(subStringDate(attendanceList.get(i).getDateTaken())).equals(stringToLocalDate(startDate))
                || stringToLocalDate(subStringDate(attendanceList.get(i).getDateTaken())).isAfter(stringToLocalDate(startDate)))
                && (stringToLocalDate(subStringDate(attendanceList.get(i).getDateTaken())).equals(stringToLocalDate(endDate))
                || stringToLocalDate(subStringDate(attendanceList.get(i).getDateTaken())).isBefore(stringToLocalDate(endDate)));
    }
}
